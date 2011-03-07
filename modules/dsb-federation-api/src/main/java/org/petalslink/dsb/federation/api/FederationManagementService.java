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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * The federation server management API. Used to manage connection between
 * federation client and federation nodes. Contains the JAXWS annotations just
 * in case...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface FederationManagementService {

    /**
     * A client want to join the federation. TODO : We may need more information
     * than just a callback URL...
     * 
     * @param callbackURL
     *            The callback URL is used by the federation server to talk to
     *            the federation client. For example, if the URL is a Web
     *            service one, the federation server will use this URL to talk
     *            to the client with the help of a Web service client... It
     *            means that the client is not only a client but also acts as a
     *            server when the federation server needs to send back response
     *            to the client.
     * @return
     * @throws FederationException
     */
    @WebMethod(operationName = "join")
    void join(@WebParam(name = "clientid") String clientId,
            @WebParam(name = "callbackURL") String callbackURL) throws FederationException;

    /**
     * Ask for leave the federation
     * 
     * @param clientId
     * @return
     * @throws FederationException
     */
    @WebMethod(operationName = "leave")
    void leave(@WebParam(name = "clientid") String clientId) throws FederationException;

}
