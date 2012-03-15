/**
 * 
 */
package org.petalslink.dsb.kernel.ws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.dragon.connection.api.service.EeType;
import org.ow2.dragon.connection.api.service.FedPattern;
import org.ow2.dragon.connection.api.service.HashMapType;
import org.ow2.dragon.connection.api.service.Node;
import org.ow2.dragon.connection.api.to.Endpoint;
import org.ow2.dragon.connection.api.to.EnvironmentFederation;
import org.ow2.dragon.connection.api.to.ExecutionEnvironment;
import org.ow2.dragon.connection.api.to.HashMapEntryType;
import org.ow2.dragon.connection.api.to.Processor;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.kernel.ws.api.MasterConnectionService;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = MasterConnectionService.class) })
public class MasterConnectionServiceImpl implements MasterConnectionService {

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Monolog(name = "logger")
    private Logger logger;

    @Requires(name = "endpoint", signature = EndpointRegistry.class)
    private EndpointRegistry endpointRegistry;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "topology", signature = TopologyService.class, contingency = Contingency.OPTIONAL)
    private TopologyService topologyService;

    @Requires(name = "adminService", signature = AdminService.class)
    private AdminService adminService;

    private LoggingUtil log;

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
    public Component getComponent() {
        return this.component;
    }

    public List<ExecutionEnvironment> getFederationMembers(final String federationName) {
        final List<ExecutionEnvironment> result = new ArrayList<ExecutionEnvironment>();

        // FIXME : Federation members are the containers from the same
        // subdomain?
        final Set<ContainerConfiguration> containers = this.getContainersConfiguration();
        for (final ContainerConfiguration containerConfiguration : containers) {
            if (this.isInFederation(containerConfiguration, federationName)) {
                result.add(this.getExecutionEnvironment(containerConfiguration));
            }
        }
        return result;
    }

    public List<Endpoint> getHostedEndpointsOnExecEnv(final String execEnvName) {
        return this.getEndpointsForContainer(execEnvName);
    }

    public List<Endpoint> getHostedEndpointsOnProcessor(final String processorName) {
        // TODO : Get all the execution environments from the processor name...
        final List<Endpoint> result = new ArrayList<Endpoint>();

        final Set<ContainerConfiguration> containers = this
                .getContainersForProcessor(processorName);
        for (final ContainerConfiguration container : containers) {
            result.addAll(this.getEndpointsForContainer(container.getName()));
        }

        return result;
    }

    public List<ExecutionEnvironment> getManagedExecutionEnvironments() {
        final List<ExecutionEnvironment> result = new ArrayList<ExecutionEnvironment>();

        // get all the containers configuration
        final Set<ContainerConfiguration> set = this.getContainersConfiguration();

        // fill the exec env from the containers configuration
        for (final ContainerConfiguration containerConfiguration : set) {
            result.add(this.getExecutionEnvironment(containerConfiguration));
        }
        return result;
    }

    public org.ow2.dragon.connection.api.to.ExecutionEnvironmentManager getProperties() {
        final org.ow2.dragon.connection.api.to.ExecutionEnvironmentManager manager = new org.ow2.dragon.connection.api.to.ExecutionEnvironmentManager();
        manager.setName("Manager@PEtALS-ESB:"
                + this.configurationService.getContainerConfiguration().getName());
        return manager;
    }

    /**
     * Fill an Execution environment from the container configuration
     * 
     * @param containerConfiguration
     * @return
     */
    private ExecutionEnvironment getExecutionEnvironment(
            final ContainerConfiguration containerConfiguration) {
        final EnvironmentFederation fed = new EnvironmentFederation();
        fed.setName(this.configurationService.getDomainConfiguration().getName());

        final ExecutionEnvironment env = new ExecutionEnvironment();
        env.setIpv4Address(containerConfiguration.getHost());
        env.setEnvType(EeType.ESB);
        env.setType("PEtALS");
        env.setVersion(this.adminService.getSystemInfo());
        env.setName(containerConfiguration.getName());
        if (this.isStandalone()) {
            env.setRoleInFederation("standalone");
        } else {
            // TODO : The role depends on the topology, a node will be a
            // member of a domain, a node parent, a routing node or
            // whatever... Will come in PEtALS v3.
            env.setRoleInFederation("peer");
        }

        // FIXME : Standalone and distributed are not the same...
        fed.setPattern(FedPattern.DISTRIBUTED);
        env.setParentFederation(fed);

        final List<Endpoint> endpoints = this.getEndpointsForContainer(containerConfiguration
                .getName());
        Node.Endpoints eps = new Node.Endpoints();
        eps.getEndpoint().addAll(endpoints);
        env.setEndpoints(eps);
        
        env.setHostProcessor(this.getProcessor(containerConfiguration));

        Node.Properties props = new Node.Properties();
        props.getProperty().addAll(this.getProperties(containerConfiguration));
        env.setProperties(props);
        
        return env;
    }

    /**
     * @return
     */
    private Set<ContainerConfiguration> getContainersConfiguration() {
        Set<ContainerConfiguration> set = null;

        if (this.isStandalone()) {
            final ContainerConfiguration cc = this.configurationService.getContainerConfiguration();
            set = new HashSet<ContainerConfiguration>();
            set.add(cc);
        } else {
            set = this.topologyService.getContainersConfiguration(null);
        }

        return set;
    }

    /**
     * Get all the endpoints for the given container
     * 
     * @param containerName
     * @return
     */
    private List<Endpoint> getEndpointsForContainer(final String containerName) {
        final List<Endpoint> result = new ArrayList<Endpoint>();
        List<ServiceEndpoint> endpoints;
        try {
            endpoints = this.endpointRegistry.getEndpoints();
        } catch (RegistryException e) {
            return result;
        }

        for (final ServiceEndpoint ep : endpoints) {
            if (containerName.equals(ep.getLocation().getContainerName())) {
                final Endpoint endpoint = new Endpoint();
                final QName endpointName = QName.valueOf(ep.getEndpointName());
                endpoint.setName(endpointName);
                try {
                    Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader()
                            .read(ep.getDescription());
                    Map<URI, org.w3c.dom.Document> imports = desc.deleteImportedDocumentsInWsdl();
                    String rootWSDL = WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter()
                            .writeWSDL4ComplexWsdl(desc);
                    endpoint.setWsdlDescription(rootWSDL);

                    HashMapType map = new HashMapType();
                    if (imports != null) {
                        for (URI uri : imports.keySet()) {
                            try {
                                HashMapEntryType entry = new HashMapEntryType();
                                entry.setKey(uri.toString());
                                entry.setValue(XMLHelper.createStringFromDOMDocument(imports.get(uri)));
                                map.getEntry().add(entry);
                            } catch (Exception e) {
                                // skipped
                                this.log.warning(e.getMessage());
                            }
                        }
                    }

                    endpoint.setWsdlDescriptionImports(map);
                    result.add(endpoint);
                } catch (WSDL4ComplexWsdlException e) {
                    this.log.warning(e.getMessage());
                } catch (URISyntaxException e) {
                    this.log.warning(e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * FIXME : how to get processor information for a PEtALS node ?
     * 
     * @param configuration
     * @return
     */
    private Processor getProcessor(final ContainerConfiguration configuration) {
        final Processor processor = new Processor();
        processor.setIpv4Address(configuration.getHost());
        processor.setName("ProcessorName-" + configuration.getName());
        processor.setType("Unknown");
        return processor;
    }

    /**
     * Get the container properties
     * 
     * @param configuration
     * @return
     */
    private List<String> getProperties(final ContainerConfiguration configuration) {
        final List<String> result = new ArrayList<String>();
        result.add("Description=" + configuration.getDescription());
        result.add("State=" + configuration.getState());
        return result;
    }

    /**
     * Get all the containers which are hosted on given the processor
     * 
     * @param processorName
     * @return
     */
    private Set<ContainerConfiguration> getContainersForProcessor(final String processorName) {
        final Set<ContainerConfiguration> result = new HashSet<ContainerConfiguration>();
        final Set<ContainerConfiguration> containers = this.getContainersConfiguration();
        for (final ContainerConfiguration containerConfiguration : containers) {
            // FIXME : For now the processor is the container...
            if (processorName.equals(containerConfiguration.getName())) {
                result.add(containerConfiguration);
            }
        }
        return result;
    }

    /**
     * Test if the container belongs to a federation. FIXME : Define a
     * federation in PEtALS.
     * 
     * @param configuration
     * @param federationName
     * @return
     */
    private boolean isInFederation(final ContainerConfiguration configuration,
            final String federationName) {
        return federationName.equals(configuration.getSubdomainName());
    }

    /**
     * 
     * @return
     */
    private boolean isStandalone() {
        return this.topologyService == null;
    }

}
