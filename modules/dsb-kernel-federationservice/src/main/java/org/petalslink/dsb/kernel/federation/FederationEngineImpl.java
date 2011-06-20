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

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.client.FederationClientWithCallback;
import org.petalslink.dsb.kernel.DSBConfigurationService;


/**
 * The federation engine implementation...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationEngine.class) })
public class FederationEngineImpl implements FederationEngine {

    @Monolog(name = "logger")
    private Logger logger;

    @Requires(name = "federationconfiguration", signature = FederationConfigurationService.class)
    private FederationConfigurationService fedConfigurationService;

    @Requires(name = "federationclientregistry", signature = FederationClientRegistry.class)
    private FederationClientRegistry federationClientRegistry;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
        // FIXME : There is something wrong when the registry is stopping before
        // the engine --> NPE and unable to disconnect from the federation!!!
        // this.disconnectFromFederation();
        // TODO : call stop on the federation client???
    }

    /**
     * {@inheritDoc}
     */
    public FederationClientWithCallback getFederationClient() {
        if (!this.isActivated()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Federation is not activated");
            }
            return null;
        }
        return this.getFederationClientWithCallback();
    }

    /**
     * {@inheritDoc}
     */
    public void connectFederation() {
        if (!this.isActivated()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Federation is not activated");
            }
            return;
        }

        // get a client for the federation. A container just belongs to one
        // federation for now... The choice of the client is based on the
        // federation URL protocol.
        FederationClientWithCallback client = this.getFederationClientWithCallback();
        if (client != null) {
            client.start();
            try {
                client.join();
            } catch (FederationException e) {
                this.log.warning(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnectFromFederation() {
        if (!this.isActivated()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Federation is not activated");
            }
            return;
        }

        FederationClientWithCallback client = this.getFederationClientWithCallback();
        if (client != null) {
            try {
                client.leave();
            } catch (FederationException e) {
                this.log.warning(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated() {
        return this.fedConfigurationService.isActive();
    }

    private FederationClientWithCallback getFederationClientWithCallback() {
        URI uri = URI.create(this.fedConfigurationService.getFederationURL());
        return this.federationClientRegistry.getFederationClient(uri.getScheme());
    }
}
