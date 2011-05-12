/**
 * 
 */
package org.petalslink.dsb.kernel.io;

import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;

/**
 * A simple IO implementation. Able to send messages and to be notified on new
 * ones...
 * 
 * @author chamerling
 * 
 */
public class IOImpl implements IO {

    private Client client;

    private MessageListener listener;

    public IOImpl(Client client, MessageListener listener) {
        this.client = client;
        this.listener = listener;
    }

    public void fireAndForget(Message message) throws ClientException {
        this.client.fireAndForget(message);
    }

    public Message sendReceive(Message message) throws ClientException {
        return this.client.sendReceive(message);
    }

    public Message onMessage(Message message) {
        return this.listener.onMessage(message);
    }

    public void sendAsync(Message message, MessageListener listener) throws ClientException {
        this.client.sendAsync(message, listener);
    }

}
