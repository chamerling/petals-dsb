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
package org.petalslink.dsb.kernel.registry;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
import org.ow2.petals.jbi.descriptor.original.generated.LinkType;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.jbi.messaging.registry.RegistryListener;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.w3c.dom.Document;

/**
 * Fractal component for {@link EndpointRegistryImpl}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = org.ow2.petals.jbi.messaging.registry.EndpointRegistry.class))
public class FractalEndpointRegistryImpl implements EndpointRegistry {

    /**
     * Configuration service fractal component
     */
    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    protected ConfigurationService configurationService;

    @Requires(name = "topology", signature = org.ow2.petals.communication.topology.TopologyService.class)
    protected org.ow2.petals.communication.topology.TopologyService localTopologyService;

    @Requires(name = BaseEndpointRegistry.LISTENER_FRACTAL_PREFIX, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL, signature = RegistryListener.class)
    protected Hashtable<String, Object> listeners = new Hashtable<String, Object>();

    protected EndpointRegistryImpl delegate;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() throws Exception {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");

        // FIXME = Need to work on log management to not inject log in
        // constructor
        this.delegate = new EndpointRegistryImpl(this.log);
        this.delegate.setConfigurationService(this.configurationService);
        this.delegate.setTopologyService(this.localTopologyService);
        this.delegate.setListeners(this.listeners);

        this.delegate.init();
        this.delegate.setup();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws Exception {
        this.log.debug("Stopping...");
        this.delegate.shutdown();

        // FIXME ?
        // this.delegate = null;
    }

    public ServiceEndpoint activateEndpoint(QName serviceName, String endpointName,
            QName[] interfaces, Document description, ServiceEndpoint address,
            Map<String, String> properties) throws RegistryException {
        return this.delegate.activateEndpoint(serviceName, endpointName, interfaces, description,
                address, properties);
    }

    public ServiceEndpoint activateEndpoint(QName serviceName, String endpointName,
            ServiceEndpoint address) throws RegistryException {
        return this.delegate.activateEndpoint(serviceName, endpointName, address);
    }

    public void deactivateEndpoint(String endpointName, QName serviceName) throws RegistryException {
        this.delegate.deactivateEndpoint(endpointName, serviceName);
    }

    public void deregisterConnection(QName consInterface, QName provService, String provEndpoint)
            throws RegistryException {
        this.delegate.deregisterConnection(consInterface, provService, provEndpoint);
    }

    public void deregisterConnection(QName consService, String consEndpoint, QName provService,
            String provEndpoint) throws RegistryException {
        this.delegate.deregisterConnection(consService, consEndpoint, provService, provEndpoint);
    }

    public void deregisterExternalEndpoint(String endpointName, QName serviceName)
            throws RegistryException {
        this.delegate.deregisterExternalEndpoint(endpointName, serviceName);
    }

    public List<Map<String, Object>> getAllEndpoints() {
        return this.delegate.getAllEndpoints();
    }

    public List<Map<String, Object>> getAllExternalEndpoints() {
        return this.delegate.getAllExternalEndpoints();
    }

    public List<Map<String, Object>> getAllInternalEndpoints() {
        return this.delegate.getAllInternalEndpoints();
    }

    public Document getDescription(String serviceName, String endpointName)
            throws RegistryException {
        return this.delegate.getDescription(serviceName, endpointName);
    }

    public ServiceEndpoint getEndpoint(QName service, String name) throws RegistryException {
        return this.delegate.getEndpoint(service, name);
    }

    public Document getEndpointDescriptorForEndpoint(ServiceEndpoint endpoint)
            throws RegistryException {
        return this.delegate.getEndpointDescriptorForEndpoint(endpoint);
    }

    public List<ServiceEndpoint> getEndpoints() {
        return this.delegate.getEndpoints();
    }

    public List<ServiceEndpoint> getExternalEndpoints() throws RegistryException {
        return this.delegate.getExternalEndpoints();
    }

    public ServiceEndpoint[] getExternalEndpointsForInterface(QName interfaceName)
            throws RegistryException {
        return this.delegate.getExternalEndpointsForInterface(interfaceName);
    }

    public ServiceEndpoint[] getExternalEndpointsForService(QName serviceName)
            throws RegistryException {
        return this.delegate.getExternalEndpointsForService(serviceName);
    }

    public QName[] getInterfacesForEndpoint(ServiceEndpoint endpoint) {
        return this.delegate.getInterfacesForEndpoint(endpoint);
    }

    public List<ServiceEndpoint> getInternalEndpoints() throws RegistryException {
        return this.delegate.getInternalEndpoints();
    }

    public ServiceEndpoint[] getInternalEndpointsForInterface(QName interfaceName, LinkType linktype)
            throws RegistryException {
        return this.delegate.getInternalEndpointsForInterface(interfaceName, linktype);
    }

    public ServiceEndpoint[] getInternalEndpointsForService(QName serviceName, LinkType linktype)
            throws RegistryException {
        return this.delegate.getInternalEndpointsForService(serviceName, linktype);
    }

    public List<ServiceEndpoint> query(String endpointName, QName itf, QName service,
            String containerName, String componentName, String subDomainName, String type)
            throws RegistryException {
        return this.delegate.query(endpointName, itf, service, containerName, componentName,
                subDomainName, type);
    }

    public void registerConnection(QName consInterface, QName provService, String provEndpoint)
            throws RegistryException {
        this.delegate.registerConnection(consInterface, provService, provEndpoint);
    }

    public void registerConnection(QName consService, String consEndpoint, QName provService,
            String provEndpoint) throws RegistryException {
        this.delegate.registerConnection(consService, consEndpoint, provService, provEndpoint);
    }

    public void registerExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint externalEndpoint)
            throws RegistryException {
        this.delegate.registerExternalEndpoint(externalEndpoint);
    }

    public void removeAllLocalEndpoints() throws RegistryException {
        this.delegate.removeAllLocalEndpoints();
    }

    public void synchronizeData() throws RegistryException {
        this.delegate.synchronizeData();
    }

    public List<RegistryListener> getListeners() {
        return this.delegate.getListeners();
    }

}
