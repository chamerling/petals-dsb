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
package org.petalslink.dsb.kernel.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.descriptor.original.generated.LinkType;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.endpoint.EndpointOrderer;
import org.ow2.petals.kernel.api.service.ServiceEndpoint.EndpointType;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.PetalsService;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointSearchEngineImpl implements EndpointSearchEngine, PetalsService {

    private final LoggingUtil log;

    /**
     * the different kind of research of a endpoint: by service or by interface.
     */
    private static enum kindSearch {
        /**
         * the research by service.
         */
        SERVICE_SEARCH,

        /**
         * the research by interface.
         */
        INTERFACE_SEARCH
    }

    /**
     * The default hardcoded strategy parameters: highest,3,2,1
     */
    public static final List<Object> DEFAULT_STRATEGY_PARAMETERS = Arrays.asList(new Object[] {
            EndpointOrderer.HIGHEST, new Float(3f), new Float(2f), new Float(1f) });

    /**
     * the default configured strategy parameters.
     */
    private static List<Object> defaultStrategyParameters;

    /**
     * The elected endpoint orderer
     */
    private EndpointOrderer endpointOrderer;

    /**
     * This random is used to configure the load balancing strategy.
     */
    private Random random;

    private TopologyService topologyService;

    private ConfigurationService configurationService;

    private EndpointRegistry endpointRegistry;

    /**
     * 
     */
    public EndpointSearchEngineImpl(LoggingUtil log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        ContainerConfiguration containerConfiguration = this.configurationService
                .getContainerConfiguration();

        // TODO not very clean to initialize the static variable like that
        try {
            defaultStrategyParameters = tokenizeAndAnalyseStrategy(containerConfiguration
                    .getRouterStrategy());
        } catch (final SearchException e) {
            defaultStrategyParameters = DEFAULT_STRATEGY_PARAMETERS;
        }

        this.endpointOrderer = new EndpointOrderer(this.log, containerConfiguration,
                this.topologyService);
        this.random = new Random();
    }

    /**
     * {@inheritDoc}
     */
    public void setup() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * Get the targeted endpoint from a given endpoint.
     * 
     * @param givenEndpoint
     *            the endpoint in the message exchange. May be null
     * @return the targeted endpoint. May be null
     * @throws RoutingException
     *             impossible to get the endpoint
     */
    public ServiceEndpoint getTargetedEndpointFromGivenEndpoint(
            final ServiceEndpoint givenEndpoint, String linktype) throws SearchException {
        this.log.call();

        ServiceEndpoint targetedEndpoint = null;

        if (givenEndpoint.getType() == EndpointType.INTERNAL) {
            if (LinkType.SOFT.value().equals(linktype)) {
                throw new SearchException("The target endpoint '"
                        + givenEndpoint.getEndpointName() + "' is not a SOFT link");
            } else {
                targetedEndpoint = givenEndpoint;
            }
        } else if (givenEndpoint.getType() == EndpointType.LINKED) {
            if (LinkType.HARD.value().equals(linktype)) {
                throw new SearchException("The target endpoint '"
                        + givenEndpoint.getEndpointName() + "' is not an HARD link");
            } else {
                try {
                    targetedEndpoint = this.endpointRegistry.getEndpoint(givenEndpoint
                            .getServiceName(), givenEndpoint.getEndpointName());
                } catch (RegistryException e) {
                    throw new SearchException(e);
                }
            }
        } else if (givenEndpoint.getType() == EndpointType.EXTERNAL) {
            targetedEndpoint = this.findEndpointInRegistry(givenEndpoint);
        }

        return targetedEndpoint;
    }

    /**
     * Find the endpoint in registry.
     * 
     * @param givenEndpoint
     *            the given endpoint. May be null.
     * @return the targeted endpoint. May be null.
     * @throws RoutingException
     *             Impossible to find the endpoint
     */
    private ServiceEndpoint findEndpointInRegistry(final ServiceEndpoint givenEndpoint)
            throws SearchException {
        this.log.call();

        ServiceEndpoint targetedEndpoint = null;
        try {
            targetedEndpoint = this.endpointRegistry.getEndpoint(givenEndpoint.getServiceName(),
                    givenEndpoint.getEndpointName());
        } catch (RegistryException e) {
            throw new SearchException(e.getMessage());
        }
        // no endpoint found, we can not find a destination
        if (targetedEndpoint == null) {
            throw new SearchException("The target endpoint '" + givenEndpoint.getEndpointName()
                    + "' does not match a registered endpoint");
        }

        return targetedEndpoint;
    }

    /**
     * Get the targeted endpoint from a given service name.
     * 
     * @param givenServiceName
     *            the given Service Name in the message exchange. May be null
     * @param exchange
     *            the exchange
     * @param component
     *            the component
     * @return the targeted endpoint following the specific strategy used. May
     *         be null
     * @throws RoutingException
     *             impossible to get the endpoint
     */
    public List<ServiceEndpoint> getTargetedEndpointFromGivenServiceName(
            final QName givenServiceName, final String strategy, final String linkType)
            throws SearchException {
        this.log.call();

        List<ServiceEndpoint> retrievedEndpoints = null;
        // find an Endpoint with the specified InterfaceName
        try {
            retrievedEndpoints = this.getEnabledEndpoints(kindSearch.SERVICE_SEARCH,
                    givenServiceName, linkType);
        } catch (RegistryException e) {
            throw new SearchException(e);
        }

        List<Object> strategyParameters = getAndAnalyseStrategy(strategy);

        List<ServiceEndpoint> orderedEndpoints = null;
        if (retrievedEndpoints.size() == 1) {
            orderedEndpoints = new ArrayList<ServiceEndpoint>(1);
            orderedEndpoints.add(retrievedEndpoints.get(0));
        } else if (retrievedEndpoints.size() > 1) {
            try {
                orderedEndpoints = this.endpointOrderer.orderEndpoints(retrievedEndpoints, null,
                        strategyParameters, this.random);
            } catch (RoutingException e) {
                throw new SearchException(e);
            }
        }

        if ((orderedEndpoints == null) || (orderedEndpoints.size() == 0)) {
            throw new SearchException("No endpoint found matching the target service '"
                    + givenServiceName + "'");
        }

        return orderedEndpoints;
    }

    /**
     * Get the elected endpoints from the given interface name.
     * 
     * @param givenInterfaceName
     *            the given interface Name in the message exchange. May be null
     * @param exchange
     *            the exchange.
     * @param component
     *            the component.
     * @return the targeted endpoint following the specific strategy used. May
     *         be null
     * @throws RoutingException
     *             impossible to get the endpoint
     */
    public List<ServiceEndpoint> getTargetedEndpointFromGivenInterfaceName(
            final QName givenInterfaceName, final String strategy, String linkType)
            throws SearchException {
        this.log.call();

        List<ServiceEndpoint> retrievedEndpoints = null;
        // find an Endpoint with the specified InterfaceName
        try {
            retrievedEndpoints = this.getEnabledEndpoints(kindSearch.INTERFACE_SEARCH,
                    givenInterfaceName, linkType);
        } catch (RegistryException e) {
            throw new SearchException(e);
        }

        List<Object> strategyParameters = getAndAnalyseStrategy(strategy);

        List<ServiceEndpoint> orderedEndpoints = null;
        if (retrievedEndpoints.size() == 1) {
            orderedEndpoints = new ArrayList<ServiceEndpoint>(1);
            orderedEndpoints.add(retrievedEndpoints.get(0));
        } else if (retrievedEndpoints.size() > 1) {
            try {
                orderedEndpoints = this.endpointOrderer.orderEndpoints(retrievedEndpoints, null,
                        strategyParameters, this.random);
            } catch (RoutingException e) {
                throw new SearchException(e);
            }
        }

        if ((orderedEndpoints == null) || (orderedEndpoints.size() == 0)) {
            // No exception thrown yet, because we will try with the Federation
            // first
            // throw new
            // RoutingException("No endpoint found matching the target interface '"
            // + givenInterfaceName + "'");
        }

        return orderedEndpoints;
    }

    /**
     * Return only the enabled endPoints among the potentials endpoints.
     * 
     * @param type
     *            the type of research (by service or interface)
     * @param consumer
     *            the consumer
     * @param givenName
     *            the given service or interface name
     * @param exchange
     *            the exchange
     * @return the list of valide endpoints
     * @throws RegistryException
     */
    public List<ServiceEndpoint> getEnabledEndpoints(final kindSearch type, final QName givenName,
            String linkType) throws RegistryException {
        this.log.call();

        List<ServiceEndpoint> enabledEndpoints = new ArrayList<ServiceEndpoint>();
        org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint[] potentialEndpoints = null;

        if (type == kindSearch.SERVICE_SEARCH) {
            potentialEndpoints = this.endpointRegistry.getInternalEndpointsForService(givenName,
                    LinkType.fromValue(linkType));
        } else {
            potentialEndpoints = this.endpointRegistry.getInternalEndpointsForInterface(givenName,
                    LinkType.fromValue(linkType));
        }

        for (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint providerEP : potentialEndpoints) {
            enabledEndpoints.add(providerEP);
        }

        return enabledEndpoints;
    }

    /**
     * Get the strategy and analyses it. If the strategy is defined as message
     * property, it is used. Otherwise the default confugred strategy is used.
     * 
     * @param exchange
     *            the message exchange in which the strategy to use can be
     *            provided. Must be non-null
     * @throws RoutingException
     *             impossible to create the endpoint chooser strategy
     * @return the analyzed list of parameters
     */
    private static List<Object> getAndAnalyseStrategy(final String strategy)
            throws SearchException {

        final List<Object> result;
        if (strategy == null) {
            result = defaultStrategyParameters;
        } else {
            result = tokenizeAndAnalyseStrategy(strategy);

        }
        return result;
    }

    private static List<Object> tokenizeAndAnalyseStrategy(final String strategy)
            throws SearchException {
        final List<String> temp = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(strategy, EndpointOrderer.STRATEGY_SEPARATOR);
        while (st.hasMoreTokens()) {
            temp.add(st.nextToken().trim());
        }
        return analyseStrategy(temp);
    }

    /**
     * Analyse the strategy.
     * 
     * @param parameters
     *            the list of parameters to analyze
     * @return the analyzed list of parameters to analyze
     * @throws RoutingException
     *             impossible to analyze the default routing strategy.
     */
    private static List<Object> analyseStrategy(final List<String> parameters)
            throws SearchException {
        List<Object> result = null;
        try {
            if ((parameters != null)
                    && (parameters.size() == EndpointOrderer.NUMBER_STRATEGY_PARAMETERS)) {

                try {
                    EndpointOrderer.verifStrategyParameters(parameters.get(0).toLowerCase(), Float
                            .valueOf(parameters.get(1)), Float.valueOf(parameters.get(2)), Float
                            .valueOf(parameters.get(3)));
                } catch (RoutingException e) {
                    throw new SearchException(e);
                }

                result = new ArrayList<Object>();
                result.add(parameters.get(0).toLowerCase());
                result.add(Float.valueOf(parameters.get(1)));
                result.add(Float.valueOf(parameters.get(2)));
                result.add(Float.valueOf(parameters.get(3)));
            } else {
                throw new SearchException("Invalid Parameters: "
                        + "4 parameters are required to configure the routing strategy");
            }
        } catch (NumberFormatException e) {
            throw new SearchException("Impossible to convert the "
                    + "parameters to realize the routing strategy");
        }
        return result;
    }

    public TopologyService getTopologyService() {
        return this.topologyService;
    }

    public void setTopologyService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    public ConfigurationService getConfigurationService() {
        return this.configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public EndpointRegistry getEndpointRegistry() {
        return this.endpointRegistry;
    }

    public void setEndpointRegistry(EndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getAll() {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        try {
            result.addAll(this.endpointRegistry.getEndpoints());
        } catch (RegistryException e) {
            e.printStackTrace();
        }
        return result;
    }

}
