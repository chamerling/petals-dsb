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
package org.petalslink.dsb.transport.soap;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.api.TransportService;
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

    private static Logger log = Logger.getLogger(CXFServerImpl.class.getName());

    /**
     * 
     */
    public TransportServiceImpl(final Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * {@inheritDoc}
     */
    public void receive(org.petalslink.dsb.api.MessageExchange messageExchange)
            throws TransportException {
        if (messageExchange == null) {
            final String message = "Null message received!!!";
            log.warning(message);
            throw new TransportException(message);
        }

        if (log.isLoggable(Level.FINE)) {
            log.fine("Receiving a message " + messageExchange.getId());
            log.fine(messageExchange.toString());
        }

        if (this.receiver == null) {
            log.warning("There is no receiver on this transporter...");
        } else {
            this.receiver.onMessage(messageExchange);
        }
    }
}
