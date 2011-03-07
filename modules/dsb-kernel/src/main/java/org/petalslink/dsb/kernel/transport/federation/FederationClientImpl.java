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
package org.petalslink.dsb.kernel.transport.federation;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.client.FederationClientWithCallback;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;
import org.petalslink.dsb.transport.cxf.Adapter;


/**
 * Federation client at ransport level ie for remote service invocations
 * (services on the federation not on the domain)
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationClientImpl implements Client {

    private final FederationClientWithCallback client;

    private final LoggingUtil log;

    /**
     * 
     */
    public FederationClientImpl(FederationClientWithCallback client,
            LoggingUtil log) {
        this.client = client;
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchange exchange, long sendTimeout) throws ClientException {
        this.setTimeout(sendTimeout);
        this.log.info("Sending the message to federation");
        org.petalslink.dsb.api.MessageExchange messageExchange = Adapter.createWSMessage(exchange);
        try {
            this.client.invoke(messageExchange);
        } catch (FederationException e) {
            this.log.warning("Got an error on federation invoke");
            throw new ClientException(e.getMessage());
        }
    }

    /**
     * @param sendTimeout
     */
    private void setTimeout(long sendTimeout) {

    }

}
