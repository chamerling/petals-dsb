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
package org.petalslink.dsb.kernel;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Cardinality;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.api.server.PetalsStateListener;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.listener.LifeCycleManager;
import org.petalslink.dsb.kernel.management.binder.EmbeddedServiceBinder;
import org.petalslink.dsb.kernel.management.component.EmbeddedComponentService;
import org.petalslink.dsb.kernel.management.cron.EmbeddedServiceBinderCron;
import org.petalslink.dsb.kernel.management.cron.ServicePoller;
import org.petalslink.dsb.kernel.webapp.WebAppServer;
import org.petalslink.dsb.transport.api.Server;

/**
 * Invoke operations on all the services which are required to be launch after
 * petals startup.
 * 
 * FIXME : Refactor to user interface map and not direct binds to services to
 * call...
 * 
 * @deprecated : Use {@link LifeCycleManager} with annotations. This service is
 *             still here for backward compatibility but will be removed on 1.0
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = PetalsStateListener.class) })
public class DSBInitializer implements PetalsStateListener {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "embeddedcomponentservice", signature = EmbeddedComponentService.class)
    private EmbeddedComponentService embeddedComponentService;

    @Requires(name = "embeddedservicebinder", signature = EmbeddedServiceBinder.class)
    private EmbeddedServiceBinder embeddedServiceBinder;

    @Requires(name = "embeddedservicebindercron", signature = EmbeddedServiceBinderCron.class)
    private EmbeddedServiceBinderCron embeddedServiceBinderCron;

    @Requires(name = "servicepoller", signature = ServicePoller.class)
    private ServicePoller servicePoller;

    @Requires(name = "webappserver", signature = WebAppServer.class, contingency = Contingency.OPTIONAL)
    private WebAppServer webAppServer;

    //@Requires(name = "federationengine", signature = FederationEngine.class, contingency = Contingency.OPTIONAL)
    //private FederationEngine federationEngine;

    // @Requires(name = "transportserver", signature = Server.class)
    // private Server transportServer;

    @Requires(name = "transportserver", signature = Server.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.MANDATORY)
    private final Map<String, Object> server = new Hashtable<String, Object>();

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void onPetalsStarted() {
        this.log.info("DSB Initializer listener");

        // start the transport server
        this.log.info("Starting the transport servers");
        for (String k : this.server.keySet()) {
            this.log.info("Starting Transport Server " + k);
            ((Server) this.server.get(k)).startServer();
        }
        // this.transportServer.startServer();

        //if (this.federationEngine != null) {
        //    this.federationEngine.connectFederation();
        //}

        this.log.info("Installing required components...");
        this.embeddedComponentService.install();

        // bind the services to the bus
        this.log.info("Binding required services...");
        this.embeddedServiceBinder.bindAll();

        this.log.info("Starting embedded service background task");
        this.embeddedServiceBinderCron.execute();

        if (this.webAppServer != null) {
            this.log.info("Start the Web application server...");
            try {
                this.webAppServer.startServer();
            } catch (DSBException e) {
                this.log.warning("Can not start the Web Application, embedded management will not be available!");
            }
        }

        // start polling for new endpoints
        this.log.info("Start polling for new endpoints...");
        this.servicePoller.startPolling();

    }

    /**
     * {@inheritDoc}
     */
    public void onPetalsStopped(boolean success, Exception exception) {
        if (this.webAppServer != null) {
            this.log.info("Stop the HTTP server...");
            this.webAppServer.stopServer();
        }
    }

}
