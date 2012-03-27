/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
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

package org.ow2.petals.binding.soap.listener.incoming.jetty;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soap.listener.incoming.servlet.ListServicesServlet;
import org.ow2.petals.binding.soap.listener.incoming.servlet.SoapServlet;
import org.ow2.petals.binding.soap.listener.incoming.servlet.WelcomeServlet;

/**
 * Create a Jetty server that will be the Axis 2 transport listener. This Jetty
 * server is used to replace the SimpleHTTPServer provided by Axis that have
 * quite bad performances.
 * 
 * @author Christophe Hamerling - EBM WebSourcing
 * 
 */
public class AxisServletServer {

    /**
     * The logger
     */
    private final Logger logger;

    /**
     * the Jetty server
     */
    private SoapServletServer server;

    /**
     * The component properties
     */
    private SoapServerConfig config;

    /**
     * The Jetty server statistics
     */
    private ServerStats stats;

    /**
     * Create a SOAP server
     * 
     * @param logger
     *            the logger
     * @param config
     *            the SOAP server configuration
     * @param configContext
     *            Axis 2 configuration context
     * @throws AxisFault
     *             impossible to set Jetty as SOAP transport listener
     */
    public AxisServletServer(final Logger logger, final SoapServerConfig config,
            final ConfigurationContext configContext) throws AxisFault {

        this.config = config;
        this.logger = logger;
        this.stats = new ServerStats();

        String restrictedIp = null;
        if (config.isRestricted()) {
            restrictedIp = config.getHostAddress();
        }

        HTTPConfig httpConfig = new HTTPConfig(restrictedIp, config.getHttpPort(),
                config.getJettyAcceptors());

        HTTPSConfig httpsConfig = null;
        if (config.isHttpsEnabled()) {
            HTTPSTruststoreConfig httpsTruststoreConfig = new HTTPSTruststoreConfig(
                    config.getHttpsTruststoreType(), config.getHttpsTruststoreFile(),
                    config.getHttpsTruststorePassword());
            HTTPSKeystoreConfig httpsKeystoreConfig = new HTTPSKeystoreConfig(
                    config.getHttpsKeystoreKeyPassword(), config.getHttpsKeystorePassword(),
                    config.getHttpsKeystoreFile(), config.getHttpsKeystoreType());

            httpsConfig = new HTTPSConfig(restrictedIp, config.getHttpsPort(),
                    config.getJettyAcceptors(), httpsTruststoreConfig, httpsKeystoreConfig);
        }

        ServletServerConfig ssc = new ServletServerConfig(config.getServicesMapping(),
                config.getServicesContext(), config.getJettyThreadMaxPoolSize(),
                config.getJettyThreadMinPoolSize(), httpConfig, httpsConfig);

        this.server = new SoapServletServer(ssc,
                createSoapServicesDispatcherServlet(configContext),
                createSoapServicesListingServlet(configContext), createWelcomeServlet(), logger);
    }

    private HttpServlet createSoapServicesListingServlet(ConfigurationContext configContext) {
        return new ListServicesServlet(configContext, config);
    }

    private HttpServlet createWelcomeServlet() {
        return new WelcomeServlet(config, stats);
    }

    private HttpServlet createSoapServicesDispatcherServlet(ConfigurationContext configContext) {
        return new SoapServlet(this.logger, configContext, this.stats, this.config);
    }

    /**
     * Start the SOAP server
     * 
     * @throws AxisFault
     *             the SOAP server can not start
     */
    public void start() throws AxisFault {
        if (this.logger.isLoggable(Level.INFO)) {
            this.logger.info("Starting Jetty server...");
            this.logger.info(this.config.getHostToDisplay() + " - HTTP Port : "
                    + this.config.getHttpPort() + " - HTTPS Port : " + this.config.getHttpsPort()
                    + " - Jetty Max poolsize : " + this.config.getJettyThreadMaxPoolSize()
                    + " - Jetty Min poolsize : " + this.config.getJettyThreadMinPoolSize()
                    + " - Jetty Acceptors size : " + this.config.getJettyAcceptors());
        }
        try {
            this.stats.setStartTime(System.currentTimeMillis());
            this.server.start();
        } catch (final Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                this.logger.log(Level.SEVERE, "Can not start the Jetty server");
            }
            throw new AxisFault("Can not start the Jetty server", e);
        }
    }

    /**
     * Stop the SOAP server
     * 
     * @throws AxisFault
     *             the SOAP server can not stop
     */
    public void stop() throws AxisFault {
        if (this.logger.isLoggable(Level.INFO)) {
            this.logger.log(Level.INFO, "Stopping Jetty server...");
        }

        try {
            this.server.stop();
            this.stats.setStopTime(System.currentTimeMillis());
        } catch (final InterruptedException e) {
            throw AxisFault.makeFault(e);
        } catch (final Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Return if the SOAP server is running
     * 
     * @return true if the SOAP server is running, otherwise false
     */
    public boolean isRunning() {
        return this.server.isRunning();
    }

    /**
     * Redirect specified URI to the specified web service address
     * 
     * @param from
     *            The URI to redirect
     * @param to
     *            The serviceName or address of the destination service
     */
    public void addRedirect(String from, String to) {
        this.config.addRedirect(from,
                "/" + this.config.getServicesContext() + "/" + this.config.getServicesMapping()
                        + "/" + to);
    }

    /**
     * Remove the redirection
     * 
     * @param from
     *            the URL to redirect
     */
    public void removeRedirect(String from) {
        this.config.removeRedirect(from);
    }

    public void addServlet(HttpServlet servlet) {

    }
}
