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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.SourceHelper;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.addressing.Addressing;
import org.ow2.petals.binding.soap.addressing.WSAHelper;
import org.ow2.petals.binding.soap.util.AttachmentHelper;
import org.ow2.petals.binding.soap.util.Marshaller;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.StaxUtils;
import org.ow2.petals.commons.exception.ExceptionUtil;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import static javax.jbi.messaging.NormalizedMessageProperties.PROTOCOL_HEADERS;

import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.addressing.definition.api.WSAddressingException;

/**
 * An external web service dispatcher. This dispatcher send the JBI message to
 * an external web service. The service URL is specified in the PEtALS
 * extensions.
 * 
 * @author chamerling - eBM WebSourcing
 */
public class SOAPCaller {

    public SoapComponentContext soapContext;

    public Logger logger;

    /**
     * Creates a new instance of {@link SOAPCaller}
     * 
     * @param soapContext
     * @param logger
     */
    public SOAPCaller(final SoapComponentContext soapContext, final Logger logger) {
        this.soapContext = soapContext;
        this.logger = logger;
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
            final ConfigurationExtensions extensions) throws XMLStreamException {

        StAXOMBuilder builder = null;
        if (body instanceof DOMSource) {
            try {
                builder = new StAXOMBuilder(SourceHelper.convertDOMSource2InputSource(
                        ((DOMSource) body)).getByteStream());
            } catch (final XmlException e) {
                throw new XMLStreamException(e);
            }
        } else {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(body);
            builder = new StAXOMBuilder(parser);
        }
        OMElement rootElement = builder.getDocumentElement();

        return rootElement;
    }

    /**
     * Create the SOAPBody content. The JBI content is used as root element. All
     * the JBI attachments are added as root element children.
     * 
     * @param body
     * @return the soap body content or null if {@link NormalizedMessage} is
     *         empty.
     * @throws XMLStreamException
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("unchecked")
    protected static OMElement createSOAPBodyContent(final NormalizedMessage nm,
            final QName operation, final PetalsServiceClient client,
            final ConfigurationExtensions extensions) throws XMLStreamException,
            UnsupportedEncodingException {

        OMElement document = null;
        final Source src = nm.getContent();

        if (src != null) {
            // create the root element
            final OMFactory fac = OMAbstractFactory.getOMFactory();
            document = SOAPCaller.buildOMElementFromSource(src, operation, extensions);
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
                        // An element references the attachment, we replace it
                        // by
                        // itself using AXIOM API (It's a requirement of Axis2)
                        attachRefElt.getFirstChildWithName(
                                new QName("http://www.w3.org/2004/08/xop/include", "Include"))
                                .detach();
                        final OMText attach = fac.createOMText(attachment, true);
                        attachRefElt.addChild(attach);
                    }
                }
            }
        }

        return document;
    }

    public void call(final Exchange exchange, final ConfigurationExtensions cdkExtensions,
            final Provides provides) {

        final Addressing addressing = retrieveAddressing(exchange, cdkExtensions);
        if (addressing.getTo() == null) {
            final String message = "Can not define the Web service address to send message to";
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, message);
            }
            exchange.setError(new MessagingException("BC-SOAP Exception => " + message));
            return;
        }

        final String address = addressing.getTo();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Calling external Web Service : " + address);
        }

        // Get the incoming Normalized message
        final NormalizedMessage in = exchange.getInMessage();
        if (in == null) {
            exchange.setError(new Exception("Message exchange must handle an In normalized message"));
        } else {

            // Get operation
            final QName jbiOperation = exchange.getOperation();

            // Trying to determine the value of the soapAction parameter to set
            // on the outgoing message
            // first : the soapAction has been set in the jbi descriptor
            String soapAction = SUPropertiesHelper.retrieveDefaultSOAPAction(cdkExtensions);

            // at last, trying to retrieve it from the WSDL, based on the first
            // element of the message
            if (soapAction == null) {
                soapAction = retrieveSoapActionFromWsdl(exchange, provides);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("jbiOperation of the received exchange: " + jbiOperation);
                logger.fine("soapAction of the received exchange: " + soapAction);
            }

            // create service client used to invoke WS
            try {
                PetalsServiceClient petalsServiceClient = null;
                try {
                    // The service client options are set during its creation
                    petalsServiceClient = soapContext.borrowServiceClient(address, jbiOperation,
                            soapAction, exchange.getExchangePattern(), cdkExtensions, provides);
                    petalsServiceClient.setLogger(logger);

                    // update the service client options with the addressing
                    // information
                    // FIXME : This should be done in the client pool factory...
                    updateClient(petalsServiceClient, addressing, exchange);

                    // create the in body
                    final OMElement inBodyElement = SOAPCaller.createSOAPBodyContent(in,
                            exchange.getOperation(), petalsServiceClient, cdkExtensions);

                    if (logger.isLoggable(Level.FINE)) {
                        // The condition is splitted for performance reasons
                        if (inBodyElement != null) {
                            logger.log(Level.FINE, "OUTGOING Payload : " + inBodyElement.toString());
                        }
                    }

                    if (exchange.isInOnlyPattern()) {
                        // send as InOnly message
                        petalsServiceClient.fireAndForget(jbiOperation, inBodyElement,
                                getHeaders(in, cdkExtensions));
                    } else if (exchange.isRobustInOnlyPattern()) {
                        // send as InOnly message
                        petalsServiceClient.sendRobust(jbiOperation, inBodyElement,
                                getHeaders(in, cdkExtensions));
                    } else if (exchange.isInOptionalOutPattern() || exchange.isInOutPattern()) {
                        // send as In(optional)Out message
                        final MessageContext response = petalsServiceClient.sendReceive(
                                jbiOperation, inBodyElement, getHeaders(in, cdkExtensions));
                        // if msg exchange required a response, set it
                        if (response.getEnvelope().getBody() != null) {
                            // For performance reasons, the AXIOM's cache is not
                            // used to optimize the namespace writing. The
                            // service
                            // provider must be in charge of this optimization.
                            Source source = null;
                            if (response.getAttachmentMap() == null
                                    || ((response.getAttachmentMap().getContentIDSet() != null && response
                                            .getAttachmentMap().getContentIDSet().size() <= 0))) {
                                boolean axis1Compatibility = SUPropertiesHelper
                                        .isAxis1CompatibilityEnabled(cdkExtensions);
                                source = Marshaller.createSourceContent(response.getEnvelope(),
                                        axis1Compatibility);
                            } else {
                                // That's avoid to put the attachment in the
                                // payload as binary node
                                source = Marshaller.createSourceContentAndAttachment(response);
                            }

                            NormalizedMessage returnedNormalizedMessage = null;
                            // Get the outgoing Normalized message
                            if (response.isFault()) {
                                logger.log(Level.FINE, "RESPONSE is a SOAP Fault.");
                                returnedNormalizedMessage = exchange.createFault();
                                exchange.setFault((Fault) returnedNormalizedMessage);
                            } else {
                                returnedNormalizedMessage = exchange.getOutMessage();
                            }

                            returnedNormalizedMessage.setContent(source);

                            // add SOAP attachments to normalized message
                            Marshaller.setAttachments(response.getAttachmentMap(),
                                    returnedNormalizedMessage);

                            // propage the SOAP headers
                            Marshaller.setProperties(response, returnedNormalizedMessage);

                        } else {
                            logger.log(Level.FINE, "RESPONSE Payload : No response.");
                        }
                    } else {
                        throw new MessagingException("WSCaller - MEP not recognized : "
                                + exchange.getPattern().toString());
                    }
                } finally {
                    soapContext.returnServiceClient(address, jbiOperation,
                            exchange.getExchangePattern(), petalsServiceClient, soapAction);
                    if (petalsServiceClient != null
                            && petalsServiceClient.getOptions().isCallTransportCleanup()) {
                        petalsServiceClient.cleanupTransport();
                    }
                }
            } catch (final Exception e) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Catch an exception on the WS invocation : " + e.getMessage(), e);
                }
                exchange.setError(new MessagingException(ExceptionUtil.getExtendedMessage(e)));
            } catch (final Throwable t) {                
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Catch a throwable on the WS invocation : "
                            + t.getMessage(), t);
                }
                exchange.setError(new MessagingException(ExceptionUtil.getExtendedMessage(new Exception(t))));
            }
        }
    }

    /**
     * Retrieve data from JBI properties wich will be set into the SOAP header.
     * 
     * @param nm
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, DocumentFragment> getHeaders(final NormalizedMessage nm,
            final ConfigurationExtensions cdkExtensions) {
        final Map<String, DocumentFragment> result = new HashMap<String, DocumentFragment>();
        final List<String> filters = SUPropertiesHelper.retrieveHeaderList(cdkExtensions);

        // 1. get the properties defined in the SU from the filter value
        if (filters != null && filters.size() > 0) {
            final Set<String> properties = new HashSet<String>();
            final Set<?> names = nm.getPropertyNames();
            for (final Object object : names) {
                if (object instanceof String) {
                    final String propertyName = (String) object;
                    if (isFilteredValue(propertyName, filters)) {
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

            List<DocumentFragment> toInject = SUPropertiesHelper
                    .retrieveHeaderToInject(cdkExtensions);
            if (toInject != null) {
                int i = 1;
                for (DocumentFragment h : toInject)
                    result.put("_bc_soap_inject_" + i++, h);
            }
        }
        return result;
    }

    /**
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
     * Get the addressing information from the exchange (ws) and from the
     * service unit extensions (su).
     * 
     * @param exchange
     * @param extensions
     * @return
     */
    private final Addressing retrieveAddressing(final Exchange exchange,
            final ConfigurationExtensions extensions) {
        Addressing wsAddressing = null;
        try {
            if (exchange
                    .getInMessageProperty(org.ow2.petals.component.framework.api.Constants.WSStar.ADDRESSING_KEY) != null) {
                // get the property
                final Object object = exchange
                        .getInMessageProperty(org.ow2.petals.component.framework.api.Constants.WSStar.ADDRESSING_KEY);
                if (object instanceof Document) {
                    final String address = WSAddressingFactory.getInstance()
                            .newWSAddressingReader().readEndpointReferenceType((Document) object)
                            .getAddress();
                    if (address != null) {
                        wsAddressing = WSAHelper.getAddressing(address);
                    }
                }
            }
        } catch (final MessagingException e) {
            // no error to throw on this property research
        } catch (final WSAddressingException e) {
            // no error to throw on this property research
        }
        final Addressing suAddressing = WSAHelper.getAddressing(extensions);
        final Addressing wsaToAddressing = WSAHelper.getAddressing(exchange.getInAddressing());
        return WSAHelper.merge(wsAddressing, wsaToAddressing, suAddressing);
    }

    private String retrieveSoapActionFromWsdl(final Exchange exchange, final Provides provides) {
        String soapAction = null;

        try {

            final String endpointName = exchange.getEndpointName();
            final QName service = exchange.getEndpoint().getServiceName();

            final org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description d = soapContext
                    .getProvidersManager().getServiceContext(provides).getServiceDescription();

            final Endpoint e = d.getService(service).getEndpoint(endpointName);
            if (e != null) {
                final Binding b = e.getBinding();
                if (b != null) {
                    final BindingOperation bo = b.getBindingOperation(exchange.getOperationName());
                    if (bo != null) {
                        soapAction = bo.getSoapAction();
                    }
                }
            }
        } catch (final MessagingException e1) {
        	if (logger.isLoggable(Level.FINE)) {
        		logger.log(Level.WARNING, "Error '" + e1.getMessage()
                        + "' while trying to get elements to resolve soapAction.", e1);
        	} else {
        		logger.warning("Error '" + e1.getMessage()
                    + "' While trying to get elements to resolve soapAction.");
        	} 
        } catch (final Exception e) {
        	if (logger.isLoggable(Level.FINE)) {
        		logger.log(Level.WARNING, "Error '" + e.getMessage()
                        + "' while trying to get elements to resolve soapAction.", e);
        	} else {            
        		logger.warning("Error '" + e.getMessage()
                        + "' while trying to get elements to resolve soapAction.");
        	}
        }

        return soapAction;
    }

    /**
     * Update the client properties
     * 
     * @param client
     * @param addressing
     */
    protected void updateClient(final PetalsServiceClient client, final Addressing addressing,
            final Exchange exchange) {
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
}
