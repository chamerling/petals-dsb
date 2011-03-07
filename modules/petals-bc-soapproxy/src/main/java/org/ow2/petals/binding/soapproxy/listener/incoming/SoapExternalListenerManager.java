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

package org.ow2.petals.binding.soapproxy.listener.incoming;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;

import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.engine.Phase;
import org.ow2.petals.binding.soapproxy.SoapComponentContext;
import org.ow2.petals.binding.soapproxy.listener.incoming.jetty.SoapServer;
import org.ow2.petals.binding.soapproxy.util.ComponentPropertiesHelper;
import org.ow2.petals.binding.soapproxy.util.NetworkUtil;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

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

    protected DeliveryChannel channel;

    protected AbstractComponent component;

    protected SoapServer httpServer;

    protected SoapServerConfig serverConfig;

    protected SoapComponentContext soapContext;

    protected PetalsReceiver petalsReceiver;

    /**
     * Creates a new instance of {@link SoapExternalListenerManager}
     * 
     * @param ccontext
     * @param dchannel
     * @param bindingSUM
     * @param soapContext
     * @param propertiesManager
     * @param log
     */
    public SoapExternalListenerManager(final AbstractComponent component,
            final DeliveryChannel dchannel, final SoapComponentContext soapContext,
            final PetalsReceiver petalsReceiver, final Logger log) {
        super();
        this.logger = log;
        this.addresses = new HashSet<String>();
        this.channel = dchannel;
        this.component = component;
        this.soapContext = soapContext;
        this.serverConfig = this.createServerConfig();
        this.petalsReceiver = petalsReceiver;
    }

    /**
     * Starts the listener manager : Start the embedded HTTP server and
     * initialize the dispatcher.
     * 
     * @throws AxisFault
     */
    public void start() throws AxisFault {

        // start the axis http server
        this.startHttpServer();

        /*
         * The PetalsDispatcher object used by Axis is dynamicaly created by the
         * Axis http server. Some attributes have to be set to this object to
         * help it working well. The PetalsDispatcher object is retieved from
         * the axis2 http server configuration.
         */
        final PetalsDispatcher petalsDispatcher = this
                .retrievePetalsDispatcherFromAxisConfiguration();
        petalsDispatcher.init(this.component, this.channel, this.petalsReceiver,
                this.logger);

        this.logger.info("Component Information is available at " + this.serverConfig.getBaseURL());
    }

    /**
     * Stops the listener manager : Stop the embedded HTTP server that handles
     * incoming requests.
     * 
     * @throws AxisFault
     */
    public void stop() throws AxisFault {
        this.stopHttpServer();
    }

    /**
     * Init and start the HTTP server which will handle external SOAP request.
     * The axis2.xml defines a Petals dispatcher that will catch incoming
     * requests.
     * 
     * @throws AxisFault
     */
    protected void startHttpServer() throws AxisFault {
        // start the Jetty server
        this.httpServer = new SoapServer(this.serverConfig, this.soapContext, this.logger);
        this.httpServer.start();
    }

    /**
     * Stop the HTTP server
     * 
     */
    protected void stopHttpServer() throws AxisFault {
        this.httpServer.stop();
    }

    /**
     * Retrieve from the AxisConfiguration the PetalsDispatcher object.
     * (AxisConfiguration/GlobalInFlow/Dispatch/PetalsDispatcher)
     * 
     * @return PetalsDispatcher object
     * @throws AxisFault
     *             PetalsDispatcher object not found
     * 
     */
    protected PetalsDispatcher retrievePetalsDispatcherFromAxisConfiguration() throws AxisFault {

        PetalsDispatcher petalsDispatcher = null;

        final List<?> axisPhases = this.httpServer.getConfigurationContext().getAxisConfiguration()
                .getInFlowPhases();

        for (final Iterator<?> iter = axisPhases.iterator(); iter.hasNext()
                && (petalsDispatcher == null);) {

            final Phase phase = (Phase) iter.next();

            if (phase.getPhaseName().equalsIgnoreCase("Dispatch")) {
                for (final Iterator<?> iterator = phase.getHandlers().iterator(); iterator
                        .hasNext()
                        && (petalsDispatcher == null);) {

                    final Handler handler = (Handler) iterator.next();

                    if (handler.getName().toString().equalsIgnoreCase("PetalsDispatcher")) {
                        petalsDispatcher = (PetalsDispatcher) handler;
                    }
                }
            }
        }

        if (petalsDispatcher == null) {
            throw new AxisFault(
                    "The PetalsDispatcher object can not be retrieved from the AxisConfiguration.");
        }
        return petalsDispatcher;
    }

    /**
     * Create the SOAP server configuration
     * 
     * @return
     */
    private SoapServerConfig createServerConfig() {
        final SoapServerConfig soapConfig = new SoapServerConfig();
        ConfigurationExtensions extensions = this.component.getComponentExtensions();
        soapConfig.setPort(ComponentPropertiesHelper.getHttpPort(extensions));
        soapConfig.setProvidesList(ComponentPropertiesHelper.isProvidingServicesList(extensions));
        soapConfig.setProtocol(ComponentPropertiesHelper.getProtocol());
        soapConfig.setJettyThreadMaxPoolSize(ComponentPropertiesHelper
                .getHttpThreadMaxPoolSize(extensions));
        soapConfig.setJettyThreadMinPoolSize(ComponentPropertiesHelper
                .getHttpThreadMinPoolSize(extensions));
        soapConfig.setJettyAcceptors(ComponentPropertiesHelper.getHttpAcceptors(extensions));
        soapConfig.setServicesContext(ComponentPropertiesHelper.getServicesContext(extensions));
        soapConfig.setServicesMapping(ComponentPropertiesHelper.getServicesMapping(extensions));

        String host = ComponentPropertiesHelper.getHttpHostName(extensions);
        this.validateHost(soapConfig, host);
        return soapConfig;
    }

    /**
     * 
     * @param config
     */
    protected void validateHost(SoapServerConfig config, String host) {
        // InetAddress localhost = null;
        // try {
        // localhost = InetAddress.getLocalHost();
        // } catch (UnknownHostException e1) {
        // this.logger.fine("Can not get localhost InetAddress");
        // }

        if ((host == null) || (host.length() == 0) || host.equals("null")) {
            // no host specified, do not restrict and use all addresses
            config.addAddresses(NetworkUtil.getAllIPv4InetAddresses());
            config.setRestrict(false);
        } else {
            // host specified
            // 1. Check if we can resolve the specified host name
            InetAddress address = null;
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                this.logger.warning("Host name '" + host
                        + "' can not be resolved, using the wildcard address");
                config.addAddresses(NetworkUtil.getAllIPv4InetAddresses());
                config.setRestrict(false);
                return;
            }

            // address exists; check if it is a local one
            if (NetworkUtil.isLocalAddress(address)) {
                // yes : restrict access
                config.addAddress(address);
                config.setRestrict(true);
            } else {
                this.logger.warning(address
                        + " is not a valid local address, using the wildcard one");
                // no : set to localhost and do not restrict (FIXME : To be
                // defined)
                config.setRestrict(false);
                config.addAddresses(NetworkUtil.getAllIPv4InetAddresses());
            }
        }
    }

    /**
     * Get the URL where services can be accessed
     * 
     * @return
     */
    public SoapServerConfig getSoapServerConfig() {
        return this.serverConfig;
    }

    public Set<String> getAddresses() {
        return this.addresses;
    }

}
