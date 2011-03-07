/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.federation;

import java.net.URI;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.client.FederationService;
import org.petalslink.dsb.federation.core.api.ClientManager;
import org.petalslink.dsb.federation.core.client.FederationClientImpl;
import org.petalslink.dsb.federation.core.client.FederationClientWithCallback;
import org.petalslink.dsb.federation.core.commons.ClientManagerImpl;
import org.petalslink.dsb.federation.xmpp.commons.XMPPClientFactory;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationClientWithCallback.class) })
public class XMPPFederationClientImpl implements FederationClientWithCallback {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "federationservice", signature = FederationService.class)
    private FederationService federationService;

    @Requires(name = "federationconfiguration", signature = FederationConfigurationService.class)
    private FederationConfigurationService fedConfigurationService;

    private FederationClientWithCallback delegate;

    @LifeCycle(on = LifeCycleType.START)
    protected void startComponent() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stopComponent() {
        this.log.debug("Stopping...");

        // if we joined the federation, let's leave it now!!! This is used to
        // avoid blocking threads since stopping the container in the right
        // order is not available right now!
        // We look if the delegate is not null since it has been created by
        // another component on start call. If it is null, we do not try to
        // leave and stop the federation link...
        if (this.delegate != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Leave and stop the federation client");
            }
            try {
                this.delegate.leave();
            } catch (FederationException e) {
                e.printStackTrace();
            }
            this.delegate.stop();
        }
    }

    private FederationClientImpl createClient() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Create client");
        }
        String federationName = this.configurationService.getContainerConfiguration().getUser()
                + "@gmail.com";
        String password = this.configurationService.getContainerConfiguration().getPassword();

        if (this.log.isDebugEnabled()) {
            this.log.debug("Federation name is " + federationName);
            this.log.debug("Password is " + password);
        }

        // remove the xmpp:// or jabber:// from the federation URL...
        URI uri = URI.create(this.fedConfigurationService.getFederationURL());
        String federationServer = this.fedConfigurationService.getFederationURL();
        if ((uri.getScheme() != null) && uri.getScheme().equalsIgnoreCase("xmpp")) {
            federationServer = federationServer.substring("xmpp://".length());
        } else if ((uri.getScheme() != null) && uri.getScheme().equalsIgnoreCase("jabber")) {
            federationServer = federationServer.substring("jabber://".length());
        }

        if (this.log.isInfoEnabled()) {
            this.log.info("XMPP/Jabber Federation server is " + federationServer);
        }

        FederationClientImpl client = new FederationClientImpl(federationName, federationName,
                federationServer, federationServer);
        ClientManager clientManager = new ClientManagerImpl();
        clientManager.setClientFactory(new XMPPClientFactory());
        client.setClientManager(clientManager);
        client.setServiceManager(new org.petalslink.dsb.federation.xmpp.client.XMPPServiceManagerImpl(
                federationName, password));
        client.setServiceImplementation(this.federationService);
        return client;
    }

    private synchronized FederationClientWithCallback getClient() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Get client");
        }
        if (this.delegate == null) {
            this.delegate = this.createClient();
        }
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void onLookupResponse(Set<ServiceEndpoint> endpoints, String id)
            throws FederationException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("onLookupResponse");
        }
        this.getClient().onLookupResponse(endpoints, id);
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message) throws FederationException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("invoke");
        }
        this.getClient().invoke(message);
    }

    /**
     * {@inheritDoc}
     */
    public void join() throws FederationException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("join");
        }
        this.getClient().join();
    }

    /**
     * {@inheritDoc}
     */
    public void leave() throws FederationException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("leave");
        }
        this.getClient().leave();
    }

    /**
     * {@inheritDoc}
     */
    public Set<ServiceEndpoint> lookup(EndpointQuery query) throws FederationException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("lookup");
        }
        return this.getClient().lookup(query);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("start client");
        }
        this.getClient().start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("stop client");
        }
        this.getClient().stop();
    }
}
