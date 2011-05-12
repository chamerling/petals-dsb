/**
 * 
 */
package org.petalslink.dsb.service.client;

import org.petalslink.dsb.service.client.Message;

/**
 * A simple message listener
 * 
 * @author chamerling
 * 
 */
public interface MessageListener {

    /**
     * 
     * @param message
     * @return a message if any available or null on inonly invocation
     */
    Message onMessage(Message message);

}
