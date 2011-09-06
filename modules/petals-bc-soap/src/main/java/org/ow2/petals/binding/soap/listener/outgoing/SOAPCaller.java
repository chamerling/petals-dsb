/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11Factory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.neethi.All;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.Policy;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.RampartConfig;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.SourceHelper;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.addressing.Addressing;
import org.ow2.petals.binding.soap.util.AttachmentHelper;
import org.ow2.petals.binding.soap.util.AxiomUtils;
import org.ow2.petals.binding.soap.util.Marshaller;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.StaxUtils;
import org.ow2.petals.binding.soap.util.WsdlHelper;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import static javax.jbi.messaging.NormalizedMessageProperties.PROTOCOL_HEADERS;

import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.SOAP;

import static org.ow2.petals.binding.soap.Constants.Policy.SECURITY_USER_PROPERTY;

/**
 * An external web service dispatcher. This dispatcher send the JBI message to
 * an external web service. The service URL is specified in the PEtALS
 * extensions.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SOAPCaller extends AbstractExternalServiceCaller {

    /**
     * Creates a new instance of {@link SOAPCaller}
     * 
     * @param soapContext
     * @param logger
     */
    public SOAPCaller(final SoapComponentContext soapContext, final Logger logger) {
        super(soapContext, logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.binding.soap.listener.outgoing.ExternalServiceCaller#call
     * (org.ow2.petals.component.framework.api.message.Exchange,
     * org.ow2.petals.component
     * .framework.api.configuration.ConfigurationExtensions,
     * org.ow2.petals.component.framework.jbidescriptor.generated.Provides)
     */
    public void call(final Exchange exchange, final ConfigurationExtensions cdkExtensions,
            final Provides provides) {

        final Addressing addressing = retrieveAddressing(exchange, cdkExtensions);
        if (addressing.getTo() == null) {
            final String message = "Can not define the Web service address to send message to";
            this.logger.warning(message);
            this.handleException(exchange, message);
            return;
        }

        final String address = addressing.getTo();

        // Get the jbi:principal property
        final String rampartUserName = (String) exchange.getProperty(SECURITY_USER_PROPERTY);

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Calling external Web Service : " + address);
        }

        // Get the incoming Normalized message
        final NormalizedMessage in = exchange.getInMessage();
        if (in == null) {
            exchange
                    .setError(new Exception(
            "Message exchange must handle an In normalized message"));
        } else {

            // Get the outgoing Normalized message
            final NormalizedMessage out = exchange.getOutMessage();

            // Get operation
            final QName jbiOperation = exchange.getOperation();
                
            //Trying to determine the value of the soapAction parameter to set on the outgoing message
            //first : the soapAction has been set in the jbi descriptor
            String soapAction = AbstractExternalServiceCaller.retrieveDefaultSOAPAction(cdkExtensions);
            
            //second, trying to get it from the jbi property
            if (soapAction == null && exchange.getProperty("soap-action") != null){
                soapAction =(String)exchange.getProperty("soap-action");
            } 
            
            //at last, trying to retrieve it from the WSDL, based on the first element of the message
            if (soapAction == null){

                try {
                    Element firstElement = null;
                    String endpointName= null;
                    
                    if (exchange.getEndpoint() != null
                            && exchange.getEndpoint().getServiceName() != null) {
                        QName service = exchange.getEndpoint().getServiceName();
                        firstElement = exchange.getInMessageContentAsDocument(true)
                                .getDocumentElement();
                        endpointName = exchange.getEndpointName();
                        soapAction = WsdlHelper.findSoapAction(firstElement, this.soapContext
                                .getProvidersManager().getServiceContext(provides)
                                .getServiceDescription(), endpointName, service);
                    }
                } catch (Exception e) {
                    this.logger.fine("Error '" +e+"' while trying to get elements to resolve soapAction.");
                }
            }

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("jbiOperation of the received exchange: " +jbiOperation);
                this.logger.fine("soapAction of the received exchange: " +soapAction);
            }

            // create service client used to invoke WS
            try {
                ServiceClient serviceClient = null;
                try {
                    // The service client options are set during its creation
                    serviceClient = this.soapContext.borrowServiceClient(address, jbiOperation,
                            soapAction, exchange.getExchangePattern(), cdkExtensions, provides, rampartUserName);
                            
                    // update the service client options with the addressing
                    // information
                    // FIXME : This should be done in the client pool factory...
                    this.updateClient(serviceClient, addressing, exchange);
                    if (rampartUserName != null) {
                        this.updateRampartConfiguration(serviceClient, exchange);
                    }

                    // create the in body
                    final OMElement inBodyElement = SOAPCaller.createSOAPBodyContent(in, exchange
                            .getOperation(), serviceClient, cdkExtensions);

                    if (this.logger.isLoggable(Level.FINE)) {
                        // The condition is splitted for performance reasons
                        if (inBodyElement != null) {
                            this.logger.log(Level.FINE, "OUTGOING Payload : "
                                    + inBodyElement.toString());
                        }
                    }

                    if (exchange.isInOnlyPattern()) {
                        // send as InOnly message
                        serviceClient.fireAndForget(jbiOperation, inBodyElement);

                    } else if (exchange.isRobustInOnlyPattern()) {
                        // send as RobustInOnly message
                        // If the robust in only exchange throws a Fault
                        // It will be catch by the component-common framework
                        serviceClient.sendRobust(jbiOperation, inBodyElement);

                    } else if (exchange.isInOptionalOutPattern() || exchange.isInOutPattern()) {
                        // send as In(optional)Out message
                        SOAPBody outBodyElement = null;
                        try {
                            outBodyElement = serviceClient.sendReceiveBody(jbiOperation,
                                    inBodyElement, this.getHeaders(in, cdkExtensions));
                        } catch (final AxisFault e) {
                            // only catched to send the performance
                            // notification.
                            // TODO : Extend to all MEPs
                            throw e;
                        }

                        // if msg exchange required a response, set it
                        if (outBodyElement != null) {
                            // For performance reasons, the AXIOM's cache is not
                            // used to optimize the namespace writing. The
                            // service
                            // provider must be in charge of this optimization.
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.log(Level.FINE, "RESPONSE Payload : "
                                        + outBodyElement.toString());
                            }

                            final OMElement response = this.buildResponseElement(outBodyElement);
                            if (response != null)
                                out.setContent(AxiomUtils.createSource(response));
                            Marshaller.copyAttachments(response, out);
                        } else {
                            this.logger.log(Level.FINE, "RESPONSE Payload : No response.");
                        }
                    } else {
                        throw new MessagingException("WSCaller - MEP not recognized : "
                                + exchange.getPattern().toString());
                    }
                } finally {
                    this.soapContext.returnServiceClient(address, jbiOperation, exchange
                            .getExchangePattern(), serviceClient, soapAction);
                    if (rampartUserName != null) {
                        this.resetRampartConfiguration(serviceClient, provides);
                    }
                    if (serviceClient != null && serviceClient.getOptions().isCallTransportCleanup()) {
                        serviceClient.cleanupTransport();
                    }
                }
            } catch (final Exception e) {
                this.logger.warning("Catch an exception on the WS invocation : " + e.getMessage());

                // The exception is an AxisFault (probably a SOAP Fault)
                if (e instanceof AxisFault) {
                    this.handleSOAPFault(exchange, (AxisFault) e);
                } else {
                    this.handleException(exchange, e);
                }
            }
        }
    }



    /**
     * Update the client properties
     * 
     * @param client
     * @param addressing
     */
    protected void updateClient(final ServiceClient client, final Addressing addressing, final Exchange exchange) {
        if (addressing == null) {
            return;
        }

        // update the WS-Addressing properties. No need to update the wsa:To
        // since this value has been used to create the client from the pool
        // factory.
        final Options options = client.getOptions();
        if (addressing.getFaultTo() != null) {
            options.setFaultTo(new EndpointReference(addressing.getFaultTo()));
        }
        if (addressing.getFrom() != null) {
            options.setFrom(new EndpointReference(addressing.getFrom()));
        }
        if (addressing.getReplyTo() != null) {
            options.setReplyTo(new EndpointReference(addressing.getReplyTo()));
        }
    }

    /**
     * Reset the rampart configuration.
     * @param serviceClient
     * @param provides
     */
    private void resetRampartConfiguration(final ServiceClient serviceClient, final Provides provides) {

        final Policy policyFile = Axis2Utils.loadPolicy(this.soapContext.getProvidersManager().getServiceContext(provides)
                .getPolicyPath(), this.logger);
        if (policyFile != null) {
            serviceClient.getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, policyFile);
        }
    }


    /**
     * Update the rampart user in the policy configuration
     * @param client : The Axis client
     * @param exchange : JBI message exchange
     */
    private void updateRampartConfiguration(final ServiceClient client, final Exchange exchange) {

        String rampartUser = null;

        // update the rampart user of the WS-Security
        if (exchange.getProperty(SECURITY_USER_PROPERTY) != null) {
            rampartUser = (String) exchange.getProperty(SECURITY_USER_PROPERTY);
        }

        final Policy policy = (Policy) client.getServiceContext().getProperty(RampartMessageData.KEY_RAMPART_POLICY);
        if (rampartUser != null && policy.getAssertions() != null && policy.getAssertions().get(0) instanceof ExactlyOne) {
            final ExactlyOne exactlyOne = (ExactlyOne) policy.getAssertions().get(0);
            if (exactlyOne.getPolicyComponents() != null
                    && exactlyOne.getPolicyComponents().get(0) instanceof All) {
                final List<?> assertions = ((All)exactlyOne.getPolicyComponents().get(0)).getAssertions();
                if (assertions != null) {
                    for (final Object assertion : assertions) {
                        if (assertion instanceof RampartConfig) {
                            // Override the rampart user defined in the policy.xml file
                            final RampartConfig rampartConfig = (RampartConfig) assertion;
                            rampartConfig.setUser(rampartUser);
                            client.getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);

                        }
                    }
                }
            }
        }

    }

    /**
     * Retrieve data from JBI properties wich will be set into the SOAP header.
     * 
     * @param nm
     * @return
     */
    private Map<String, DocumentFragment> getHeaders(final NormalizedMessage nm,
            final ConfigurationExtensions cdkExtensions) {
        final Map<String, DocumentFragment> result = new HashMap<String, DocumentFragment>();
        final List<String> filters = SUPropertiesHelper.retrieveHeaderList(cdkExtensions);

        // 1. get the properties defined in the SU from the filter value
        if (filters != null && filters.size() > 0) {
            final Set<String> properties = new HashSet<String>();
            final Set names = nm.getPropertyNames();
            for (final Object object : names) {
                if (object instanceof String) {
                    final String propertyName = (String) object;
                    if (this.isFilteredValue(propertyName, filters)) {
                        properties.add(propertyName);
                    }
                }
            }

            for (final String propertyName : properties) {
                final Object property = nm.getProperty(propertyName);
                // TODO : Inject not only document fragments...
                if (property instanceof DocumentFragment) {
                    result.put(propertyName, (DocumentFragment) property);
                }
            }
        }

        // 2. additional headers
        if (SUPropertiesHelper.retrieveInjectHeader(cdkExtensions)) {
            // add the protocol headers (the ones injected in consumer mode)
            final Object protocolHeadersPropertyObject = nm.getProperty(PROTOCOL_HEADERS);
            if (protocolHeadersPropertyObject != null
                    && protocolHeadersPropertyObject instanceof Map) {
                result.putAll((Map<String, DocumentFragment>) protocolHeadersPropertyObject);
            }
        }
        return result;
    }

    /**
     * 
     * @param propertyName
     * @param filters
     * @return
     */
    protected boolean isFilteredValue(final String propertyName, final List<String> filters) {
        final boolean result = false;
        for (final String filter : filters) {
            if (propertyName.equalsIgnoreCase(filter)) {
                return true;
            }

            if (filter.endsWith("*")) {
                final String tmp = filter.substring(0, filter.lastIndexOf("*"));
                if (propertyName.equals(tmp) || propertyName.startsWith(tmp)) {
                    return true;
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param body
     * @return
     */
    protected OMElement buildResponseElement(final SOAPBody body) {
        OMElement outBodyElement = body.getFirstElement();

        final java.util.Iterator<OMElement> it = body.getChildElements();
        OMElement message = null;
        OMElement firstRef = null;
        if (it.hasNext()) {
            message = it.next();
        }
        if (it.hasNext()) {
            firstRef = it.next();
        }

        // create new body without references
        if (firstRef != null) {
            final SOAP11Factory factory = new SOAP11Factory();
            final OMElement newBodyElement = factory.createOMElement(message.getQName());

            // TODO: find and replace all references: used recursivity
            if (message.getFirstElement() != null) {
                final OMElement newParamElement = factory.createOMElement(message.getFirstElement()
                        .getQName());
                final Iterator<OMElement> itChildRef = firstRef.getChildElements();
                OMElement refElmt = null;
                while (itChildRef.hasNext()) {
                    refElmt = itChildRef.next();

                    if (refElmt.getLocalName().equals("multiRef") && refElmt
                            .getAttribute(new QName("http://schemas.xmlsoap.org/soap/encoding/",
                            "arrayType")) != null) {
                        refElmt.setLocalName("array");
                    }

                    newParamElement.addChild(refElmt);
                }

                newBodyElement.addChild(newParamElement);

                outBodyElement = newBodyElement;
            }
        }

        return outBodyElement;
    }

    /**
     * Handle a SOAP Fault. Set it in the JBI message as Fault.
     * 
     * @param exchange
     * @param fault
     */
    private void handleSOAPFault(final Exchange exchange, final AxisFault fault) {
        if (fault.getFaultMessageContext() != null
                && fault.getFaultMessageContext().getEnvelope() != null) {
            try {
                final Source sourceFault = Marshaller.createSource(fault.getFaultMessageContext()
                        .getEnvelope(), false);
                final Fault jbiFault = exchange.createFault();
                jbiFault.setContent(sourceFault);
                exchange.setFault(jbiFault);
            } catch (final Exception e) {
                this.logger.log(Level.WARNING,
                "A fault can't be analized. An exception will be thrown");
                this.handleException(exchange, fault);
            }
        } else {
            this.handleException(exchange, fault);
        }
    }

    /**
     * Create the SOAPBody content. The JBI content is used as root element. All
     * the JBI attachments are added as root element children.
     * 
     * @param body
     * @return the soap body content or null if {@link NormalizedMessage} is
     *         empty.
     * @throws XMLStreamException
     */
    @SuppressWarnings("unchecked")
    protected static OMElement createSOAPBodyContent(final NormalizedMessage nm,
            final QName operation, final ServiceClient client,
            final ConfigurationExtensions extensions) throws XMLStreamException {

        OMElement document = null;
        final Source src = nm.getContent();

        if (src != null) {
            // create the root element
            final OMFactory fac = OMAbstractFactory.getOMFactory();
            document = SOAPCaller.buildOMElementFromSource(src, operation, extensions, fac);
            // add attachments if any
            if (nm.getAttachmentNames().size() > 0) {
                // enable MTOM
                client.getOptions().setProperty(Constants.Configuration.ENABLE_MTOM,
                        Constants.VALUE_TRUE);

                // Add JBI attachments to the document element
                final Set<String> names = nm.getAttachmentNames();
                for (final String key : names) {
                    final DataHandler attachment = nm.getAttachment(key);
                    final OMElement attachRefElt = AttachmentHelper.hasAttachmentElement(document,
                            attachment, key);
                    if (attachRefElt != null) {
                        // An element references the attachment, we replace it by
                        // itself using AXIOM API (It's a requirement of Axis2)
                        attachRefElt.getFirstChildWithName(
                                new QName("http://www.w3.org/2004/08/xop/include", "Include")).detach();
                        final OMText attach = fac.createOMText(attachment, true);
                        attachRefElt.addChild(attach);
                    }
                }
            }
        }
        return document;
    }


    /**
     * Build the {@link OMElement} from the {@link Source}. The SOAP operation
     * is added as root element if it is configured in the petals extensions.
     * 
     * @param body
     * @param operation
     * @param extensions
     * @return
     */
    protected static OMElement buildOMElementFromSource(final Source body, final QName operation,
            final ConfigurationExtensions extensions, final OMFactory factory)
    throws XMLStreamException {

        StAXOMBuilder builder = null;
        if(body instanceof DOMSource) {
            try {
                builder = new StAXOMBuilder(SourceHelper.convertDOMSource2InputSource(((DOMSource)body)).getByteStream());
            } catch (final XmlException e) {
                throw new XMLStreamException(e);
            }
        } else {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(body);
            builder = new StAXOMBuilder(parser);
        }
        OMElement rootElement = builder.getDocumentElement();

        if (AbstractExternalServiceCaller.retrieveAddRoot(extensions)) {
            final OMElement newRootElement = factory.createOMElement(operation);
            newRootElement.addChild(rootElement);
            rootElement = newRootElement;
        }
        return rootElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.binding.soap.listener.outgoing.ExternalServiceCaller#
     * getCallerType()
     */
    public String getCallerType() {
        return SOAP;
    }
}
