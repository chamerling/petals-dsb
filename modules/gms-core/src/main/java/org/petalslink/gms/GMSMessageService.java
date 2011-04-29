/**
 * 
 */
package org.petalslink.gms;

/**
 * A message service for GMS. Used to send and receive messages between Group
 * members
 * 
 * @author chamerling
 * 
 */
public interface GMSMessageService {

    /**
     * Send a message to a destination. This is one way, it is up to the remote
     * peer to reply by invoking the receive operation on the local peer if
     * needed or not...
     * 
     * @param message
     * @param to
     */
    void send(GMSMessage message, Peer to) throws GMSException;

    /**
     * Receive a message from a remote peer. It is up to the service
     * implementation to forward the message to interested listeners for
     * example.
     * 
     * @param message
     */
    void receive(GMSMessage message);

}
