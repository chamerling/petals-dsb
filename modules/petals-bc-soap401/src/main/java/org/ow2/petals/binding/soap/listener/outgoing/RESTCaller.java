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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.jaxen.JaxenException;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.util.AxiomUtils;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.StaxUtils;
import org.ow2.petals.binding.soap.util.URIBuilder;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.w3c.dom.Document;

import static org.ow2.petals.binding.soap.Constants.ServiceUnit.REST_HTTP_METHOD;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.REST;

/**
 * A REST service caller.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class RESTCaller extends AbstractExternalServiceCaller {

    public static final String DEFAULT_HTTP_METHOD = Constants.Configuration.HTTP_METHOD_POST;

    /**
     * Creates a new instance of {@link RESTCaller}
     * 
     * @param soapContext
     * @param logger
     */
    public RESTCaller(final SoapComponentContext soapContext, final Logger logger) {
        super(soapContext, logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.binding.soap.listener.outgoing.JBIListenerDispatcher#
     * getDispatcherType()
     */
    public String getCallerType() {
        return REST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.binding.soap.listener.outgoing.JBIListenerDispatcher#process
     * (org.ow2.petals.component.framework.util.Exchange,
     * org.ow2.petals.component.framework.util.Extensions)
     */
    public void call(final Exchange exchange, final ConfigurationExtensions extensions,
            final Provides provides) {
        final String address = retrieveServiceAddress(exchange, extensions);
        if (address == null) {
            String message = "Can not define the REST service address to send message to";
            logger.warning(message);
            this.handleException(exchange, message);
            return;
        }

        final String httpMethod = this.getHTTPMethod(extensions);
        final NormalizedMessage out = exchange.getOutMessage();

        this.logger.fine("Calling REST service on " + address);

        OMElement result = null;
        try {

            // TODO : Check MEP and HTTP Method
            if (exchange.isInOutPattern()) {

                final OMElement omSource = this.sourceAsOMElement(exchange
                        .getInMessageContentAsSource());
                final URI epr = this.buildEPR(address, omSource);

                this.logger.fine("Final EPR for service call is : " + epr.toString());
                final Options options = createOptions(extensions, httpMethod, epr);

                final ServiceClient serviceClient = new ServiceClient();
                serviceClient.setOptions(options);

                final OMElement message = this.buildMessageBody(exchange.getOperation(), omSource);

                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine(getCallerType() + " MESSAGE : " + message.toString());
                }

                result = serviceClient.sendReceive(message);

                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine(getCallerType() + " RESULT : " + result.toString());
                }

                // if msg exchange required a response, set it
                if (result != null) {
                    out.setContent(AxiomUtils.createSource(result));
                }
            } else {
                throw new MessagingException("MEP not supported for " + getCallerType()
                        + " caller : " + exchange.getPattern().toString());
            }
        } catch (final Exception e) {
            this.logger.warning("Catch an exception on the " + getCallerType() + " invocation : "
                    + e.getMessage());
            try {
                exchange.setFault(e);
            } catch (final MessagingException e1) {
                this.logger.log(Level.SEVERE, "Can't return fault to consumer", e1);
            }
        }
    }

    /**
     * Create the {@link Options} object
     * 
     * @param extensions
     * @param httpMethod
     * @param epr
     * @return
     */
    protected Options createOptions(final ConfigurationExtensions extensions,
            final String httpMethod, final URI epr) {
        final Options options = new Options();
        options.setTo(new EndpointReference(epr.toString()));
        options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
        options.setProperty(Constants.Configuration.HTTP_METHOD, httpMethod);
        final long timeout = SUPropertiesHelper.retrieveTimeout(extensions);
        if (timeout != -1L) {
            options.setTimeOutInMilliSeconds(timeout);
        }
        return options;
    }

    /**
     * Build the EPR for the REST service
     * 
     * @param address
     * @return
     * @throws URISyntaxException
     * @throws Exception
     */
    protected URI buildEPR(final String address, final OMElement element) throws URISyntaxException {
        final URIBuilder builder = URIBuilder.getInstance();
        final Map<String, String> parameters = new HashMap<String, String>();
        final Set<String> set = builder.getParams(address);

        for (final String xpathExpression : set) {
            String tmp = null;
            this.logger.fine("Getting parameter for " + xpathExpression);
            OMElement elt = null;
            try {
                final AXIOMXPath axiomXPath = new AXIOMXPath(xpathExpression);
                elt = (OMElement) axiomXPath.selectSingleNode(element);
            } catch (final JaxenException e) {
                this.logger.warning("Bad XPATH expression : " + xpathExpression);
            }

            if (elt != null) {
                tmp = elt.getText();
            } else {
                tmp = "";
            }
            this.logger.fine("Value for key '" + xpathExpression + "' is '" + tmp + "'");
            parameters.put(xpathExpression, tmp);
        }

        return builder.build(address, parameters);
    }

    /**
     * Build the REST message from the JBI one
     * 
     * @param exchange
     * @return
     * @throws PEtALSCDKException
     * @throws XMLStreamException
     * @throws MessagingException
     */
    protected OMElement buildMessageBody(final QName operation, final OMElement source)
            throws PEtALSCDKException, XMLStreamException, MessagingException {
        OMElement result = source;
        if (operation != null) {
            final OMFactory factory = OMAbstractFactory.getOMFactory();
            final OMElement rootElement = factory.createOMElement(operation);
            rootElement.addChild(source);
            result = rootElement;
        }
        return result;
    }

    /**
     * 
     * @param source
     * @return
     * @throws PEtALSCDKException
     * @throws XMLStreamException
     * @throws MessagingException
     */
    protected OMElement sourceAsOMElement(final Source source) throws PEtALSCDKException,
            XMLStreamException, MessagingException {

        final Document contentSrc = UtilFactory.getSourceUtil().createDocument(source);
        final XMLStreamReader parser = StaxUtils.createXMLStreamReader(new DOMSource(contentSrc));
        final StAXOMBuilder builder = new StAXOMBuilder(parser);
        final OMElement document = builder.getDocumentElement();

        return document;
    }

    /**
     * 
     * @param jbiOperation
     * @param extensions
     * @return
     */
    protected String getHTTPMethod(final ConfigurationExtensions extensions) {
        // TODO change REST-HTTP-METHOD to HTTP-METHOD
        String rest = extensions.get(REST_HTTP_METHOD);
        if (rest == null) {
            rest = DEFAULT_HTTP_METHOD;
        }
        return rest;
    }
}
