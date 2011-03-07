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
package org.petalslink.dsb.federation.core.client;

import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.api.client.FederationCallback;

/**
 * The service wrapper. This is the gateway between the client and the server.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public class FederationClientServiceWrapperImpl implements FederationService {

    private static Log logger = LogFactory.getLog(FederationClientServiceWrapperImpl.class);

    /**
     * The client side service implementation ie the service which is exposed by
     * the client in order to receive messages. This service is the client entry
     * point from the server point of view
     */
    private org.petalslink.dsb.federation.api.client.FederationService clientService;

    /**
     * The client callback for server responses. When a server send a lookup
     * reply message to the current service, the service must be able to notify
     * the federation client side by invoking the current callback.
     */
    private FederationCallback callback;

    /**
     * The client which is used to talk to the server
     */
    private FederationService federationServiceClient;

    private final FederationClientImpl client;

    /**
     * 
     */
    public FederationClientServiceWrapperImpl(FederationClientImpl client) {
        this.client = client;
    }

    /**
     * @param clientService
     *            the clientService to set
     */
    public void setClientService(
            org.petalslink.dsb.federation.api.client.FederationService clientService) {
        this.clientService = clientService;
    }

    /**
     * @param callback
     *            the callback to set
     */
    public void setCallback(FederationCallback callback) {
        this.callback = callback;
    }

    /**
     * @param federationServiceClient
     *            the federationServiceClient to set
     */
    public void setFederationServiceClient(FederationService federationServiceClient) {
        this.federationServiceClient = federationServiceClient;
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message, String clientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Got an incoming invoke query from client " + clientId + " and id " + id);
        }
        // local call
        this.clientService.invoke(message);
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Got an incoming lookup query from client " + clientId + " and id " + id);
        }
        // local call
        Set<ServiceEndpoint> result = this.clientService.lookup(query);
        // send back the result to the client which is i fact the federation
        // server...
        // TODO : Another thread can do that
        this.federationServiceClient.lookupReply(result, this.client.getName(), id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        // got a reply from the server; unlock listener
        if (logger.isInfoEnabled()) {
            logger.info("In client " + this.client.getName()
                    + " Got an incoming lookup reply from client " + clientId
                    + ", let's unlock all...");
        }
        this.callback.onLookupResponse(endpoints, id);
    }

}
