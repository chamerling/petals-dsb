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
package org.petalslink.dsb.kernel.messaging.router.modules;

import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.control.ExchangeCheckerClient;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.messaging.EndpointSearchEngine;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = SenderModule.class))
public class FractalEndpointResolverModule implements SenderModule {

    /**
     * Logger wrapper.
     */
    protected LoggingUtil log;

    /**
     * The logger.
     */
    @Monolog(name = "logger")
    protected Logger logger;

    /**
     * The Topology service fractal component
     */
    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    protected ConfigurationService configurationService;

    @Requires(name = "checker", signature = org.ow2.petals.jbi.messaging.control.ExchangeCheckerClient.class)
    protected ExchangeCheckerClient exchangeCheckerClient;

    @Requires(name = "endpoint-search-engine", signature = EndpointSearchEngine.class)
    private EndpointSearchEngine endpointSearchEngine;

    private org.petalslink.dsb.kernel.messaging.router.modules.EndpointResolverModule delegate;

    /**
     * Start the Fractal component
     */
    @LifeCycle(on = LifeCycleType.START)
    protected void start() throws Exception {
        this.log = new LoggingUtil(this.logger);
        this.log.call();

        this.delegate = new org.petalslink.dsb.kernel.messaging.router.modules.EndpointResolverModule(
                this.log);
        this.delegate.setConfigurationService(this.configurationService);
        this.delegate.setEndpointSearchEngine(this.endpointSearchEngine);
        this.delegate.setExchangeCheckerClient(this.exchangeCheckerClient);

        this.delegate.init();
        this.delegate.setup();
    }

    /**
     * Stop the Fractal component
     */
    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws RoutingException {
        this.log.call();
        try {
            this.delegate.shutdown();
        } catch (Exception e) {
            throw new RoutingException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        this.delegate.electEndpoints(electedEndpoints, sourceComponentContext, exchange);
    }
}
