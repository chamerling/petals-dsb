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

package org.ow2.petals.binding.soap.listener.incoming;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.Utils;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_CLIENT;
import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_SERVER;

/**
 * REST service processor.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class RESTProcessor extends RequestProcessor {

    /**
     * 
     * @param component
     * @param channel
     * @param log
     */
    public RESTProcessor(final AbstractComponent component, final DeliveryChannel channel,
            final Logger log) {
        super(component, channel, log);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.binding.soap.listener.incoming.RequestProcessor#process
     * (org.apache.axis2.context.MessageContext,
     * org.apache.axiom.soap.SOAPFactory)
     */
    protected MessageContext process(final MessageContext inContext, final SOAPFactory factory)
            throws AxisFault {
        this.logger.log(Level.FINE, "Processing the incoming REST message");

        // Type of service has been checked before
        final PetalsAxisService axisService = (PetalsAxisService) inContext.getAxisService();
        final ConfigurationExtensions extensions = axisService.getConsumesCDKExtensions();

        // Retrieve message operation and service
        QName jbiOperation = null;
        final String method = (String) inContext
                .getProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_METHOD);
        SOAPEnvelope env = inContext.getEnvelope();
        final String uri = SUPropertiesHelper.retrieveRESTAddNSURIOnRequest(extensions);
        final String prefix = SUPropertiesHelper.retrieveRESTAddNSPrefixOnRequest(extensions);

        if (Constants.Configuration.HTTP_METHOD_GET.equals(method)) {
            jbiOperation = this.getJBIOperationFromURL(inContext);
            if (jbiOperation == null) {
                throw new AxisFault(
                        "Operation not found in message context, check the request URL (http://HOST:PORT/petals/services/SERVICE/OPERATION?PARAM=VALUE)",
                        FAULT_CLIENT);
            }
            env = this.createEnvelopeFromGet(inContext, jbiOperation, uri, prefix, factory);

        } else if (Constants.Configuration.HTTP_METHOD_POST.equals(method)
                || Constants.Configuration.HTTP_METHOD_PUT.equals(method)
                || Constants.Configuration.HTTP_METHOD_DELETE.equals(method)) {
            // jbiOperation = this.retrieveOperationFromSOAPBody(inContext);
            jbiOperation = this.getJBIOperationFromURL(inContext);
            if (jbiOperation == null) {
                throw new AxisFault(
                        "Operation not found in message context, check the request URL (http://HOST:PORT/petals/services/SERVICE/OPERATION?PARAM=VALUE)",
                        FAULT_CLIENT);
            }
            env = this.createEnvelopeFromPost(inContext, uri, prefix, factory);

        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "JBI OPERATION : " + jbiOperation);
            this.logger.log(Level.FINE, "HTTP METHOD : " + method);
            this.logger.log(Level.FINE, "SOAP ENVELOPE : " + env.toString());
        }
        inContext.setEnvelope(env);

        // get timeout
        final long timeout = SUPropertiesHelper.retrieveTimeout(extensions);

        // create the message exchange
        MessageExchange exchange = null;
        try {
            exchange = this.createMessageExchange(inContext, jbiOperation, axisService);
        } catch (final AxisFault e) {
            final String errorMsg = "Error while transforming SOAP request to JBI MessageExchange";
            this.logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }

        // send the message exchange through JBI
        // As the SOAP/HTTP request may wait for a response,
        // use the sendSynchrone MEP
        boolean sent = false;
        try {

            if (timeout == -1L) {
                sent = this.channel.sendSync(exchange);
            } else {
                sent = this.channel.sendSync(exchange, timeout);
            }

        } catch (final MessagingException e) {
            final String errorMsg = "Error while sending message through JBI NMR.";
            this.logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }

        if (!sent) {
            throw new AxisFault("A timeout occurs calling the consumed service.", FAULT_SERVER);
        }
        // process the response received from JBI NMR. The out context is
        // updated
        return processJBIResponse(exchange, inContext, factory);
    }

    @Override
    protected MessageContext processJBIResponse(final MessageExchange exchange,
            final MessageContext inMessage, final SOAPFactory factory) throws AxisFault {
        MessageContext outMessage = super.processJBIResponse(exchange, inMessage, factory);
        final PetalsAxisService axisService = (PetalsAxisService) inMessage.getAxisService();
        final ConfigurationExtensions extensions = axisService.getConsumesCDKExtensions();
        // remove the prefixes if required...
        final List<String> prefixes = SUPropertiesHelper
                .retrieveRESTRemovePrefixOnResponse(extensions);
        if (prefixes != null) {
            SOAPEnvelope envelope = outMessage.getEnvelope();
            removeNSPrefixes(envelope, prefixes);
            outMessage.setEnvelope(envelope);
        }
        return outMessage;
    }

    /**
     * Remove all the prefixes from the SOAP Body root element.
     * 
     * @param envelope
     * @param prefixes
     */
    private void removeNSPrefixes(final SOAPEnvelope envelope, final List<String> prefixes) {
        final OMElement element = envelope.getBody().getFirstElement();
        for (final String prefix : prefixes) {
            this.removePrefix(element, prefix);
        }
    }

    /**
     * Remove prefix from the OMElement. Do it for the current element and its
     * children.
     * 
     * @param element
     * @param prefixName
     */
    @SuppressWarnings("unchecked")
    private void removePrefix(final OMElement element, final String prefixName) {
        // remove the root element namespace
        final OMNamespace omns = element.getNamespace();
        boolean same = false;
        if (omns != null) {
            same = prefixName.equals(element.getNamespace().getPrefix());
        }

        if ((prefixName == null) || same || "*".equals(prefixName)) {
            element.setNamespace(null);
        }

        // remove xmlns attributes
        final Iterator attributes = element.getAllAttributes();
        while (attributes.hasNext()) {
            final OMAttribute attribute = (OMAttribute) attributes.next();
            if ((attribute.getNamespace() != null)
                    && "xmlns".equals(attribute.getNamespace().getPrefix())) {
                element.removeAttribute(attribute);
            }
        }

        // go into children
        final Iterator<OMElement> iter = element.getChildElements();
        while (iter.hasNext()) {
            final OMElement element2 = iter.next();
            this.removePrefix(element2, prefixName);
        }
    }

    /**
     * Get an operation from the REST URI. The operation is the part just after
     * the Service Name.
     * 
     * @param inContext
     * @return
     */
    protected QName getJBIOperationFromURL(final MessageContext inContext) {
        QName operation = null;
        final EndpointReference toEPR = inContext.getTo();
        final String filePart = toEPR.getAddress();
        final String[] values = Utils.parseRequestURLForServiceAndOperation(filePart, inContext
                .getConfigurationContext().getServiceContextPath());

        this.logger.log(Level.FINE, "Values : " + values[0] + ", " + values[1]);
        if ((values[1] != null) && (values[1].length() != 0)) {
            operation = new QName(values[1]);
        }
        return operation;
    }

    /**
     * Create the SOAP Envelope from the incoming one. Just add namespace if
     * defined in the Service Unit.
     * 
     * @param inContext
     * @param operation
     * @param nsUri
     * @param nsPrefix
     * @param factory
     * @return
     */
    protected SOAPEnvelope createEnvelopeFromPost(final MessageContext inContext,
            final String nsUri, final String nsPrefix, final SOAPFactory factory) {

        final SOAPEnvelope envelope = inContext.getEnvelope();
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "POST envelope before : " + envelope);
        }

        OMNamespace namespace = null;
        if (nsUri != null) {
            namespace = factory.createOMNamespace(nsUri, nsPrefix);
        }

        if (namespace != null) {
            envelope.declareNamespace(namespace);
            final SOAPBody body = envelope.getBody();
            body.declareNamespace(namespace);

            final OMElement rootElement = body.getFirstElement();
            this.fixNameSpace(rootElement, namespace);
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "POST envelope after : " + envelope);
        }

        return envelope;
    }

    /**
     * If element namespace is null, add the given one and go into all children.
     * 
     * @param element
     * @param namespace
     */
    private void fixNameSpace(final OMElement element, final OMNamespace namespace) {
        if (element.getNamespace() == null) {
            element.setNamespace(namespace);
        }

        final Iterator<OMElement> iter = element.getChildElements();
        while (iter.hasNext()) {
            final OMElement childElement = iter.next();
            this.fixNameSpace(childElement, namespace);
        }
    }

    /**
     * Create a SOAPEnvelope from the REST URI.
     * 
     * @param factory
     * @param inContext
     * @param operation
     * @return
     */
    protected SOAPEnvelope createEnvelopeFromGet(final MessageContext inContext,
            final QName operation, final String nsUri, final String nsPrefix,
            final SOAPFactory factory) {

        OMNamespace namespace = null;
        final SOAPEnvelope env = factory.createSOAPEnvelope();

        if (nsUri != null) {
            namespace = factory.createOMNamespace(nsUri, nsPrefix);
            env.declareNamespace(namespace);
        }

        final SOAPBody body = factory.createSOAPBody(env);
        final OMElement root = factory.createOMElement(operation, body);
        root.setNamespace(namespace);

        // get the URL parameters as Map
        final EndpointReference toEPR = inContext.getTo();
        final String filePart = toEPR.getAddress();
        final Map<String, String> params = this.getURLParameters(filePart, inContext
                .getConfigurationContext().getServiceContextPath());
        final List<String> keys = this.getOrderedURLKeys(filePart, inContext
                .getConfigurationContext().getServiceContextPath());

        for (final String key : keys) {
            final String value = params.get(key);
            final OMElement element = factory.createOMElement(key, namespace, root);
            element.setText(value);
            root.addChild(element);
        }
        return env;
    }

    /**
     * Get the URL parameters keys in the good order. Something like
     * <code>http://HOST:PORT/PREFIX/SERVICE/OPERATION?param1=value1&param2=value2</code>
     * will return a list with <code>param1,param2</code> elements.
     * 
     * @param path
     * @param servicePath
     * @return
     */
    private List<String> getOrderedURLKeys(final String path, final String servicePath) {
        final List<String> result = new LinkedList<String>();
        final int index = path.lastIndexOf(servicePath);
        String afterService;

        if (-1 != index) {
            final int serviceStart = index + servicePath.length();

            if (path.length() > serviceStart + 1) {
                afterService = path.substring(serviceStart + 1);

                final int queryIndex = afterService.indexOf('?');
                String query = "";
                if (queryIndex > 0) {
                    // Skip operation
                    query = afterService.substring(queryIndex + 1, afterService.length());
                }

                if (query.length() > 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(query, "&");
                    while (tokenizer.hasMoreElements()) {
                        final String token = (String) tokenizer.nextElement();
                        if (token.contains("=")) {
                            result.add(token.substring(0, token.indexOf("=")));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the URL parameters as Map.
     * 
     * @param path
     * @param servicePath
     * @return
     */
    private Map<String, String> getURLParameters(final String path, final String servicePath) {
        final Map<String, String> parameters = new HashMap<String, String>();
        final int index = path.lastIndexOf(servicePath);
        String afterService;

        if (-1 != index) {
            final int serviceStart = index + servicePath.length();

            if (path.length() > serviceStart + 1) {
                afterService = path.substring(serviceStart + 1);

                final int queryIndex = afterService.indexOf('?');
                String query = "";
                if (queryIndex > 0) {
                    // Skip operation
                    // String operation = afterService.substring(0, queryIndex);
                    query = afterService.substring(queryIndex + 1, afterService.length());
                }

                if (query.length() > 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(query, "&");
                    while (tokenizer.hasMoreElements()) {
                        final String token = (String) tokenizer.nextElement();
                        if (token.contains("=")) {
                            parameters.put(token.substring(0, token.indexOf("=")), token.substring(
                                    token.indexOf("=") + 1, token.length()));
                        }
                    }
                }
            }
        }
        return parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.binding.soap.listener.incoming.RequestProcessor#
     * getProcessorName()
     */
    @Override
    protected String getProcessorName() {
        return "REST";
    }
}
