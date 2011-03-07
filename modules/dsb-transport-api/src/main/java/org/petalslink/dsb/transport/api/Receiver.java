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

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

/**
 * The receiver is called when a new message is receipted on the core transport
 * layer. The receiver is implemented by the generic petals transporter. The
 * message server just have to have a reference to it to call
 * {@link #onMessage(MessageExchange)}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Receiver {

    /**
     * Callback on message reception for the petals transport layer.
     * 
     * @param message
     */
    void onMessage(MessageExchange message);

}
