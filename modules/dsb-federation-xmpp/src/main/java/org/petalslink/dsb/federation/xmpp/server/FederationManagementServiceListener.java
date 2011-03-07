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

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.xmpp.commons.Adapter;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationManagementServiceListener implements PacketListener,
        FederationManagementService {

    private final FederationManagementService managementService;

    private static Log logger = LogFactory.getLog(FederationManagementServiceListener.class);

    /**
     * @param managementService
     * @param executorService
     */
    public FederationManagementServiceListener(FederationManagementService managementService,
            ExecutorService executorService) {
        this.managementService = managementService;
    }

    /**
     * {@inheritDoc}
     */
    public void processPacket(Packet packet) {
        if (logger.isInfoEnabled()) {
            logger.info("Got a packet in federation management listener");
        }
        Message message = (Message) packet;
        String action = Adapter.getAction(message);
        String clientId = Adapter.getClientId(message);

        if ("join".equals(action)) {
            // just call the service
            try {
                this.join(clientId, Adapter.getCallbackURL(message));
            } catch (FederationException e) {
                e.printStackTrace();
            }
        } else if ("leave".equals(action)) {
            try {
                this.leave(clientId);
            } catch (FederationException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("Unknown action " + action);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void join(String clientId, String callbackURL) throws FederationException {
        this.managementService.join(clientId, callbackURL);
    }

    /**
     * {@inheritDoc}
     */
    public void leave(String clientId) throws FederationException {
        this.managementService.leave(clientId);
    }

}
