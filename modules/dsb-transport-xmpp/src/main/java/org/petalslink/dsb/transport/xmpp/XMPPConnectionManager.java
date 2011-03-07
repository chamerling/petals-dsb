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

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPConnectionManager {

    public static int AVAILABLE_STATUS = 1;

    public static int UNAVAILABLE_STATUS = 2;

    private static Map<Integer, Presence> PRESENCE = new HashMap<Integer, Presence>();

    static {
        PRESENCE.put(AVAILABLE_STATUS, new Presence(Type.available));
        PRESENCE.put(UNAVAILABLE_STATUS, new Presence(Type.unavailable));
    }

    private static XMPPConnectionManager SINGLETON;

    /**
     * 
     */
    public synchronized static XMPPConnectionManager getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new XMPPConnectionManager();
        }
        return SINGLETON;
    }

    private XMPPConnection connection;

    private XMPPConnectionManager() {
    }

    /**
     * TODO : give server ports and more as parameters
     */
    public boolean connect() {
        boolean result = true;
        if (this.connection != null) {
            return result;
        }

        ConnectionConfiguration configuration = new ConnectionConfiguration("talk.google.com",
                5222, "gmail.com");
        this.connection = new XMPPConnection(configuration);
        try {
            this.connection.connect();
        } catch (XMPPException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 
     */
    public void login(String user, String password) {
        if (this.connection != null) {
            try {
                this.connection.login(user, password);
            } catch (XMPPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    public void disconnect() {
        if (this.connection != null) {
            this.connection.disconnect();
        }
    }

    public void send(Message message, String jid) {
        // TODO = cache the chat in order to not create it each time...
        Chat chat = this.connection.getChatManager().createChat(jid, null);
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public XMPPConnection getConnection() {
        return this.connection;
    }

    public void setStatus(int status, String realStatus) {
        if ((this.connection != null) && this.connection.isConnected()
                && (PRESENCE.get(status) != null)) {
            Presence p = PRESENCE.get(status);
            p.setStatus(realStatus);
            this.getConnection().sendPacket(PRESENCE.get(status));
        }
    }

    /**
     * @param xmppListener
     * @param messageTypeFilter
     */
    public void addPacketListener(XMPPChatMessageListener xmppListener, MessageTypeFilter messageTypeFilter) {
        if (this.connection != null) {
            this.getConnection().addPacketListener(xmppListener, messageTypeFilter);
        }
    }

}
