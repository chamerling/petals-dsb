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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPConnectionManager {

    private static Log logger = LogFactory.getLog(XMPPConnectionManager.class);

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
            this.connection.addConnectionListener(new ConnectionListener() {

                public void reconnectionSuccessful() {
                    if (logger.isInfoEnabled()) {
                        logger.info("Reconnection successfull");
                    }
                }

                public void reconnectionFailed(Exception arg0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Reconnection failed");
                        logger.info("Let's reconnect ourself (X max attemps)");
                    }
                }

                public void reconnectingIn(int arg0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Reconnecting In");
                    }
                }

                public void connectionClosedOnError(Exception arg0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Connection closed on error");
                    }
                }

                public void connectionClosed() {
                    if (logger.isInfoEnabled()) {
                        logger.info("Connection closed");
                    }
                }
            });
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
        if ((this.connection != null) && this.connection.isConnected()) {
            try {
                this.connection.login(user, password);
            } catch (XMPPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            logger.warn("Not connected to server, can not login!");
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
    public void addPacketListener(PacketListener xmppListener, PacketFilter packetFilter) {
        if ((this.connection != null) && this.connection.isConnected()) {
            this.getConnection().addPacketListener(xmppListener, packetFilter);
        } else {
            logger.warn("Can not add the listener when connection is not OK");
        }
    }

}
