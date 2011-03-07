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
package org.petalslink.dsb.federation.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * TODO = All can be in invoke... The federation service asynchronous API
 * definition. Used by the federation server to expose service and by the
 * federation client implementation to invoke the service. Contains JAXWS
 * annotations just in case...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface FederationService {

    /**
     * 
     * @param query
     * @param clientId
     * @throws FederationException
     */
    @WebMethod(operationName = "lookup")
    @WebResult(name = "endpoint")
    void lookup(@WebParam(name = "query") EndpointQuery query,
            @WebParam(name = "clientId") String clientId, String id) throws FederationException;

    /**
     * 
     * @param endpoints
     * @param clientId
     * @throws FederationException
     */
    @WebMethod(operationName = "reply")
    void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException;

    /**
     * Invoke a service with the given message. The target endpoint is defined
     * in the message exchange
     * 
     * @param endpoint
     * @param message
     */
    @WebMethod(operationName = "invoke")
    void invoke(MessageExchange message, @WebParam(name = "clientId") String clientId, String id)
            throws FederationException;

}
