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
package org.petalslink.dsb.transport.cxf;

import javax.jbi.messaging.MessagingException;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.transport.Adapter;
import org.petalslink.dsb.transport.api.Receiver;

/**
 * The Transport Service is in charge of translating received message and
 * passign it to the {@link Receiver}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TransportServiceImpl implements TransportService {

    private final Receiver receiver;

    private final LoggingUtil logger;

    /**
     * 
     */
    public TransportServiceImpl(final Receiver receiver, final LoggingUtil logger) {
        this.receiver = receiver;
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    public void receive(org.petalslink.dsb.api.MessageExchange messageExchange)
            throws TransportException {
        if (messageExchange == null) {
            final String message = "Null message received!!!";
            this.logger.warning(message);
            throw new TransportException(message);
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Receiving a message " + messageExchange.getId());
            this.logger.debug(messageExchange.toString());
        }

        // transform the message to JBI one and send to receiver
        MessageExchangeWrapper jbiMessageExchange = null;
        try {
            jbiMessageExchange = Adapter.createJBIMessageWrapper(messageExchange);
        } catch (MessagingException e) {
            throw new TransportException(e.getMessage());
        }
        this.receiver.onMessage(jbiMessageExchange);
    }
}
