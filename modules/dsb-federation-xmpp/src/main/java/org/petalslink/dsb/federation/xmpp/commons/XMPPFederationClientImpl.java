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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.Message;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;

/**
 * TODO Connect if needed!!!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPFederationClientImpl implements org.petalslink.dsb.federation.api.FederationService {

    private static Log logger = LogFactory.getLog(XMPPFederationClientImpl.class);

    private final String jid;

    /**
     * 
     * @param jid
     *            The Jabber ID to send message to
     */
    public XMPPFederationClientImpl(String jid) {
        this.jid = jid;
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange exchange, String clientId, String id)
            throws FederationException {
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        Message message = Adapter.createMessage(exchange, clientId, id);
        Adapter.setAction(message, "invoke");
        // TODO = In another thread...
        xmppConnectionManager.send(message, this.jid);
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        Message message = Adapter.createMessage(query, clientId, id);
        Adapter.setAction(message, "lookup");
        xmppConnectionManager.send(message, this.jid);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        Message message = Adapter.createMessage(endpoints, clientId, id);
        Adapter.setAction(message, "lookupReply");
        xmppConnectionManager.send(message, this.jid);
    }

}
