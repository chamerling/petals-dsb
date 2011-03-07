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
package org.petalslink.dsb.federation.xmpp.client;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.xmpp.commons.Adapter;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationClientListener implements PacketListener, FederationService {

    private static Log logger = LogFactory.getLog(FederationClientListener.class);

    private final FederationService federationService;

    /**
     * @param federationService
     */
    public FederationClientListener(FederationService federationService) {
        this.federationService = federationService;
    }

    /**
     * {@inheritDoc}
     */
    public void processPacket(Packet packet) {
        if (logger.isInfoEnabled()) {
            logger.info("Got a packet in federation client listener");
        }
        Message message = (Message) packet;
        String action = Adapter.getAction(message);
        String clientId = Adapter.getClientId(message);
        String id = Adapter.getFedId(message);

        if (logger.isInfoEnabled()) {
            logger.info("Action : " + action);
            logger.info("Client : " + clientId);
            logger.info("id : " + id);
        }

        // TODO : dispatch depends on the service...
        if ("invoke".equals(action)) {
            // just call the service
            try {
                this.invoke(Adapter.getMessageExchange(message), clientId, id);
            } catch (FederationException e) {
                e.printStackTrace();
            }
        } else if ("lookup".equals(action)) {
            // call the service and send back a response
            try {
                this.lookup(Adapter.getQuery(message), clientId, id);

            } catch (FederationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if ("lookupReply".equals(action)) {
            try {
                this.lookupReply(Adapter.getServiceEndpoints(message), clientId, id);
            } catch (FederationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            logger.warn("Unknown action " + action);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message, String clientId, String id)
            throws FederationException {
        this.federationService.invoke(message, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        this.federationService.lookup(query, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        this.federationService.lookupReply(endpoints, clientId, id);
    }

}
