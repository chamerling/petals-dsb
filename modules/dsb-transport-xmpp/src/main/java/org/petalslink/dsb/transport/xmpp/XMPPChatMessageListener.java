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

import java.util.concurrent.ExecutorService;

import javax.jbi.messaging.MessagingException;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.transport.api.Receiver;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPChatMessageListener implements PacketListener {

    private final Receiver receiver;

    private final LoggingUtil log;

    private final ExecutorService executorService;

    /**
     * @param log
     * 
     */
    public XMPPChatMessageListener(Receiver receiver, ExecutorService executorService,
            LoggingUtil log) {
        this.receiver = receiver;
        this.executorService = executorService;
        this.log = log;

    }

    /**
     * {@inheritDoc}
     */
    public void processPacket(Packet packet) {
        // TODO : The contact MUST be one defined in the topology, if not reject
        // the message. We can also share a secret key in a message property.
        if (this.log.isDebugEnabled()) {
            this.log.debug("Just got an XMPP packet from '" + packet.getFrom() + "' : "
                    + packet.toXML());
        }
        if (packet instanceof Message) {
            // delegate the message processing to a worker...
            this.executorService.submit(new Worker((Message) packet));

        } else {
            this.log.warning("Not a valid packet, we just process messages");
        }
    }

    /**
     * 
     * @author chamerling - eBM WebSourcing
     * 
     */
    class Worker implements Runnable {

        private final Message message;

        /**
         * 
         */
        public Worker(Message message) {
            this.message = message;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            try {
                MessageExchangeWrapper exchange = Adapter.createJBIMessage(this.message);
                if (exchange != null) {
                    XMPPChatMessageListener.this.receiver.onMessage(exchange);
                } else {
                    XMPPChatMessageListener.this.log.warning("Incoming message body is null");
                }
            } catch (MessagingException e) {
                XMPPChatMessageListener.this.log.warning("Processing packet error", e);
            }
        }

    }
}
