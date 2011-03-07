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
package org.petalslink.dsb.transport;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.NormalizedMessage;

import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;

/**
 * This class is used to have access to core data of the Petals
 * {@link MessageExchangeImpl} in order to skip the control when setting values.
 * This is only and MUST only be used by the marshaller which transforms a
 * message from the core transport layer to a petals message.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class MessageExchange extends MessageExchangeImpl {

    /**
     * @param consumerEndpoint
     */
    public MessageExchange(ServiceEndpoint consumerEndpoint) {
        super(consumerEndpoint);
    }

    /**
     * @param valueOf
     */
    public void setCoreStatus(ExchangeStatus status) {
        this.status = status;
    }

    /**
     * @param createNormalizedMessage
     */
    public void setCoreFault(Fault fault) {
        this.fault = fault;
    }

    /**
     * 
     * @param message
     * @param name
     */
    public void setCoreMessage(NormalizedMessage message, String name) {
        this.messages.put(name.toLowerCase(), message);
    }

    /**
     * @param exception
     */
    public void setCoreError(Exception exception) {
        this.error = exception;
    }

}
