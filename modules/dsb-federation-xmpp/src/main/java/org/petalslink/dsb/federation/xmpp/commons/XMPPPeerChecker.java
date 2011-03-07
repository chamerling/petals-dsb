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
package org.petalslink.dsb.federation.xmpp.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Roster;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.PeerChecker;

/**
 * XMPP implementation of {@link PeerChecker}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPPeerChecker implements PeerChecker {

    public FederationServer server;

    private static Log logger = LogFactory.getLog(XMPPPeerChecker.class);

    /**
     * 
     */
    public XMPPPeerChecker(FederationServer server) {
        this.server = server;
    }

    /**
     * {@inheritDoc}
     */
    public boolean check(String peerId) {
        if (logger.isInfoEnabled()) {
            logger.info("Checking presence of peer " + peerId);
        }
        XMPPConnectionManager manager = XMPPConnectionManager.getInstance();
        Roster r = manager.getConnection().getRoster();
        return r.getPresence(peerId).isAvailable();
    }

}
