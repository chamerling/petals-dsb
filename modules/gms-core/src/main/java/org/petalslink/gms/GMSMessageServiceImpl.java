/**
 * 
 */
package org.petalslink.gms;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The message service implementation is just a gateway between the core
 * service, the client and the receiver.
 * 
 * @author chamerling
 * 
 */
public class GMSMessageServiceImpl implements GMSMessageService {
    
    private static final Logger LOG = Logger.getLogger(GMSMessageServiceImpl.class.getCanonicalName());

    private GMSService gmsService;

    public GMSMessageServiceImpl(GMSService gmsService) {
        this.gmsService = gmsService;
    }

    public void send(GMSMessage message, Peer to) throws GMSException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Sending message");
        }
        // send the message to remote pair
        GMSClient client = this.gmsService.getClientFactory().getClient(to);
        // fire and forget, exception are processed by the message service
        // client
        client.send(message);
    }

    public void receive(GMSMessage message) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Receiving message");
        }
        // The message is received by the GMSServer and its listener, so just
        // forward...
        this.gmsService.onMessage(message);
    }
}
