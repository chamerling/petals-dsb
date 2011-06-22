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

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFClientImpl implements Client {

    /**
     * Client side which is a proxy to the remote service
     */
    private final TransportService transportService;

    private static Logger log = Logger.getLogger(CXFClientImpl.class.getName());

    /**
     * 
     */
    public CXFClientImpl(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchange exchange, long sendTimeout) throws ClientException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Sending message exchange...");
        }

        this.setTimeout(sendTimeout);

        if (log.isLoggable(Level.FINE)) {
            log.fine("Sending the following message : " + exchange);
        }

        try {
            // send to remote transporter
            this.transportService.receive(exchange);
        } catch (TransportException e) {
            String error = "Got an error : " + e.getMessage();
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.WARNING, error, e);
            } else {
                log.warning(error);
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
