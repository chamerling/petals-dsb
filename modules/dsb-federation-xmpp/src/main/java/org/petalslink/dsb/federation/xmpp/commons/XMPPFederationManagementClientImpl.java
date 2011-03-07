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

import org.jivesoftware.smack.packet.Message;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationManagementService;

/**
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPFederationManagementClientImpl implements FederationManagementService {

    private final String jid;

    /**
     * 
     * @param jid
     *            the federation server jabber id
     */
    public XMPPFederationManagementClientImpl(String jid) {
        this.jid = jid;
    }

    /**
     * {@inheritDoc}
     */
    public void join(String clientId, String callbackURL) throws FederationException {
        Message message = new Message();
        Adapter.setClientId(message, clientId);
        Adapter.setCallBackURL(message, callbackURL);
        Adapter.setAction(message, "join");
        // Adapter.setClientSide(message);
        XMPPConnectionManager.getInstance().send(message, this.jid);
    }

    /**
     * {@inheritDoc}
     */
    public void leave(String clientId) throws FederationException {
        Message message = new Message();
        Adapter.setClientId(message, clientId);
        Adapter.setAction(message, "leave");
        // Adapter.setClientSide(message);
        XMPPConnectionManager.getInstance().send(message, this.jid);
    }

}
