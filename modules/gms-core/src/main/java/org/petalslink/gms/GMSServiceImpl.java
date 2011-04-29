package org.petalslink.gms;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core GMS service
 * 
 * @author chamerling - PetalsLink
 * 
 */
public class GMSServiceImpl implements GMSService {
    
    private static final Logger LOG = Logger.getLogger(GMSServiceImpl.class.getName());

    private Gossiper gossiper;

    private GMSMessageService gmsMessageService;

    private PeerManager peerManager;

    private GMSListenerManager listenerManager;

    private GMSClientFactory clientFactory;

    private GMSServer server;

    public GMSServiceImpl() {
        this.listenerManager = new GMSListenerManagerImpl();
        this.gmsMessageService = new GMSMessageServiceImpl(this);
        this.gossiper = new Gossiper(this);
        this.getListenerManager().register(this.gossiper);
    }

    public void start() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Starting the GMS Service");
        }
        if (server != null) {
            this.server.startServer();
        }

        if (this.gossiper != null)
            this.gossiper.start();
    }

    public void stop() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Stopping the service");
        }
        if (this.gossiper != null)
            this.gossiper.stop();

        if (server != null) {
            this.server.stopServer();
        }
    }

    public void onMessage(GMSMessage message) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Got a message, will dispatch if listeners are present");
        }
        Set<GMSListener> listeners = listenerManager.getListeners();
        // For now it is sequential...
        for (GMSListener listener : listeners) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Got a message type " + message.type + " from " + message.source.getName());
            }
            listener.onMessage(message);
        }
    }

    public PeerManager getPeerManager() {
        return this.peerManager;
    }

    public GMSMessageService getGMSMessageService() {
        return this.gmsMessageService;
    }

    public GMSListenerManager getListenerManager() {
        return this.listenerManager;
    }

    public GMSClientFactory getClientFactory() {
        return this.clientFactory;
    }

    public GMSListenerManager getGmsManager() {
        return listenerManager;
    }

    public GMSMessageService getGmsMessageService() {
        return gmsMessageService;
    }

    public GMSServer getServer() {
        return server;
    }

    public void setServer(GMSServer server) {
        this.server = server;
    }

    public void setPeerManager(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    public void setClientFactory(GMSClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

}
