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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jbi.component.Component;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.descriptor.original.generated.LinkType;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.jbi.messaging.routing.module.endpoint.EndpointOrderer;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.federation.FederationEngine;
import org.petalslink.dsb.kernel.messaging.EndpointSearchEngine;


/**
 * Get endpoints from the federation using the federation search engine.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = SenderModule.class))
public class FederationEndpointResolverModule implements SenderModule {

    private static final String FEDERATION_TRANSPORT = "fdsb";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    // @Requires(name = "dsbconfiguration", signature =
    // DSBConfigurationService.class)
    // private DSBConfigurationService dsbconfigurationService;

    @Requires(name = "federationendpointsearchengine", signature = EndpointSearchEngine.class)
    private EndpointSearchEngine federationEndpointResolver;

    @Requires(name = "federationengine", signature = FederationEngine.class)
    private FederationEngine federationEngine;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting... ");
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

        this.log.start();


        if (!electedEndpoints.isEmpty()) {
            /*            this.log.debug(" #############  Endpoints not empty:");
            for (Map.Entry<ServiceEndpoint, TransportSendContext > ep : electedEndpoints.entrySet()){
                ep.getValue().transport = FEDERATION_TRANSPORT;
                System.out.println("##" + ep.getKey().getEndpointName() + "## on ####  " + ep.getKey().getLocation() + " >>> to be sent through >>> " + ep.getValue().transport);
            }
            this.log.debug(" ############# --");*/
            return;
        }

        // TODO : CODE THE FEDERATION ENDPOINT RESOLVER HERE
        this.log.debug("  No endpoints found so far ... resorting to the Federation");

        if (Role.CONSUMER.equals(exchange.getRole())) {
            List<ServiceEndpoint> endpoints = this.resolveEndpoints(sourceComponentContext
                    .getComponent(), exchange);
            if (endpoints != null) {
                for (ServiceEndpoint endpoint : endpoints) {
                    electedEndpoints.put(endpoint, new TransportSendContext(endpoint.getLocation()));
                }
            }
        } else {
            electedEndpoints.put(exchange.getConsumerEndpoint(), new TransportSendContext(exchange.getConsumerEndpoint().getLocation()));
        }

    }

    private List<ServiceEndpoint> resolveEndpoints(final Component sourceComponent,
            final MessageExchange exchange) throws RoutingException {
        this.log.start();

        List<ServiceEndpoint> electedEndpoints = new ArrayList<ServiceEndpoint>();

        EndpointSearchEngine endpointSearchEngine = this.getEndpointSearchEngine();
        if (endpointSearchEngine == null) {
            this.log.warning("Can not find the remote seach engine");
            return electedEndpoints;
        }

        final String strategy = (String) exchange
        .getProperty(EndpointOrderer.PROPERTY_STRATEGY_PROTOCOLS);

        // get message exchange values
        ServiceEndpoint givenEndpoint = (ServiceEndpoint) exchange.getEndpoint();
        QName givenServiceName = exchange.getService();
        QName givenInterfaceName = exchange.getInterfaceName();

        String linkType = LinkType.STANDARD.value();
        if (exchange.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_LINKTYPE) != null) {
            linkType = (String) exchange
            .getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_LINKTYPE);
        }

        if (givenEndpoint != null) {
            // Case 1 : The endpoint is explicit
            ServiceEndpoint targetEndpoint = endpointSearchEngine
            .getTargetedEndpointFromGivenEndpoint(givenEndpoint, linkType);
            electedEndpoints.add(targetEndpoint);

        } else if (givenServiceName != null) {
            // Case 2 : The endpoint is implicit, the service name is set.
            electedEndpoints = endpointSearchEngine.getTargetedEndpointFromGivenServiceName(
                    givenServiceName, strategy, linkType);

        } else if (givenInterfaceName != null) {
            /*
             * Case 3 : Nor the endpoint nor the service have been specified in
             * the message exchange. Get all the endpoints which resolves the
             * given interface;
             */
            electedEndpoints = endpointSearchEngine.getTargetedEndpointFromGivenInterfaceName(
                    givenInterfaceName, strategy, linkType);

        }

        if ((electedEndpoints == null) || (electedEndpoints.size() == 0)) {
            throw new RoutingException(
                    "Failed to find a destination for the MessageExchange with id: "
                    + exchange.getExchangeId());
        }

        this.log.end();
        return electedEndpoints;
    }

    /**
     * @return
     */
    private EndpointSearchEngine getEndpointSearchEngine() {
        return this.federationEndpointResolver;
    }
}
