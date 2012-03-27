/**
 * 
 */
package org.petalslink.dsb.kernel.gms;

import java.util.HashSet;
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
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.gms.Peer;
import org.petalslink.gms.PeerManager;

/**
 * A peer manager implementation for the DSB. Takes the data from the topology service.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = PeerManager.class) })
public class DSBPeerManagerImpl implements PeerManager {
    
    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "topology", signature = TopologyService.class)
    private TopologyService topologyService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        log = new LoggingUtil(logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
    }

    public Peer getMe() {
        return new Peer(configurationService.getContainerConfiguration().getName());
    }

    public Set<Peer> getPeers() {
        Set<Peer> result = new HashSet<Peer>();
        Set<ContainerConfiguration> set = topologyService.getContainersConfiguration(null);
        for (ContainerConfiguration containerConfiguration : set) {
            result.add(new Peer(containerConfiguration.getName()));
        }
        return result;
    }

}
