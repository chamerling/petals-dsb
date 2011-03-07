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

package org.ow2.petals.binding.soapproxy.listener.incoming.jetty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.TransportListener;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.BoundedThreadPool;
import org.ow2.petals.binding.soapproxy.SoapComponentContext;
import org.ow2.petals.binding.soapproxy.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soapproxy.listener.incoming.servlet.ListServicesServlet;
import org.ow2.petals.binding.soapproxy.listener.incoming.servlet.SoapServlet;
import org.ow2.petals.binding.soapproxy.listener.incoming.servlet.WelcomeServlet;

public class SoapServer implements TransportListener {

    protected Logger logger;

    /**
     * The axis2 configurat configurationContext;
     * 
     * /** The component properties
     */
    protected SoapServerConfig config;

    /**
     * The Jetty server
     */
    protected Server server;

    /**
     * The jetty thread pool. Size is configured from component configuration.
     */
    protected BoundedThreadPool threadPool;

    protected ServerStats stats;

    private ConfigurationContext configurationContext;

    public static final int HEADER_BUFFER_SIZE = 4096 * 4;

    /**
     * 
     */
    static {
        JettyLogger.init();
    }

    /**
     * Creates a new instance of {@link SoapServer}
     * 
     * @param configContext
     * @param config
     * @param componentContext
     * @param logger
     * @throws AxisFault
     */
    public SoapServer(final SoapServerConfig config, final SoapComponentContext componentContext,
            final Logger logger) throws AxisFault {
        this.config = config;
        this.logger = logger;
        this.configurationContext = componentContext.getAxis2ConfigurationContext();

        // jetty threapool configuration
        this.threadPool = new BoundedThreadPool();
        this.threadPool.setName("BCSoapProxyJettyThreadPo");

        this.threadPool.setMaxThreads(this.config.getJettyThreadMaxPoolSize());
        this.threadPool.setMinThreads(this.config.getJettyThreadMinPoolSize());

        // jetty http connector configuration
        final SelectChannelConnector nioConnector = new SelectChannelConnector();
        nioConnector.setPort(this.config.getPort());

        // If we assign the host, we will only be able to contact server
        // on it. No value or a null one is a wildcard so connection is possible
        // on network interface
        // @see java.net.InetSocketAddress
        if (this.config.isRestrict()) {
            nioConnector.setHost(this.config.getHost());
        }

        nioConnector.setHeaderBufferSize(HEADER_BUFFER_SIZE);
        nioConnector.setStatsOn(false);
        nioConnector.setAcceptors(this.config.getJettyAcceptors());

        this.server = new Server();
        this.server.setConnectors(new Connector[] { nioConnector });
        this.server.setThreadPool(this.threadPool);

        this.stats = new ServerStats();

        // create context handlers
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        this.server.setHandler(contexts);

        // create the axis context
        final Context axisContext = new Context(contexts, "/" + this.config.getServicesContext(),
                Context.SESSIONS);
        axisContext.setErrorHandler(new ErrorHandler(false));

        // create axis servlet holder
        final ServletHolder axisServlet = new ServletHolder(new SoapServlet(
                this.configurationContext, this.stats));
        axisServlet.setName("AxisServlet");
        axisServlet.setInitOrder(1);
        axisContext.addServlet(axisServlet, "/" + this.config.getServicesMapping() + "/*");
        axisContext.addServlet(axisServlet, "/servlet/AxisServlet");

        // add our own simple service list servlet
        final ServletHolder listServlet = new ServletHolder(new ListServicesServlet(
                this.configurationContext, this.config));
        listServlet.setName("ServicesListServlet");
        listServlet.setInitOrder(1);
        axisContext.addServlet(listServlet, "/" + this.config.getServicesMapping() + "/"
                + ListServicesServlet.MAPPING_NAME);

        // add welcome servlet
        final Context welcomeContext = new Context(contexts, "/", Context.SESSIONS);
        final ServletHolder welcomeServlet = new ServletHolder(new WelcomeServlet(this.config,
                this.stats));
        welcomeServlet.setName("WelcomeServlet");
        welcomeServlet.setInitOrder(1);
        welcomeContext.addServlet(welcomeServlet, "/*");

        this.configureListenerManager();
    }

    /**
     * 
     * @return
     */
    public ConfigurationContext getConfigurationContext() {
        return this.configurationContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.TransportListener#getEPRsForService(java.lang
     * .String, java.lang.String)
     */
    public EndpointReference getEPRForService(final String serviceName, final String ip)
            throws AxisFault {
        return this.getEPRsForService(serviceName, ip)[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.TransportListener#getEPRsForService(java.lang
     * .String, java.lang.String)
     */
    public EndpointReference[] getEPRsForService(final String serviceName, final String ip)
            throws AxisFault {
        if (this.config != null) {
            return new EndpointReference[] { new EndpointReference(this.config.getProtocol()
                    + "://" + this.config.getHost() + ":" + this.config.getPort() + "/"
                    + this.configurationContext.getServiceContextPath() + "/" + serviceName) };
        } else {
            throw new AxisFault("Unable to generate EPR for the transport : http");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.TransportListener#init(org.apache.axis2.context
     * .ConfigurationContext,
     * org.apache.axis2.description.TransportInDescription)
     */
    public void init(final ConfigurationContext axisConf, final TransportInDescription transprtIn)
            throws AxisFault {
        this.configurationContext = axisConf;

        // These values are defined by the component and not by the Axis2
        // configuration file so we set them if Axis2 do something with them...
        Parameter portParam = transprtIn.getParameter(PARAM_PORT);
        if (portParam == null) {
            portParam = new Parameter(PARAM_PORT, Integer.toString(this.config.getPort()));
        }
        transprtIn.addParameter(portParam);

        Parameter hostParam = transprtIn.getParameter(HOST_ADDRESS);
        if (hostParam == null) {
            hostParam = new Parameter(HOST_ADDRESS, this.config.getHost());
        }
        transprtIn.addParameter(hostParam);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis2.transport.TransportListener#start()
     */
    public void start() throws AxisFault {
        this.logger.info("Starting Jetty server...");
        this.logger.info("Host : "
                + (!this.config.isRestrict() ? "*" : this.config.getHost() + " (restricted)")
                + " / Port : " + this.config.getPort() + " / Jetty Max poolsize : "
                + this.config.getJettyThreadMaxPoolSize() + " / Jetty Min poolsize : "
                + this.config.getJettyThreadMinPoolSize() + " / Jetty Acceptors size : "
                + this.config.getJettyAcceptors());
        try {
            this.stats.setStartTime(System.currentTimeMillis());
            this.server.start();
        } catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Can not start the Jetty server");
            throw new AxisFault("Can not start the Jetty server", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis2.transport.TransportListener#stop()
     */
    public void stop() throws AxisFault {
        this.logger.log(Level.INFO, "Stopping Jetty server...");

        try {
            // stop jetty server
            this.server.stop();
            this.stats.setStopTime(System.currentTimeMillis());
        } catch (final InterruptedException e) {
            throw AxisFault.makeFault(e);
        } catch (final Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Configure the listener manager used by Axis
     * 
     * @throws AxisFault
     */
    private void configureListenerManager() throws AxisFault {
        ListenerManager listenerManager = this.configurationContext.getListenerManager();
        final TransportInDescription trsIn = new TransportInDescription(Constants.TRANSPORT_HTTP);
        trsIn.setReceiver(this);
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
            listenerManager.init(this.configurationContext);
        }
        listenerManager.addListener(trsIn, true);
    }

    public SessionContext getSessionContext(final MessageContext messageContext) {
        // TODO Auto-generated method stub
        return null;
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }
}
