/**
 * 
 */
package org.petalslink.gms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.gms.GMSMessage.Type;

/**
 * The gossiper is in charge of maintaining a list of reachable and unreachable
 * peers of the network. Based on some background tasks, it will randomly send
 * messages to peers to check if there are alive or not.
 * 
 * @author chamerling
 * 
 */
public class Gossiper implements GMSListener {

    private static final Logger LOG = Logger.getLogger(Gossiper.class.getName());

    private class GossipTask implements Runnable {

        public void run() {
            GMSMessage message = createPingMessage();
            // first send to live member, if we have an exception and can not
            // send the message, put the peer in some unreachable state...
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Sending to live members...");
            }
            sendToLiveMember(message);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Sending to unreachable member...");
            }
            sendToUnreachableMember(message);
        }

    }

    private Comparator<Peer> peerComparator = new Comparator<Peer>() {
        public int compare(Peer peer1, Peer peer2) {
            return peer1.getName().compareTo(peer2.getName());
        }
    };

    /**
     * An updated list of live peers
     */
    private Set<Peer> livePeers = new ConcurrentSkipListSet<Peer>(peerComparator);

    /**
     * An updated list of unreachable peers. At startup, this list will be
     * filled with all the topology information from local configuration.
     */
    private Set<Peer> unreachablePeers = new ConcurrentSkipListSet<Peer>(peerComparator);

    private ScheduledExecutorService executorService;

    ScheduledFuture<?> scheduledGossipTask;

    /**
     * The local peer
     */
    private Peer me;

    private GMSService gmsService;

    public Gossiper(GMSService gmsService) {
        this.gmsService = gmsService;
    }

    public void sendToUnreachableMember(GMSMessage message) {
        if (unreachablePeers.size() > 0) {
            Random random = new Random();
            int size = unreachablePeers.size();
            /* Generate a random number from 0 -> size */
            List<Peer> lpeers = new ArrayList<Peer>(unreachablePeers);
            int index = (size == 1) ? 0 : random.nextInt(size);
            Peer to = lpeers.get(index);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(me.getName() + " Sending to " + to.getName());
            }
            try {
                this.gmsService.getGMSMessageService().send(message, to);
                isActive(to, true);
            } catch (GMSException e) {
                // still unreachable... Do nothing
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine(e.getMessage());
                }
            }
        }
    }

    public void sendToLiveMember(GMSMessage message) {
        if (livePeers.size() == 0) {
            return;
        }
        Random random = new Random();
        int size = livePeers.size();
        List<Peer> lpeers = new ArrayList<Peer>(livePeers);
        int index = (size == 1) ? 0 : random.nextInt(size);
        Peer to = lpeers.get(index);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(me.getName() + " Sending to " + to.getName());
        }
        try {
            this.gmsService.getGMSMessageService().send(message, to);
        } catch (GMSException e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(e.getMessage());
            }
            isActive(to, false);
        }
    }

    /**
     * Init and start all
     */
    public void start() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Starting...");
        }
        this.me = this.gmsService.getPeerManager().getMe();
        // get all the peers (without current one) from the configuration
        Set<Peer> peers = this.gmsService.getPeerManager().getPeers();

        // for now all are unreachable...
        for (Peer peer : peers) {
            if (!peer.getName().equals(this.me.getName())) {
                this.unreachablePeers.add(peer);
            }
        }

        // launch the background task
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledGossipTask = this.executorService.scheduleAtFixedRate(new GossipTask(), 1000,
                1000, TimeUnit.MILLISECONDS);

    }

    public void stop() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Stopping...");
        }
        this.scheduledGossipTask.cancel(false);
    }

    /**
     * Send a message to a random peer chosen from the given peers
     * 
     * @throws GMSException
     */
    void sendMessageToRandom(GMSMessage message, Set<Peer> peers) throws GMSException {
        Random random = new Random();
        int size = peers.size();
        /* Generate a random number from 0 -> size */
        List<Peer> lpeers = new ArrayList<Peer>(peers);
        int index = (size == 1) ? 0 : random.nextInt(size);
        Peer to = lpeers.get(index);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(me.getName() + " Sending to " + to.getName());
        }
        this.gmsService.getGMSMessageService().send(message, to);
    }

    Set<Peer> getLivePeers() {
        return new HashSet<Peer>(livePeers);
    }

    GMSMessage createPingMessage() {
        GMSMessage message = new GMSMessage();
        message.source = me;
        message.type = Type.PING;
        return message;
    }

    public void onMessage(GMSMessage message) {
        // got a message, let's update things...
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Gossiper @ " + me.getName() + " got a message from "
                    + message.source.getName());
        }

        if (message != null && message.getSource() != me) {
            // we potentially have a new node...
            isActive(message.getSource(), true);

            // TODO : we can have some listeners doing things on new nodes
            // arrival...
        }
    }

    /**
     * Change a peer state according to its locall status and to the active flag
     * 
     * @param peer
     * @param active
     */
    void isActive(Peer peer, boolean active) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Peer " + peer.getName() + " is alive : " + active);
        }
        if (active) {
            livePeers.add(peer);
            unreachablePeers.remove(peer);
        } else {
            livePeers.remove(peer);
            unreachablePeers.add(peer);
        }
    }

}
