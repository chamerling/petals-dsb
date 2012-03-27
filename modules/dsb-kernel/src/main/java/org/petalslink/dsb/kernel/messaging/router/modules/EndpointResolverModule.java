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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jbi.component.Component;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;

import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.descriptor.original.generated.LinkType;
import org.ow2.petals.jbi.messaging.control.ExchangeCheckerClient;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.jbi.messaging.routing.module.endpoint.EndpointOrderer;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.jbi.Adapter;
import org.petalslink.dsb.kernel.api.PetalsService;
import org.petalslink.dsb.kernel.api.messaging.EndpointSearchEngine;
import org.petalslink.dsb.kernel.api.messaging.SearchException;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointResolverModule implements SenderModule, PetalsService {

    protected LoggingUtil log;

    private EndpointSearchEngine endpointSearchEngine;

    private ConfigurationService configurationService;

    private ExchangeCheckerClient exchangeCheckerClient;

    /**
     * 
     */
    public EndpointResolverModule(LoggingUtil log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {

    }

    /**
     * {@inheritDoc}
     */
    public void setup() throws Exception {

    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() throws Exception {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.module.SenderModule#electEndpoints
     * (java.util.Map,
     * org.ow2.petals.jbi.component.context.ComponentContextImpl,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl)
     */
    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext,
            org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper exchange) throws RoutingException {
        this.log.call();

        // if we are a consumer ie we want to invoke a service
        if (Role.CONSUMER.equals(exchange.getRole())) {
            List<ServiceEndpoint> endpoints = this.resolveEndpoints(sourceComponentContext
                    .getComponent(), exchange);
            for (ServiceEndpoint endpoint : endpoints) {
                electedEndpoints.put(endpoint, new TransportSendContext(endpoint.getLocation()));
            }
        } else {
            // CHA 2012 update : CAST taken from endpointreslover!
            ServiceEndpoint serviceEndpoint = (ServiceEndpoint) exchange.getConsumerEndpoint();
            electedEndpoints.put(serviceEndpoint, new TransportSendContext(
                    serviceEndpoint.getLocation()));
        }
    }

    /**
     * Resolve the address if necessary. Resolution consist in finding an
     * endpoint satisfying <code>interface</code> or <code>service</code>. the
     * source of the exchange and the exchange help the
     * <code>AddressResolver</code> to find the endpoint. The
     * <code>containerName</code> and <code>ComponentName</code> properties are
     * set. If <code>containerName</code> and <code>ComponentName</code> already
     * exist, do nothing. The check of acceptance from the two part is done here
     * (the Component method isExchangeWithXXXOkay()).
     * 
     * @param source
     *            the component context
     * @param exchange
     *            the exchange
     * @throws RoutingException
     *             no destination can be found, or problem accessing the
     *             registry
     */
    private List<ServiceEndpoint> resolveEndpoints(final Component sourceComponent,
            final MessageExchangeWrapper exchange) throws RoutingException {
        this.log.start();

        final String strategy = (String) exchange
                .getProperty(org.ow2.petals.jbi.messaging.routing.module.EndpointResolverModule.PROPERTY_STRATEGY_PROTOCOLS);

        // get message exchange values
        ServiceEndpoint givenEndpoint = (ServiceEndpoint) exchange.getEndpoint();
        QName givenServiceName = exchange.getService();
        QName givenInterfaceName = exchange.getInterfaceName();

        String linkType = LinkType.STANDARD.value();
        if (exchange.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_LINKTYPE) != null) {
            linkType = (String) exchange
                    .getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_LINKTYPE);
        }

        // Construct an array of potential matching endpoints.
        // If the endpoint has been set in the messageExchange,
        // this array contains only one element, which is this endpoint.
        List<ServiceEndpoint> electedEndpoints = new ArrayList<ServiceEndpoint>();

        if (givenEndpoint != null) {
            // Case 1 : The endpoint is explicit

            ServiceEndpoint targetEndpoint = null;
            try {
                org.petalslink.dsb.api.ServiceEndpoint searchResultEndpoint = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenEndpoint(
                                Adapter.createDSBServiceEndpoint(givenEndpoint), linkType);
                targetEndpoint = Adapter.createJBIServiceEndpoint(searchResultEndpoint);
            } catch (SearchException e) {
                throw new RoutingException(e);
            }
            if (targetEndpoint != null) {
                electedEndpoints.add(targetEndpoint);
            }
        } else if (givenServiceName != null) {
            // Case 2 : The endpoint is implicit, the service name is set.
            try {
                List<org.petalslink.dsb.api.ServiceEndpoint> searchResultEndpoints = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenServiceName(givenServiceName, strategy,
                                linkType);

                if (searchResultEndpoints != null) {
                    for (org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint : searchResultEndpoints) {
                        electedEndpoints.add(Adapter.createJBIServiceEndpoint(serviceEndpoint));
                    }
                }

            } catch (SearchException e) {
                throw new RoutingException(e);
            }

        } else if (givenInterfaceName != null) {
            /*
             * Case 3 : Nor the endpoint nor the service have been specified in
             * the message exchange. Get all the endpoints which resolves the
             * given interface;
             */
            try {
                List<org.petalslink.dsb.api.ServiceEndpoint> searchResultEndpoints = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenInterfaceName(givenInterfaceName, strategy,
                                linkType);
                if (searchResultEndpoints != null) {
                    for (org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint : searchResultEndpoints) {
                        electedEndpoints.add(Adapter.createJBIServiceEndpoint(serviceEndpoint));
                    }
                }
                
            } catch (SearchException e) {
                throw new RoutingException(e);
            }

        }

        // filter the endpoints
        List<ServiceEndpoint> endpoints = new ArrayList<ServiceEndpoint>();
        if (electedEndpoints != null) {
            for (ServiceEndpoint serviceEndpoint : electedEndpoints) {
                if (this.controlAcceptationExchange(sourceComponent, serviceEndpoint, exchange)) {
                    endpoints.add(serviceEndpoint);
                }
            }
        }

        if ((endpoints.size() == 0)) {
            // has not failed yet... we can still trust in the Federation or in any other routing module...
//            throw new RoutingException(
//                    "Failed to find a destination for the MessageExchange with id: "
//                            + exchange.getExchangeId());
        }

        this.log.end();
        return endpoints;
    }

    private boolean controlAcceptationExchange(final Component consumer,
            final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint providerEP,
            final MessageExchangeWrapper exchange) {
        this.log.call();

        boolean result = false;
        if (this.configurationService.getContainerConfiguration().isExchangeValidation()) {
            if (consumer.isExchangeWithProviderOkay(providerEP, exchange)
                    && this.exchangeCheckerClient.isExchangeWithConsumerOkayForComponent(
                            providerEP, exchange)) {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * @param endpointSearchEngine
     *            the endpointSearchEngine to set
     */
    public void setEndpointSearchEngine(EndpointSearchEngine endpointSearchEngine) {
        this.endpointSearchEngine = endpointSearchEngine;
    }

    /**
     * @return the endpointSearchEngine
     */
    public EndpointSearchEngine getEndpointSearchEngine() {
        return this.endpointSearchEngine;
    }

    public ConfigurationService getConfigurationService() {
        return this.configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ExchangeCheckerClient getExchangeCheckerClient() {
        return this.exchangeCheckerClient;
    }

    public void setExchangeCheckerClient(ExchangeCheckerClient exchangeCheckerClient) {
        this.exchangeCheckerClient = exchangeCheckerClient;
    }
}
