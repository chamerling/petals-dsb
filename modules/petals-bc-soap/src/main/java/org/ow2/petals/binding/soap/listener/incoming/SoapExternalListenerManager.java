/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
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
 * $Id: SoapListenerManager.java 154 27 sept. 06 alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming;

import java.io.File;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.AxisConfiguration;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.listener.incoming.jetty.AxisServletServer;
import org.ow2.petals.binding.soap.util.ComponentPropertiesHelper;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;

/**
 * This class is used to manage listeners for external WS addresses registered
 * during SU deployments
 * 
 * @version $Rev$
 * @since Petals 1.1
 * @author alouis - EBM Websourcing
 * @author chamerling - EBM Websourcing
 */

public class SoapExternalListenerManager {

    protected Logger logger;

    protected Set<String> addresses;

    protected AbstractComponent component;

    protected AbstractServiceUnitManager bindingSUM;

    protected AxisServletServer httpServer;

    protected SoapServerConfig serverConfig;

    protected SoapComponentContext soapContext;

    protected PetalsReceiver petalsReceiver;

    /**
     * Creates a new instance of {@link SoapExternalListenerManager}
     * 
     * @param ccontext
     * @param bindingSUM
     * @param soapContext
     * @param propertiesManager
     * @param logger
     * 
     * @throws JBIException
     *             if the specified host in the component extension is not a
     *             valid address
     */
    public SoapExternalListenerManager(final AbstractComponent component,
            final AbstractServiceUnitManager bindingSUM,
            final SoapComponentContext soapContext, final PetalsReceiver petalsReceiver,
            final Logger logger) throws JBIException {
        this.logger = logger;
        this.addresses = new HashSet<String>();
        this.bindingSUM = bindingSUM;
        this.component = component;
        this.soapContext = soapContext;
        this.serverConfig = createServerConfig(logger, component.getComponentExtensions());
        
        int port = 0;
        String containerPort = ((PetalsBindingComponent)component).getContainerConfiguration("port");
        if (containerPort != null) {
            try {
                port = Integer.parseInt(containerPort.trim());
            } catch (NumberFormatException e) {
            }
        }
        if (port != 0) {
        	this.serverConfig.setHttpPort(port);
        }
        
        this.petalsReceiver = petalsReceiver;
    }

    /**
     * Create the SOAP server configuration
     * 
     * @return the SOAP server configuration
     * 
     * @throws JBIException
     *             if the specified host is not a valid address
     */
    private static final SoapServerConfig createServerConfig(Logger logger,
            ConfigurationExtensions extensions) throws JBIException {
        
        final String host = ComponentPropertiesHelper.getHttpHostName(extensions);
        int httpPort = ComponentPropertiesHelper.getHttpPort(logger, extensions);
        final SoapServerConfig soapConfig = new SoapServerConfig(logger, host, httpPort);

        soapConfig.setProvidesList(ComponentPropertiesHelper.isProvidingServicesList(extensions));
        soapConfig.setServicesContext(ComponentPropertiesHelper.getServicesContext(extensions));
        soapConfig.setServicesMapping(ComponentPropertiesHelper.getServicesMapping(extensions));

        soapConfig.setJettyThreadMaxPoolSize(ComponentPropertiesHelper.getHttpThreadMaxPoolSize(
                logger, extensions));
        soapConfig.setJettyThreadMinPoolSize(ComponentPropertiesHelper.getHttpThreadMinPoolSize(
                logger, extensions));
        soapConfig
                .setJettyAcceptors(ComponentPropertiesHelper.getHttpAcceptors(logger, extensions));

        setHttpsServerConfig(logger, extensions, soapConfig);

        return soapConfig;
    }

    private static void setHttpsServerConfig(Logger logger, ConfigurationExtensions extensions,
            final SoapServerConfig soapConfig) {
        boolean isHttpsEnabled = ComponentPropertiesHelper.isHttpsEnabled(extensions);
        if (isHttpsEnabled) {
            soapConfig.setHttpsPort(ComponentPropertiesHelper.getHttpsPort(logger, extensions));

            // a keystore is mandatory for HTTPS
            String httpsKeystoreFile = ComponentPropertiesHelper.getHttpsKeystoreFile(extensions);
            if (httpsKeystoreFile != null && !httpsKeystoreFile.trim().equals("")) {
                File httpsKeystore = new File(httpsKeystoreFile);
                
                // check the existence of the keystore file
                if (httpsKeystore.exists()) {
                    soapConfig.setHttpsKeytoreFile(httpsKeystoreFile);
                    soapConfig.setHttpsKeytoreType(ComponentPropertiesHelper
                            .getHttpsKeystoreType(extensions));
                    soapConfig.setHttpsKeytorePassword(ComponentPropertiesHelper
                            .getHttpsKeystorePassword(extensions));
                    soapConfig.setHttpsKeyPassword(ComponentPropertiesHelper
                            .getHttpsKeyPassword(extensions));

                    // check the existence of the truststore file
                    String httpsTruststoreFile = ComponentPropertiesHelper
                            .getHttpsTruststoreFile(extensions);
                    if (httpsTruststoreFile != null && !httpsTruststoreFile.trim().equals("")) {
                        File httpsTruststore = new File(httpsTruststoreFile);
                        if (httpsTruststore.exists()) {
                            soapConfig.setHttpsTruststoreType(ComponentPropertiesHelper
                                    .getHttpsTruststoreType(extensions));
                            soapConfig.setHttpsTruststoreFile(httpsTruststoreFile);
                            soapConfig.setHttpsTruststorePassword(ComponentPropertiesHelper
                                    .getHttpsTruststorePassword(extensions));
                        } else {
                            if (logger.isLoggable(Level.WARNING)) {
                                logger
                                        .log(
                                                Level.WARNING,
                                                "HTTPS Client authentication in the consumer "
                                                        + "role is disabled because the truststore file does not exist.");
                            }
                        }
                    }
                } else {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING,
                                "HTTPS is disabled because the keystore file does not exist.");
                    }
                    isHttpsEnabled = false;
                }
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "HTTPS is disabled because the keystore file is not correctly set.");
                }
                isHttpsEnabled = false;
            }
        }
        soapConfig.setHttpsEnabled(isHttpsEnabled);
    }

    public Set<String> getAddresses() {
        return this.addresses;
    }

    /**
     * Get the URL where services can be accessed
     * 
     * @return
     */
    public SoapServerConfig getSoapServerConfig() {
        return this.serverConfig;
    }

    /**
     * Starts the listener manager : Start the embedded HTTP server and
     * initialize the dispatcher.
     * 
     * @param axisConfiguration
     * 
     * @throws AxisFault
     * @throws JBIException
     * @throws MissingResourceException
     */
    public void start(AxisConfiguration axisConfiguration) throws AxisFault,
            MissingResourceException, JBIException {

        // Get a specific logger for the HTTP server
        Logger jettyLogger = this.component.getContext().getLogger("jetty", null);

        /*
         * Init and start the HTTP server which will handle external SOAP
         * request (only if the specified host is valid). The axis2.xml defines
         * a Petals dispatcher that will catch incoming requests.
         */
        this.httpServer = new AxisServletServer(jettyLogger, this.serverConfig, this.soapContext.getAxis2ConfigurationContext());
        if (this.serverConfig.isValidHostName()) {
            this.httpServer.start();
        } else if (this.logger.isLoggable(Level.WARNING)) {
            this.logger.log(Level.WARNING,
                    "Specified host name in component isn't valid, consequently "
                            + "the HTTP server is not started");
        }

        if (this.serverConfig.isValidHostName() && this.logger.isLoggable(Level.INFO)) {
            this.logger.log(Level.INFO, "Component Information is available at "
                    + this.serverConfig.getBaseURL());
        }
    }

    /**
     * Stops the listener manager : Stop the embedded HTTP server that handles
     * incoming requests.
     * 
     * @throws AxisFault
     */
    public void stop() throws AxisFault {
        this.httpServer.stop();
    }

    public AxisServletServer getHttpServer() {
        return this.httpServer;
    }
}
