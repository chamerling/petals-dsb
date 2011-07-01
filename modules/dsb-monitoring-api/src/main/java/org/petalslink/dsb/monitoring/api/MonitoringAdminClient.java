/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * A client to the monitoring admin layer.
 * 
 * @author chamerling
 * 
 */
@WebService
public interface MonitoringAdminClient {

    /**
     * Create a monitoring endpoint on the monitoring layer from the given one.
     * 
     * @param serviceEndpoint
     * @throws DSBException
     */
    @WebMethod
    void createMonitoringEndpoint(
            @WebParam(name = "serviceEndpoint") ServiceEndpoint serviceEndpoint)
            throws DSBException;

}
