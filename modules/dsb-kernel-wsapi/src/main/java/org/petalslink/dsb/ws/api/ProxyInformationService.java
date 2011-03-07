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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * Get some information about the proxy services. The proxy services are
 * services which are bound the the DSB and which are exposed by components.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface ProxyInformationService {

    /**
     * Get the REST proxy URL for the given REST url
     * 
     * @param url
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    String getRESTProxy(@WebParam(name = "restURL") String restURL)
            throws PEtALSWebServiceException;

    /**
     * Get the SOAP proxy URL for the given SOAP wsdl.
     * 
     * @param url
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    String getSOAPProxy(@WebParam(name = "wsdlURL") String wsdlURL)
            throws PEtALSWebServiceException;

}
