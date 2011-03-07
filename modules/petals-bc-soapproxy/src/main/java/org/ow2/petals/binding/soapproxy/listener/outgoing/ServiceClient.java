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
package org.ow2.petals.binding.soapproxy.listener.outgoing;

import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;
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
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
public class ServiceClient extends org.apache.axis2.client.ServiceClient {

    /**
     * Creates a new instance of ServiceClient
     * 
     * @param configContext
     * @param axisService
     * @throws AxisFault
     */
    public ServiceClient(ConfigurationContext configContext, AxisService axisService)
            throws AxisFault {
        super(configContext, axisService);
    }

    /**
     * Creates a new instance of ServiceClient
     * 
     * @param configContext
     * @param wsdl4jDefinition
     * @param wsdlServiceName
     * @param portName
     * @throws AxisFault
     */
    public ServiceClient(ConfigurationContext configContext, Definition wsdl4jDefinition,
            QName wsdlServiceName, String portName) throws AxisFault {
        super(configContext, wsdl4jDefinition, wsdlServiceName, portName);
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
    public ServiceClient(ConfigurationContext configContext, URL wsdlURL, QName wsdlServiceName,
            String portName) throws AxisFault {
        super(configContext, wsdlURL, wsdlServiceName, portName);
    }

    
    /**
     * Send / receive a message and add some SOAP header values...
     * 
     * @param operationQName
     * @param xmlPayload
     * @param headers
     * @return
     * @throws AxisFault
     */
    public SOAPBody sendReceiveBody(QName operationQName, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        MessageContext messageContext = new MessageContext();
        fillSOAPEnvelope(messageContext, xmlPayload, headers);
        OperationClient operationClient = createClient(operationQName);
        operationClient.addMessageContext(messageContext);
        operationClient.execute(true);
        MessageContext response = operationClient
                .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

        response.getEnvelope().build();
        return response.getEnvelope().getBody();

    }
    
    /**
     * 
     * @param messageContext
     * @param xmlPayload
     * @param headers
     * @throws AxisFault
     */
    protected void fillSOAPEnvelope(MessageContext messageContext, OMElement xmlPayload,
            Map<String, DocumentFragment> headers) throws AxisFault {
        messageContext.setServiceContext(getServiceContext());
        SOAPFactory soapFactory = getSOAPFactory();
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        if (xmlPayload != null) {
            envelope.getBody().addChild(xmlPayload);
        }
        addHeadersToEnvelope(envelope);
        if (headers != null) {
            addAdditionalHeadersToEnvelope(envelope, headers);
        }
        messageContext.setEnvelope(envelope);
    }
    
    /**
     * 
     * @param envelope
     * @param headers
     */
    private void addAdditionalHeadersToEnvelope(SOAPEnvelope envelope,
            Map<String, DocumentFragment> headers) {
        SOAPHeader soapHeader = envelope.getHeader();
        for (DocumentFragment docfrag : headers.values()) {
            final Node node = docfrag.getFirstChild();
            if (node instanceof Element) {
                try {
                    soapHeader.addChild(XMLUtils.toOM((Element) node));
                } catch (Exception e) {
                    // try the next one...
                }
            }
        }
    }

    /**
     * 
     * @param messageContext
     * @param xmlPayload
     * @throws AxisFault
     */
    protected void fillSOAPEnvelope(MessageContext messageContext, OMElement xmlPayload)
            throws AxisFault {
        fillSOAPEnvelope(messageContext, xmlPayload, null);
    }

    /**
     * 
     * @return
     */
    protected SOAPFactory getSOAPFactory() {
        String soapVersionURI = getOptions().getSoapVersionURI();
        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            // make the SOAP 1.1 the default SOAP version
            return OMAbstractFactory.getSOAP11Factory();
        }
    }
}
