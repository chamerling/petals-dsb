/**
 * 
 */
package org.petalslink.gms;

import java.util.Set;

/**
 * Hide DSB topology implementation with this new interface definition.
 * 
 * @author chamerling
 *
 */
public interface PeerManager {
    
    /**
     * Who am I?
     * @return
     */
    Peer getMe();
    
    /**
     * Get all the peers defined by configuration, it includes the local peer as well.
     * 
     * @return
     */
    Set<Peer> getPeers();

}
