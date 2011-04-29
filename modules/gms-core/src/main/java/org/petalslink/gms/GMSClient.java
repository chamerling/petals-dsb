/**
 * 
 */
package org.petalslink.gms;

/**
 * A client to send message to remote peers
 * 
 * @author chamerling
 *
 */
public interface GMSClient {
    
    /**
     * Send a message to a remote peer. Up to the implementation to know where to send.
     * 
     * @param message
     * @return
     */
    boolean send(GMSMessage message) throws GMSException;

}
