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
import org.petalslink.dsb.ws.api.SOAPServiceBinder;

/**
 * Get informations about platform services. Platform services are services
 * which are bound to the DSB using the Binder Services (
 * {@link SOAPServiceBinder} for example.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface PlatformServiceInformationService {

    /**
     * Get the DSB service endpoints information for the given WSDL URL (Just a
     * parsing, the endpoint is optional).
     * 
     * @param wsdlURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    List<ServiceEndpoint> getWebServiceEndpoint(String wsdlURL) throws PEtALSWebServiceException;

    /**
     * Get the DSB Service endpoint information for the given REST URL
     * 
     * @param restURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    ServiceEndpoint getRESTServiceEndpoint(String restURL) throws PEtALSWebServiceException;

    /**
     * Test if the given WSDL service is bound to the DSB (should be on another
     * node)
     * 
     * @param wsdlURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean isWebServiceBound(String wsdlURL) throws PEtALSWebServiceException;

    /**
     * Test if the given REST service is bound to the DSB
     * 
     * @param restURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean isRESTServiceBound(String restURL) throws PEtALSWebServiceException;

    /**
     * Get the web service exposed by the DSB for the given WSDL (not the inner
     * Endpoint but the real service)
     * 
     * @param wsdlURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    String getPlatformWebService(String wsdlURL) throws PEtALSWebServiceException;

    /**
     * Get the REST service exposed by the DSB for the given REST URL (not the
     * inner Endpoint but the real service)
     * 
     * @param restURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    String getPlatformRESTService(String restURL) throws PEtALSWebServiceException;

}
