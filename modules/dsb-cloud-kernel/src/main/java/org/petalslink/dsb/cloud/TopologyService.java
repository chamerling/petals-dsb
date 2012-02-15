/**
 * 
 */
package org.petalslink.dsb.cloud;

import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.edelweiss.api.CloudNode;
import org.ow2.petals.edelweiss.api.discovery.CloudTopologyService;
import org.ow2.petals.edelweiss.api.discovery.NodeLoader;
import org.ow2.petals.edelweiss.core.discovery.CloudTopologyServiceImpl;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.configuration.ConfigurationException;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.kernel.configuration.ContainerConfiguration.ContainerState;
import org.ow2.petals.kernel.configuration.DomainConfiguration;
import org.ow2.petals.kernel.configuration.DomainConfiguration.DomainMode;
import org.ow2.petals.kernel.configuration.SubDomainConfiguration;
import org.ow2.petals.util.LoggingUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A cloud aware topology service. It replaces the static and file based
 * topology service provided by Petals ESB.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = org.ow2.petals.communication.topology.TopologyService.class) })
public class TopologyService implements org.ow2.petals.communication.topology.TopologyService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private CloudTopologyService cloudTopologyService;

    /**
     * The node loader is injected because it can change on Cloud provider.
     */
    @Requires(name = "nodeloader", signature = NodeLoader.class)
    private NodeLoader nodeLoader;

    /**
     * Just match the original topology service...
     */
    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        long ttl = 30l;
        CloudConfiguration configuration = CloudConfiguration.get();

        this.cloudTopologyService = new CloudTopologyServiceImpl(nodeLoader,
                configuration.getGroupName(), ttl);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    private Set<ContainerConfiguration> map(Set<CloudNode> nodes) {
        Set<ContainerConfiguration> result = Sets.newHashSet();
        for (CloudNode node : nodes) {
            ContainerConfiguration containerConfiguration = new ContainerConfiguration();
            containerConfiguration.setName(node.getName());
            containerConfiguration.setHost(node.getHostname());
            containerConfiguration.setRegistryPort(7600);
            containerConfiguration.setState(ContainerState.STARTED);
            containerConfiguration.setTcpPort(7600);
            result.add(containerConfiguration);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * hasValidLocalContainerDynamicTopologyConfiguration()
     */
    public boolean hasValidLocalContainerDynamicTopologyConfiguration() {
        log.info("hasValidLocalContainerDynamicTopologyConfiguration");
        // NOP
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * addContainerConfiguration
     * (org.ow2.petals.kernel.configuration.ContainerConfiguration)
     */
    public void addContainerConfiguration(ContainerConfiguration addedContainer)
            throws PetalsException {
        log.info("addContainerConfiguration " + addedContainer);
        // NOP
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * addSubdomainConfiguration
     * (org.ow2.petals.kernel.configuration.SubDomainConfiguration)
     */
    public void addSubdomainConfiguration(SubDomainConfiguration addedSubdomain)
            throws PetalsException {
        log.info("addSubdomainConfiguration " + addedSubdomain);
        // NOP
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * getContainerConfiguration(java.lang.String)
     */
    public ContainerConfiguration getContainerConfiguration(final String containerName) {
        log.info("getContainerConfiguration " + containerName);

        Set<CloudNode> filter = Sets.filter(cloudTopologyService.getNodes(),
                new Predicate<CloudNode>() {
                    public boolean apply(CloudNode input) {
                        if (log.isDebugEnabled())
                            log.debug("Predicate filter for node " + input);
                        return containerName.equals(input.getName());
                    }
                });
        return Iterables.getFirst(map(filter), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * getContainersConfiguration
     * (org.ow2.petals.kernel.configuration.ContainerConfiguration
     * .ContainerState)
     */
    public Set<ContainerConfiguration> getContainersConfiguration(ContainerState state) {
        log.info("getContainersConfiguration");
        return this.map(cloudTopologyService.getNodes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * getContainersConfigurationsForLocalSubdomain()
     */
    public Set<ContainerConfiguration> getContainersConfigurationsForLocalSubdomain() {
        log.info("getContainersConfigurationsForLocalSubdomain");
        return this.map(cloudTopologyService.getNodes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.communication.topology.TopologyService#getDomainConfiguration
     * ()
     */
    public DomainConfiguration getDomainConfiguration() {
        log.info("getDomainConfiguration");

        DomainConfiguration domainConfiguration = new DomainConfiguration();
        domainConfiguration.setDescription("Cloud Domain");
        domainConfiguration.setName(CloudConfiguration.get().getGroupName());
        domainConfiguration.setMode(DomainMode.DYNAMIC);
        return domainConfiguration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * getSubDomainsConfiguration()
     */
    public Set<SubDomainConfiguration> getSubDomainsConfiguration() {
        log.info("getSubDomainsConfiguration");
        return Sets.newHashSet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.communication.topology.TopologyService#isContainerStarted
     * (java.lang.String)
     */
    public boolean isContainerStarted(final String containerName) {
        log.info("isContainerStarted " + containerName);

        Set<CloudNode> filter = Sets.filter(cloudTopologyService.getNodes(),
                new Predicate<CloudNode>() {
                    public boolean apply(CloudNode input) {
                        if (log.isDebugEnabled())
                            log.debug("Predicate filter for node " + input);
                        return containerName.equals(input.getName());
                    }
                });
        return filter.size() == 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * registerLocalContainerOnMaster()
     */
    public void registerLocalContainerOnMaster() throws PetalsException {
        log.info("registerLocalContainerOnMaster");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * removeContainerConfiguration
     * (org.ow2.petals.kernel.configuration.ContainerConfiguration)
     */
    public void removeContainerConfiguration(ContainerConfiguration removedContainer)
            throws ConfigurationException {
        log.info("removeContainerConfiguration " + removedContainer);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.communication.topology.TopologyService#
     * removeSubdomainConfiguration(java.lang.String)
     */
    public void removeSubdomainConfiguration(String removedSubdomainName)
            throws ConfigurationException {
        log.info("removeSubdomainConfiguration " + removedSubdomainName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.communication.topology.TopologyService#setContainerState
     * (java.lang.String,
     * org.ow2.petals.kernel.configuration.ContainerConfiguration
     * .ContainerState)
     */
    public void setContainerState(String containerName, ContainerState state) {
        // NOP
        log.info("Set container state : " + containerName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.communication.topology.TopologyService#updateTopology()
     */
    public void updateTopology() throws PetalsException {
        log.info("updateTopology");
        // fetch data...
        this.cloudTopologyService.getNodes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.communication.topology.TopologyService#validateDynamicTopology
     * ()
     */
    public void validateDynamicTopology() throws ConfigurationException {
        log.info("ValidateDynamicTopology");
    }

}
