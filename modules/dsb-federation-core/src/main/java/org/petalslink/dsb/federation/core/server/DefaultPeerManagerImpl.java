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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.PeerManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DefaultPeerManagerImpl implements PeerManager {

    private final FederationServer federationServer;

    private static Log logger = LogFactory.getLog(DefaultPeerManagerImpl.class);

    /**
     * 
     */
    public DefaultPeerManagerImpl(FederationServer federationServer) {
        this.federationServer = federationServer;
    }

    /**
     * {@inheritDoc}
     */
    public void onReachable(String clientId) {
        if (logger.isDebugEnabled()) {
            logger.debug("On reachable");
        }
        // update the state of the client...
        org.petalslink.dsb.federation.core.api.FederationClient client = this.federationServer
                .getClient(clientId);
        if (client != null) {
            client.setReachable();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onUnreachable(String clientId) {
        if (logger.isDebugEnabled()) {
            logger.debug("On unreachable");
        }

        // update the state of the client
        // update the state of the client...
        org.petalslink.dsb.federation.core.api.FederationClient client = this.federationServer
                .getClient(clientId);
        if (client != null) {
            client.setUnreachable();
        }
    }
}
