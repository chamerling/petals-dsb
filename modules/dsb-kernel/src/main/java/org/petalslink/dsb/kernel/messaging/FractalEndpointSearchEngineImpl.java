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

import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.messaging.EndpointSearchEngine;
import org.petalslink.dsb.kernel.api.messaging.SearchException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = EndpointSearchEngine.class))
public class FractalEndpointSearchEngineImpl implements EndpointSearchEngine {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "topology", signature = TopologyService.class)
    private TopologyService topologyService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "endpoint", signature = EndpointRegistry.class)
    private EndpointRegistry endpointRegistry;

    private EndpointSearchEngineImpl delegate;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() throws Exception {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.delegate = new EndpointSearchEngineImpl(this.log);
        this.delegate.setConfigurationService(this.configurationService);
        this.delegate.setEndpointRegistry(this.endpointRegistry);
        this.delegate.setTopologyService(this.topologyService);
        this.delegate.init();
        this.delegate.setup();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws Exception {
        this.log.debug("Stopping...");
        this.delegate.shutdown();
    }

    public ServiceEndpoint getTargetedEndpointFromGivenEndpoint(ServiceEndpoint givenEndpoint,
            String linktype) throws SearchException {
        return this.delegate.getTargetedEndpointFromGivenEndpoint(givenEndpoint, linktype);
    }

    public List<ServiceEndpoint> getTargetedEndpointFromGivenInterfaceName(
            QName givenInterfaceName, String strategy, String linkType) throws SearchException {
        return this.delegate.getTargetedEndpointFromGivenInterfaceName(givenInterfaceName,
                strategy, linkType);
    }

    public List<ServiceEndpoint> getTargetedEndpointFromGivenServiceName(QName givenServiceName,
            String strategy, String linkType) throws SearchException {
        return this.delegate.getTargetedEndpointFromGivenServiceName(givenServiceName, strategy,
                linkType);
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getAll() {
        return this.delegate.getAll();
    }

}
