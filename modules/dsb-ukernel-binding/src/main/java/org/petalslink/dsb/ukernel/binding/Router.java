/**
 * 
 */
package org.petalslink.dsb.ukernel.binding;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.petalslink.dsb.api.MessageExchange;

/**
 * 
 * @author chamerling
 * 
 */
@WebService
public interface Router {

    @WebMethod
    Route getRoute(String query);

    @WebMethod
    void addRoute(Route route);

    /**
     * Find the right route for the message. Does not invoke the final service.
     * Up to the engine to do that...
     * 
     * @param message
     * @return
     */
    Route route(MessageExchange message);

}
