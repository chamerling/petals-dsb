/**
 * 
 */
package org.petalslink.dsb.kernel.gms;

import java.util.HashSet;
import java.util.Set;

import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.petalslink.gms.Peer;
import org.petalslink.gms.PeerManager;

/**
 * A peer manager implementation for the DSB. Takes the data from the topology service.
 * 
 * @author chamerling
 * 
 */
public class DSBPeerManagerImpl implements PeerManager {

    private TopologyService topologyService;

    private ConfigurationService configurationService;

    public void start() {
    }

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
