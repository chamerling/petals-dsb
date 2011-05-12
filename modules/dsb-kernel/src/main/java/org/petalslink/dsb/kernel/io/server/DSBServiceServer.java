/**
 * 
 */
package org.petalslink.dsb.kernel.io.server;

import org.petalslink.dsb.service.client.MessageListener;

/**
 * Hook to be able to receive messages from the service bus.
 * 
 * @author chamerling
 *
 */
public interface DSBServiceServer {
    
    /**
     * Start listening to incoming messages
     */
    void start();
    
    /**
     * Stop listening to incoming messages
     */
    void stop();
    
    /**
     * Be able to set a listener for the server. It is up to the implementation to use it or not...
     * 
     * @param listener
     */
    void setListener(MessageListener listener);
    
    /**
     * 
     * @return
     */
    MessageListener getListener();

}
