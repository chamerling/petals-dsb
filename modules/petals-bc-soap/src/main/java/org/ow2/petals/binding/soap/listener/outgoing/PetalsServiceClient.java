/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.outgoing;

import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.ow2.petals.binding.soap.SoapProvideExtFlowStepBeginLogData;
import org.ow2.petals.commons.logger.AbstractFlowLogData;
import org.ow2.petals.commons.logger.ProvideExtFlowStepEndLogData;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ebmwebsourcing.easycommons.logger.Level;
import com.ebmwebsourcing.easycommons.thread.ExecutionContext;

/**
 * A {@link org.apache.axis2.client.ServiceClient} extension to be abble to get
 * the entire SOAPBody of the service response. This is because Axis2 only
 * returns the first element of the {@link SOAPBody} which will not work with
 * multiref responses.
 * 
 * @see https://issues.apache.org/jira/browse/AXIS2-2408
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class PetalsServiceClient extends org.apache.axis2.client.ServiceClient {

    public Logger logger = null;

    /**
     * Creates a new instance of ServiceClient
     * 
     * @param configContext
     * @param axisService
     * @throws AxisFault
     */
    public PetalsServiceClient(final ConfigurationContext configContext,
            final AxisService axisService) throws AxisFault {
        super(configContext, axisService);
    }

    /**
     * Creates a new instance of ServiceClient
     * 
     * @param configContext
     * @param wsdlURL
     * @param wsdlServiceName
     * @param portName
     * @throws AxisFault
     */
    public PetalsServiceClient(final ConfigurationContext configContext, final URL wsdlURL,
            final QName wsdlServiceName, final String portName) throws AxisFault {
        super(configContext, wsdlURL, wsdlServiceName, portName);
    }

    private void addAdditionalHeadersToEnvelope(final SOAPEnvelope envelope,
            final Map<String, DocumentFragment> headers) {
        final SOAPHeader soapHeader = envelope.getHeader();

        for (final DocumentFragment docfrag : headers.values()) {
            final Node node = docfrag.getFirstChild();
            if (node instanceof Element) {
                try {
                    soapHeader.addChild(XMLUtils.toOM((Element) node));
                } catch (final Throwable t) {
                    // try the next one...
                }
            }
        }
    }

    protected void fillSOAPEnvelope(final MessageContext messageContext,
            final OMElement xmlPayload, final Map<String, DocumentFragment> headers)
            throws AxisFault {
        messageContext.setServiceContext(getServiceContext());

        final SOAPFactory soapFactory = getSOAPFactory();
        final SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();

        if (xmlPayload != null) {
            envelope.getBody().addChild(xmlPayload);
        }
        addHeadersToEnvelope(envelope);

        if (headers != null) {
            addAdditionalHeadersToEnvelope(envelope, headers);
        }
        messageContext.setEnvelope(envelope);
    }

    protected SOAPFactory getSOAPFactory() {
        final String soapVersionURI = getOptions().getSoapVersionURI();

        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            // make the SOAP 1.1 the default SOAP version
            return OMAbstractFactory.getSOAP11Factory();
        }
    }

    public MessageContext sendReceive(QName operationQName, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        OperationClient operationClient = createOperationClient(operationQName, xmlPayload, headers);

        operationClient.execute(true);

        final MessageContext response = operationClient
                .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        this.logger
                .log(Level.MONIT,
                        "",
                new ProvideExtFlowStepEndLogData(ExecutionContext.getProperties()
                                .getProperty(AbstractFlowLogData.FLOW_INSTANCE_ID_PROPERTY_NAME),
                                ExecutionContext.getProperties().getProperty(
                                        AbstractFlowLogData.FLOW_STEP_ID_PROPERTY_NAME)));
        return response;
    }

    public void fireAndForget(QName operationQName, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        OperationClient operationClient = createOperationClient(operationQName, xmlPayload, headers);
        // operationClient.getOperationContext().gete
        operationClient.execute(false);
    }

    public void sendRobust(QName operationQName, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        OperationClient operationClient = createOperationClient(operationQName, xmlPayload, headers);

        operationClient.execute(true);
    }

    private OperationClient createOperationClient(QName operationQName, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        // look up the appropriate axis operation and create the client
        OperationClient operationClient = createClient(operationQName);

        // create a message context and put the payload in there along with any
        // headers
        MessageContext messageContext = new MessageContext();

        fillSOAPEnvelope(messageContext, xmlPayload, headers);

        // add the message context there and have it go
        operationClient.addMessageContext(messageContext);
        this.logger.log(
                Level.MONIT,
                "",
                new SoapProvideExtFlowStepBeginLogData(ExecutionContext.getProperties()
                        .getProperty(AbstractFlowLogData.FLOW_INSTANCE_ID_PROPERTY_NAME),
                        ExecutionContext.getProperties().getProperty(
                                AbstractFlowLogData.FLOW_STEP_ID_PROPERTY_NAME), getOptions()
                                .getTo().getAddress()));
        return operationClient;
    }

    public final void setLogger(Logger logger) {
        this.logger = logger;
    }
}
