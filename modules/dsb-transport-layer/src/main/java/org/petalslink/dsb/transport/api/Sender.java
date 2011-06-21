/**
 * 
 */
package org.petalslink.dsb.transport.api;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.TransportException;

/**
 * @author chamerling
 * 
 */
public interface Sender {

    /**
     * Fire and forget
     * 
     * @param exchange
     * @throws TransportException
     */
    void send(MessageExchange exchange, final Context context) throws TransportException;
    
    /**
     * Asynchronous send where response will be passed to the listener
     * 
     * @param exhancge
     * @param context
     * @param listener
     * @throws TransportException
     */
    void sendAsync(MessageExchange exhancge, final Context context, Receiver listener) throws TransportException;

    /**
     * Synchronous send
     * 
     * @param exchange
     * @param context
     * @return
     * @throws TransportException
     */
    MessageExchange sendSync(final MessageExchange exchange, final Context context)
            throws TransportException;
}
