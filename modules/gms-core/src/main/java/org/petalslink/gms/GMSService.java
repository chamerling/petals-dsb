/**
 * 
 */
package org.petalslink.gms;

/**
 * The {@link GMSService} is in charge of exposing local GMS operations to
 * peers.
 * 
 * @author chamerling - PetalsLink
 * 
 */
public interface GMSService extends GMSListener {
    
    void start();
    
    void stop();

    /**
     * 
     * @return
     */
    PeerManager getPeerManager();

    /**
     * 
     * @return
     */
    GMSMessageService getGMSMessageService();

    /**
     * 
     * @return
     */
    GMSListenerManager getListenerManager();

    /**
     * 
     * @return
     */
    GMSClientFactory getClientFactory();

}
