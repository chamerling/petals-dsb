/**
 * 
 */
package org.petalslink.gms;

/**
 * @author chamerling
 *
 */
public interface GMSClientFactory {
    
    /**
     * Get a client for a remote peer
     * 
     * @param peer
     * @return
     */
    GMSClient getClient(Peer peer);
    
    /**
     * Release a client
     * 
     * @param peer
     */
    void releaseClient(Peer peer);

}
