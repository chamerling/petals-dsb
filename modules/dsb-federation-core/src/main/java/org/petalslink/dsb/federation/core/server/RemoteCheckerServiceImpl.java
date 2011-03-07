/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.federation.core.server;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.core.api.FederationClient;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.PeerChecker;
import org.petalslink.dsb.federation.core.api.Service;

/**
 * A service which checks if a remote peer is here or not. On communication
 * success or failure it rise events on peer manager.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RemoteCheckerServiceImpl implements Service {

    private final FederationServer server;

    private final ScheduledExecutorService scheduledExecutorService;

    private final PeerChecker peerChecker;

    private static Log logger = LogFactory.getLog(RemoteCheckerServiceImpl.class);

    /**
     * 
     */
    public RemoteCheckerServiceImpl(FederationServer server, PeerChecker peerChecker) {
        this.server = server;
        this.peerChecker = peerChecker;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return RemoteCheckerServiceImpl.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public TYPE getType() {
        return TYPE.INTERNAL;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        Runnable command = new Runnable() {
            public void run() {
                if (logger.isInfoEnabled()) {
                    logger.info("Running task in " + RemoteCheckerServiceImpl.this.getName());
                }
                Set<org.petalslink.dsb.federation.core.api.FederationClient> set = RemoteCheckerServiceImpl.this.server
                        .getClients();
                for (org.petalslink.dsb.federation.core.api.FederationClient federationClient : set) {
                    RemoteCheckerServiceImpl.this.checkPeer(federationClient);
                }
            }
        };

        this.scheduledExecutorService.scheduleAtFixedRate(command, 10L, 10L, TimeUnit.SECONDS);
    }

    /**
     * @param federationClient
     */
    protected void checkPeer(FederationClient federationClient) {
        boolean isAvailable = this.peerChecker.check(federationClient.getCallbackURL());
        if (logger.isInfoEnabled()) {
            logger.info("Peer " + federationClient.getName() + " is available " + isAvailable);
        }
        if (!isAvailable) {
            this.server.getPeerManager().onUnreachable(federationClient.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        this.scheduledExecutorService.shutdownNow();
    }

}
