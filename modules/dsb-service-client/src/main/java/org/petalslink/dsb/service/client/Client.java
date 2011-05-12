/**
 * 
 */
package org.petalslink.dsb.service.client;

import org.petalslink.dsb.api.DSBException;

/**
 * A DSB service client. This client can be used directly inside the DSB kernel
 * to invoke services hosted locally or remotely.
 * 
 * @author chamerling
 * 
 */
public interface Client {

    /**
     * Invoke a service and do not wait for a response
     */
    void fireAndForget(Message message) throws ClientException;
    
    /**
     * Send a message and wait for its response.
     * 
     * @param message
     * @return
     * @throws DSBException
     */
    Message sendReceive(Message message) throws ClientException;
    
    /**
     * Send a message with a callback, the listener wuill be called on response
     * 
     * @param message
     * @param listener
     * @throws ClientException
     */
    void sendAsync(Message message, MessageListener listener) throws ClientException;

}
