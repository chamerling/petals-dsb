/**
 * 
 */
package org.petalslink.dsb.wsn.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * A Web service API for the producer service
 * 
 * @author chamerling
 * 
 */
@WebService
public interface NotificationProducerServiceStr {

    @WebMethod
    String subscribe(String request);

    @WebMethod
    String getCurrentMessage(String request);

}
