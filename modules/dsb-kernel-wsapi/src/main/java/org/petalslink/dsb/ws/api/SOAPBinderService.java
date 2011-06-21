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
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * DEPRECATED : This service bind or proxify existing web services to the bus.
 * 
 * @deprecated, use {@link SOAPServiceBinder}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@Deprecated
@WebService
public interface SOAPBinderService {

    /**
     * Bind existing web service to the bus means that we will create service
     * endpoints for each service defined in the WSDL description.
     * 
     * @param wsdlURI
     * @return
     */
    @WebMethod
    boolean bindWebService(@WebParam(name = "wsdlURI") String wsdlURI)
            throws DSBWebServiceException;

    /**
     * Unbind a service which has already been binded?
     * 
     * @param wsdlURI
     * @return
     * @throws ServiceBinderException
     *             if no such service has been binded or if something wrong
     *             occurs on the ESB side.
     */
    @WebMethod
    boolean unbindWebService(@WebParam(name = "wsdlURI") String wsdlURI)
            throws DSBWebServiceException;

    /**
     * Get the list of bound web services
     * 
     * @return
     */
    @WebMethod
    @WebResult(name = "wsdlURI")
    String[] getBoundWebServices();

    /**
     * Unproxify an existing web service with the Bus
     * 
     * @param wsdlURI
     * @return
     * @throws ServiceBinderException
     */
    @WebMethod
    boolean proxifyWebService(@WebParam(name = "wsdlURI") String wsdlURI)
            throws DSBWebServiceException;

    /**
     * Unproxify a proxyfied service
     * 
     * @param wsdlURI
     * @return
     * @throws ServiceBinderException
     */
    @WebMethod
    boolean unproxifyWebService(@WebParam(name = "wsdlURI") String wsdlURI)
            throws DSBWebServiceException;

    /**
     * Get the list of proxified web services
     * 
     * @return
     */
    @WebMethod
    @WebResult(name = "wsdlURI")
    String[] getProxifiedWebServices();

    @WebMethod
    @WebResult(name = "serviceAddress")
    boolean exposeService(ServiceEndpoint serviceEndpoint) throws DSBWebServiceException;
}
