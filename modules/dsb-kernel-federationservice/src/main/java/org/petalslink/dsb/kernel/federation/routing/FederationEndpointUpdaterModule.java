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
package org.petalslink.dsb.kernel.federation.routing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.kernel.configuration.SubDomainConfiguration;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.federation.FederationEngine;


/**
 * Update the endpoint location if the endpoints belong to the federation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = SenderModule.class) })
public class FederationEndpointUpdaterModule implements SenderModule {

    private static final String FEDERATION_TRANSPORT = "fdsb";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "topology", signature = TopologyService.class)
    private TopologyService topologyService;

    @Requires(name = "federationengine", signature = FederationEngine.class)
    private FederationEngine federationEngine;

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
    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        
        
        if (!this.federationEngine.isActivated()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Federation is not available");
            }
            return;
        }

        Set<String> availableSubdomains = new HashSet<String>();
        Set<SubDomainConfiguration> subdomains = this.topologyService.getSubDomainsConfiguration();
        for (SubDomainConfiguration subDomainConfiguration : subdomains) {
            availableSubdomains.add(subDomainConfiguration.getName());
        }
        
        // TODO : check the federation from the topology
        // if the service endpoint domain is not found in the current topology,
        // let's say that it is a federation endpoint

        for (ServiceEndpoint serviceEndpoint : electedEndpoints.keySet()) {
            TransportSendContext context = electedEndpoints.get(serviceEndpoint);
          
            if (!availableSubdomains.contains(context.destination.getSubdomainName())) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Updating the transport to use as federation one");
                }
                context.transport = FEDERATION_TRANSPORT;
            }
            
            
        }
        
    }
}



