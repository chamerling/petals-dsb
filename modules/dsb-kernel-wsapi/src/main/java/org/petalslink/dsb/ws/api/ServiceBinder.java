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

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * The Web service binder service is in charge of calling inner services to bind
 * given Web services to the bus.
 * 
 * @deprecated : use {@link SOAPServiceBinder} or {@link RESTServiceBinder}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface ServiceBinder {

    /**
     * Bind a Web service to the DSB
     * 
     * @param wsdlURL
     *            the WSDL of the Web service to bind to the DSB
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean bindWebService(@WebParam(name = "wsdlURL") String wsdlURL)
            throws DSBWebServiceException;

    /**
     * Unbind an already bound service
     * 
     * @param wsdlURL
     *            The WSDL of the Web service to unbind
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean unbindWebService(@WebParam(name = "wsdlURL") String wsdlURL)
            throws DSBWebServiceException;

    /**
     * Get a list of bound Web services
     * 
     * @return a list of Web service WSDL which are bound to the DSB
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getWebServices() throws DSBWebServiceException;

    /**
     * Bind a REST service to the bus
     * 
     * @param restURL
     *            The REST service base URL
     * @param endpointName
     *            The endpoint name wich will be used by the DSB endpoint to
     *            identify the REST service
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean bindRESTService(@WebParam(name = "restBaseURL") String restURL,
            @WebParam(name = "endpointName") String endpointName) throws DSBWebServiceException;

    /**
     * Unbind a service which is already bound to the DSB
     * 
     * @param restURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean unbindRESTService(@WebParam(name = "restBaseURL") String restURL)
            throws DSBWebServiceException;

    /**
     * Get a list of REST services which are bound to the DSB
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getRESTServices() throws DSBWebServiceException;

}
