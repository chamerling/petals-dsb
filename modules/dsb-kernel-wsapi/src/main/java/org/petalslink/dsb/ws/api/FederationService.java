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
package org.petalslink.dsb.ws.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.kernel.ws.api.to.Endpoint;

/**
 * Service to invoke some operations on the federation
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface FederationService {

    /**
     * Connect to the federation
     * 
     * @throws PEtALSWebServiceException
     *             if something wrong or if the federation service is not
     *             available on this node
     */
    @WebMethod
    void connect() throws PEtALSWebServiceException;

    /**
     * Disconnect from the federation
     * 
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    void disconnect() throws PEtALSWebServiceException;

    /**
     * Federation feature is available for this node?
     * 
     * @return
     */
    @WebMethod
    boolean isAvailable();

    /**
     * 
     * @return
     */
    @WebMethod
    boolean isConnected();

    /**
     * Get the endpoints of the federation the current node belongs to.
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    List<Endpoint> getEndpoints() throws PEtALSWebServiceException;

}
