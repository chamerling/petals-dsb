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
package org.petalslink.dsb.federation.api.client;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;

/**
 * The synchronous client API. This is the federation framework facade used at
 * the client side to communicate with the federation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface FederationClient {

    /**
     * Get a list of endpoints which match the query.
     * 
     * @param query
     * @return
     */
    @WebMethod(operationName = "lookup")
    @WebResult(name = "endpoint")
    Set<ServiceEndpoint> lookup(@WebParam(name = "query") EndpointQuery query)
            throws FederationException;

    /**
     * Invoke a service with the given message. The target endpoint is defined
     * in the message exchange
     * 
     * @param endpoint
     * @param message
     */
    @WebMethod(operationName = "invoke")
    void invoke(MessageExchange message) throws FederationException;

    /**
     * 
     * @return
     * @throws FederationException
     */
    @WebMethod(operationName = "join")
    void join() throws FederationException;

    /**
     * Ask for leave the federation
     * 
     * @return
     * @throws FederationException
     */
    @WebMethod(operationName = "leave")
    void leave() throws FederationException;

}
