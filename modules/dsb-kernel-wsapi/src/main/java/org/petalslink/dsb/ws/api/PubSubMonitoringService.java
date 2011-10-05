/**
 * 
 */
package org.petalslink.dsb.ws.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * A simple pubsub monitoring service. Just hides the subscription complexity.
 * Up to the implementation to use whatever needed to subscribe to local
 * monitoring...
 * 
 * @author chamerling
 * 
 */
@WebService
public interface PubSubMonitoringService {

    /**
     * Subscribe the the monitoring data. All the data will be sent to
     * subscriberEndpoint
     * 
     * @param subscriberEndpoint
     * @return a subscription ID
     * @throws DSBWebServiceException
     */
    @WebMethod
    String subscribe(String subscriberEndpoint) throws DSBWebServiceException;

    /**
     * 
     * @param subscriptionID
     * @return
     * @throws DSBWebServiceException
     */
    @WebMethod
    boolean unsubscribe(String subscriptionID) throws DSBWebServiceException;

}
