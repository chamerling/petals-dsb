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
package org.petalslink.dsb.transport.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;


/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class XMPPClientImpl implements Client {

    private final String jid;

    /**
     * 
     */
    public XMPPClientImpl(String jid) {
        this.jid = jid;
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchangeWrapper exchange, long sendTimeout) throws ClientException {
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        Message message = Adapter.createJabberMessage(exchange);
        xmppConnectionManager.send(message, this.jid);
        // ...
    }

}
