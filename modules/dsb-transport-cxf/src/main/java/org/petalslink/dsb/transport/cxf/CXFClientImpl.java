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

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.transport.Adapter;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.util.LoggingUtil;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFClientImpl implements Client {

    /**
     * Client side
     */
    private final TransportService transportService;

    private final LoggingUtil log;

    /**
     * 
     */
    public CXFClientImpl(TransportService transportService, LoggingUtil log) {
        this.transportService = transportService;
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchange exchange, long sendTimeout) throws ClientException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending message exchange...");
        }

        this.setTimeout(sendTimeout);

        org.petalslink.dsb.api.MessageExchange messageExchange = Adapter
                .createWSMessage(exchange);

        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending the following message : " + messageExchange.toString());
        }

        try {
            // send to remote transporter
            this.transportService.receive(messageExchange);
        } catch (TransportException e) {
            String error = "Got an error : " + e.getMessage();
            if (this.log.isDebugEnabled()) {
                this.log.warning(error, e);
            } else {
                this.log.warning(error);
            }
            throw new ClientException(e.getMessage());
        }
    }

    /**
     * Set the timeout on the client...
     */
    private void setTimeout(long timeout) {
        org.apache.cxf.endpoint.Client cxf = ClientProxy.getClient(this.transportService);
        if (cxf != null) {
            HTTPConduit conduit = (HTTPConduit) cxf.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(timeout);
            policy.setReceiveTimeout(timeout);
            conduit.setClient(policy);
        }
    }

}
