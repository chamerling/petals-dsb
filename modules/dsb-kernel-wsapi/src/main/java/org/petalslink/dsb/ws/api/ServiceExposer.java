/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * This service is used to expose DSB services as external services.
 * 
 * @author chamerling
 * 
 */
@WebService
public interface ServiceExposer {

    /**
     * Expose an internal service as external service. The protocol of the
     * exposed service is implementation dependant.
     * 
     * @param serviceEndpoint
     *            the internal service to expose
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean expose(ServiceEndpoint serviceEndpoint) throws PEtALSWebServiceException;

    /**
     * Delete a service which has been exposed
     * 
     * @param serviceEndpoint
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean delete(ServiceEndpoint serviceEndpoint) throws PEtALSWebServiceException;

    /**
     * Get the list of service which have been exposed by the current service.
     * Others services may have been exposed by other management API.
     * 
     * @return a Set of URLs containing the exposed services.
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getWebServices() throws PEtALSWebServiceException;
}
