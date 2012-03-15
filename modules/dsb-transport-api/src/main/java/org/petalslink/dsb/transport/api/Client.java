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
package org.petalslink.dsb.transport.api;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;


/**
 * The client interface used to send messages to the remote container.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Client {

    /**
     * Send the message exchange. It is up to the client implementation to deal
     * with the timeout and to throw an exception is a timeout occured! This
     * timeout is the timeout defined at the petals container level and not at
     * the message level. It means that the service consumer can define a
     * timeout which differs from the container one. This message timeout is
     * managed by the generic transport layer defined in
     * {@link eu.soa4all.dsb.petals.kernel.transport.TransporterImpl} .
     * 
     * @param exchange
     * @param sendTimeout
     */
    void send(MessageExchangeWrapper exchange, long sendTimeout) throws ClientException;

}
