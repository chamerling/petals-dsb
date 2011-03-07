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
package org.petalslink.dsb.federation.xmpp.server;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.petalslink.dsb.federation.core.api.PeerManager;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.xmpp.commons.XMPPConnectionManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPPeerService implements Service, RosterListener {

    private final PeerManager peerManager;

    private static Log logger = LogFactory.getLog(XMPPPeerService.class);

    /**
     * 
     */
    public XMPPPeerService(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "XMPPPeerService";
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
        // start peer detection
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        xmppConnectionManager.getConnection().getRoster().addRosterListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        // stop peer detections
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void entriesAdded(Collection<String> arg0) {
        if (logger.isInfoEnabled()) {
            logger.info("Entries added " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entriesDeleted(Collection<String> arg0) {
        if (logger.isInfoEnabled()) {
            logger.info("Entries deleted " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entriesUpdated(Collection<String> arg0) {
        if (logger.isInfoEnabled()) {
            logger.info("Entries updated " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void presenceChanged(Presence arg0) {
        if (logger.isInfoEnabled()) {
            logger.info("Presence changed " + arg0 + " from " + arg0.getFrom());
        }
        // TODO : depending on the presence, call the peermanager
        // String clientId = null;
        // this.peerManager.onUnreachable(clientId);
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return 1;
    }

}
