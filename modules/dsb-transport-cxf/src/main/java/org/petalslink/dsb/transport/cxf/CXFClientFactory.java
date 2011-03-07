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
package org.petalslink.dsb.transport.cxf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientFactory;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.LoggingUtil;

/**
 * CXF/JAX-WS client factory
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ClientFactory.class) })
public class CXFClientFactory implements ClientFactory {

    private Map<String, Client> cache;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.cache = new ConcurrentHashMap<String, Client>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Client getClient(String container) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Getting a transport client for container '" + container + "'");
        }

        Client client = null;
        if (this.cache.get(container) == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Creating a new transport client for container '" + container + "'");
            }
            // lazy loading

            ContainerConfiguration remoteConfiguration = this.configurationService
                    .getContainerConfiguration(container);
            String address = "http://" + remoteConfiguration.getHost() + ":"
                    + remoteConfiguration.getWebservicePort() + Constants.SUFFIX;

            if (this.log.isDebugEnabled()) {
                this.log.debug("Creating a CXF client to reach container " + container
                        + " located at " + address);
            }
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setAddress(address);
            factory.setServiceClass(TransportService.class);
            TransportService transportServiceClient = (TransportService) factory.create();

            client = new CXFClientImpl(transportServiceClient, this.log);
            this.cache.put(container, client);
        } else {
            client = this.cache.get(container);
        }

        return client;
    }

    /**
     * {@inheritDoc}
     */
    public void releaseClient(String container, Client client) {
        // TODO Auto-generated method stub
    }

    //

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setLog(LoggingUtil log) {
        this.log = log;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
