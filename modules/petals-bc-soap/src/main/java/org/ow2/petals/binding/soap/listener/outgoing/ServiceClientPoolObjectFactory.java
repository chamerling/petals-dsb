/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 OW2 consortium,
 * http://www.ow2.org/
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

import static org.apache.axis2.addressing.AddressingConstants.DISABLE_ADDRESSING_FOR_OUT_MESSAGES;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.OUTGOING_SERVICE_CLIENT_PREFIX;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTPS.DEFAULT_HTTPS_PORT;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.RobustOutOnlyAxisOperation;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.transport.TransportSender;
import org.apache.axis2.transport.http.CommonsHTTPTransportSender;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.ow2.petals.binding.soap.ServiceContext;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.SoapComponentContext.ServiceManager;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;

import com.ebmwebsourcing.easycommons.uuid.QualifiedUUIDGenerator;

/**
 * Get new Axis2 ServiceClient from the pool factory
 * 
 * @author Christophe DENEUX - Cap Gemini
 * @author Christophe HAMERLING - eBM WebSourcing
 */
public class ServiceClientPoolObjectFactory extends BasePoolableObjectFactory {

    private final String address;

    private final QName operation;

    private final URI mep;

    private final ConfigurationExtensions extensions;

    private final SoapComponentContext soapContext;

    private final Provides provides;

    private final Logger logger;

    private final String soapAction;

    /**
     * Create the SOAP options of the outgoing SOAP message
     * 
     * @param address
     *            the address of the partner service
     * @param soapAction
     *            the SOAP action
     * @param extensions
     *            the SU extension
     * @return the Axis 2 client options
     * @throws MessagingException
     *             if there is an error to set transport
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    private Options createOptions(final String address, final String soapAction,
            final ConfigurationExtensions extensions) throws MessagingException, GeneralSecurityException, IOException {

        final Options options = new Options();

        // check if the address is a valid URI
        try {
            new URI(address);
        } catch (final URISyntaxException e) {
            throw new AxisFault("Invalid external web-service address: '" + address + "'");
        }
        
        // set destination address
        options.setTo(new EndpointReference(address));
        
        // set the content type (necessary for the formatter)
        options.setProperty(org.apache.axis2.Constants.Configuration.MESSAGE_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_SOAP_XML);
        
        // disable WSA-Addressing
        if (!SUPropertiesHelper.isWSAEnabled(extensions)) {
            options.setProperty(DISABLE_ADDRESSING_FOR_OUT_MESSAGES, Boolean.TRUE);
        }

        // set the soap action
        if (soapAction != null) {
            options.setAction(soapAction);
        }

        // set the timeout
        if (this.provides != null) {
        	final Long timeout = this.provides.getTimeout();
        	if(timeout != null) {
        		options.setTimeOutInMilliSeconds(timeout);
        	}
        } else {
        	// NOP
        }
        
        // Get the soapEnvelopeNamespaceURI version to use - optional,
        // default
        // is 1.1 version
        final String soapEnvelopeNamespaceURI = SUPropertiesHelper
                .retrieveSOAPEnvelopeNamespaceURI(extensions);
        options.setSoapVersionURI(soapEnvelopeNamespaceURI);

        // set transport
        setTransport(address, extensions, options);

        // get proxy settings if they are defined in the extensions
        final HttpTransportProperties.ProxyProperties proxyProperties = SUPropertiesHelper
                .retrieveProxySettings(extensions);
        if (proxyProperties != null) {
            options.setProperty(HTTPConstants.PROXY, proxyProperties);
        }

        // prevent Axis from transforming SOAP fault to Axis fault
        options.setExceptionToBeThrownOnSOAPFault(false);

        options.setCallTransportCleanup(SUPropertiesHelper
                .retrieveCleanupTransport(extensions));

        SUPropertiesHelper.setBasicAuthentication(extensions, options);

        return options;
    }

    private final void setTransport(final String address, final ConfigurationExtensions extensions,
            final Options options) throws MessagingException, GeneralSecurityException, IOException {

        // get transport protocol
        URI uri = URI.create(address);
        String scheme = uri.getScheme();
        String transport = null;
        if (scheme == null) {
            transport = Constants.TRANSPORT_HTTP;
        } else {
            transport = scheme;
        }

        TransportSender sender;
        TransportOutDescription transportOutDescription;

        if (transport.equals(Constants.TRANSPORT_HTTP)) {

            // set transport protocol for ingoing message (response)
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

            // set transport out description
            transportOutDescription = new TransportOutDescription("http");

            if (SUPropertiesHelper.isAxis1CompatibilityEnabled(extensions)) {
                sender = new Axis1SOAPFaultHTTPTransportSender();

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Set the customized Axis 2 transport sender: "
                            + sender.getClass().getName());
                }
            } else {
                sender = new CommonsHTTPTransportSender();
            }
        } else if (transport.equals(Constants.TRANSPORT_HTTPS)) {

            // set transport protocol for ingoing message (response)
            options.setTransportInProtocol(Constants.TRANSPORT_HTTPS);

            transportOutDescription = new TransportOutDescription("https");
            sender = new CommonsHTTPTransportSender();

            URL keystoreURL = null;
            String keystorePassword = null;
            String keystoreFileStr = SUPropertiesHelper.getKeystoreFile(extensions);
            if (keystoreFileStr != null) {
                try {
                    File keystoreFile = new File(keystoreFileStr);
                    keystoreURL = keystoreFile.toURI().toURL();

                    if (logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "Client authentication is enabled.");
                    }
                } catch (MalformedURLException e) {
                    throw new MessagingException("The keystore URL is not correct: "
                            + e.getMessage());
                }
                keystorePassword = SUPropertiesHelper.getKeystorePassword(extensions);
            }

            URL truststoreURL = null;
            String truststorePassword = null;
            String truststoreFileStr = SUPropertiesHelper.getTruststoreFile(extensions);
            if (truststoreFileStr != null) {
                try {
                    File truststoreFile = new File(truststoreFileStr);
                    truststoreURL = truststoreFile.toURI().toURL();
                    if (logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "Server authentication is enabled.");
                    }
                } catch (MalformedURLException e) {
                    throw new MessagingException("The truststore URL is not correct: "
                            + e.getMessage());
                }
                truststorePassword = SUPropertiesHelper.getTruststorePassword(extensions);
            }

            ProtocolSocketFactory socketFactory;
            if (truststoreURL == null && keystoreURL == null) {
                socketFactory = new EasySSLProtocolSocketFactory();
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Client and server authentications are disabled.");
                }
            } else {
                socketFactory = new AuthSSLProtocolSocketFactory(keystoreURL, keystorePassword,
                        truststoreURL, truststorePassword);
            }

            int port = uri.getPort();
            if (port == -1) {
                port = DEFAULT_HTTPS_PORT;
            }

            Protocol protocolHandler = new Protocol("https", socketFactory, port);
            options.setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, protocolHandler);

        } else {
            throw new NotImplementedException("Transport protocol not supported.");
        }

        // set http PROTOCOL parameter (request)
        try {
            transportOutDescription.addParameter(new Parameter("PROTOCOL", "HTTP/1.1"));
        } catch (AxisFault af) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                        "Can not set the http PROTOCOL parameter: " + af.getMessage());
            }
        }

        try {
            sender.init(this.soapContext.getAxis2ConfigurationContext(), transportOutDescription);
        } catch (AxisFault e) {
            throw new MessagingException("Can not initialiaze the transport sender: "
                    + e.getMessage());
        }
        transportOutDescription.setSender(sender);

        // set the transport for outgoing message
        options.setTransportOut(transportOutDescription);

        // set chunked mode
        options.setProperty(HTTPConstants.CHUNKED,
                SUPropertiesHelper.retrieveChunkedMode(extensions));
    }

    /**
     * Creates a new instance of ServiceClientPoolObjectFactory
     * 
     * @param operation
     * @param mep
     * @param logger
     * @param soapAction
     * @param wsdlDescription
     * @param cdkExtensions
     * @param modules
     */
    public ServiceClientPoolObjectFactory(final String address, final QName operation,
            final URI mep, final ConfigurationExtensions extensions,
            final SoapComponentContext context, final Provides provides, final Logger logger,
            final String soapAction) {
        this.address = address;
        this.operation = operation;
        this.soapAction = soapAction;
        this.mep = mep;
        this.extensions = extensions;
        this.soapContext = context;
        this.provides = provides;
        this.logger = logger;
    }

    /**
     * Engage a module
     * 
     * @param petalsServiceClient
     * @param moduleName
     * @throws AxisFault
     */
    protected void engageModule(final PetalsServiceClient petalsServiceClient,
            final String moduleName) throws AxisFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Engaging module " + moduleName);
        }
        petalsServiceClient.engageModule(moduleName);
    }

    /**
     * Engage the modules for the service client. The modules are available on
     * the service unit listener since they have been defined during SU
     * deployment.
     * 
     * @param petalsServiceClient
     * @throws AxisFault
     */
    private void engageModules(final PetalsServiceClient petalsServiceClient) throws AxisFault {
    	if (provides == null) {
    		return;
    	}
    	
        final List<String> modules = soapContext.getProvidersManager().getServiceContext(provides)
                .getModules();
        if (modules != null) {
            for (final String name : modules) {
                engageModule(petalsServiceClient, name);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    @Override
    public Object makeObject() throws MessagingException, AxisFault, GeneralSecurityException, IOException {

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Creating a service client for : " + address + ", with operation '"
                    + operation + "', and MEP '" + mep + "'");
        }

        final AxisService axisService = new AxisService(
                OUTGOING_SERVICE_CLIENT_PREFIX
                        + new QualifiedUUIDGenerator(org.ow2.petals.commons.Constants.UUID_DOMAIN)
                                .getNewID());
        final ServiceManager<Provides> providersManager = soapContext.getProvidersManager();
        final ServiceContext<Provides> serviceContext = providersManager
                .getServiceContext(provides);
        
        // WSA support...
        if (serviceContext != null) {
        	final ClassLoader cl = serviceContext.getClassloader();
        	axisService.setClassLoader(cl);
        }

        final AxisOperation axisOperation;
        if (MEPConstants.IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new OutOnlyAxisOperation(operation);
        } else if (MEPConstants.ROBUST_IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new RobustOutOnlyAxisOperation(operation);
        } else if (MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep)
                || MEPConstants.IN_OUT_PATTERN.equals(mep)) {
            axisOperation = new OutInAxisOperation(operation);
        } else {
            axisOperation = null;
        }
        axisService.addOperation(axisOperation);

        // create Options for the stub
        Options options = createOptions(address, soapAction, extensions);

        // add the service parameters
        try {
            providersManager.addServiceParameters(provides, axisService);
        } catch (final XMLStreamException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.getMessage());
            }
        } catch (final DeploymentException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.getMessage());
            }
        }


        try {
            final PetalsServiceClient petalsServiceClient = new PetalsServiceClient(
                    soapContext.getAxis2ConfigurationContext(), axisService);
            petalsServiceClient.setOptions(options);

            // engage the Axis2 modules
            engageModules(petalsServiceClient);

            return petalsServiceClient;
        } catch (final AxisFault e) {
            throw new MessagingException("Can't create ServiceClient", e);
        }
    }
}
