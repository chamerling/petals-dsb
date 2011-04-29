/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.petalslink.gms.GMSServiceImpl;
import org.petalslink.gms.Peer;
import org.petalslink.gms.PeerManager;

/**
 * @author chamerling
 * 
 */
public class Main {

    private static final Logger logger = Logger.getLogger("org.petalslink.gms");

    /**
     * @param args
     */
    public static void main(String[] args) {
        logger.setLevel(Level.FINE);
        final List<Peer> peers = new ArrayList<Peer>();
        for (int i = 0; i < 5; i++) {
            peers.add(i, new Peer("999" + i));
        }

        final List<PeerManager> managers = new ArrayList<PeerManager>();

        for (int i = 0; i < 5; i++) {
            final int j = i;
            PeerManager peerManager = new PeerManager() {
                public Set<Peer> getPeers() {
                    return new HashSet<Peer>(peers);
                }

                public Peer getMe() {
                    return peers.get(j);
                }
            };
            managers.add(i, peerManager);
        }

        // 1
        PeerManager peerManager = managers.get(0);
        org.petalslink.gms.GMSService gms0 = createGMSService(peerManager);

        // 2
        peerManager = managers.get(1);
        org.petalslink.gms.GMSService gms1 = createGMSService(peerManager);

        // 3
        peerManager = managers.get(2);
        org.petalslink.gms.GMSService gms2 = createGMSService(peerManager);

        gms0.start();
        gms1.start();
        gms2.start();

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Uncaugth : " + e.getMessage());
            }
        });

        try {
            TimeUnit.MINUTES.sleep(5);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gms0.stop();
        gms1.stop();
        gms2.stop();

        System.out.println("STOP");

    }

    /**
     * @param peerManager
     * @return
     */
    protected static org.petalslink.gms.GMSService createGMSService(PeerManager peerManager) {
        GMSServiceImpl gms = new GMSServiceImpl();
        gms.setClientFactory(new CXFFactory());
        gms.setPeerManager(peerManager);
        gms.setServer(new CXFServer(peerManager.getMe(), gms));
        return gms;
    }

}
