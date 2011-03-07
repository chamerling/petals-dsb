/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * @author chamerling
 *
 */
@WebService
public interface RESTServiceBinder {
    
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
            @WebParam(name = "endpointName") String endpointName) throws PEtALSWebServiceException;

    /**
     * Unbind a service which is already bound to the DSB
     * 
     * @param restURL
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean unbindRESTService(@WebParam(name = "restBaseURL") String restURL)
            throws PEtALSWebServiceException;
    
    /**
     * Get a list of REST services which are bound to the DSB
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getRESTServices() throws PEtALSWebServiceException;

}
