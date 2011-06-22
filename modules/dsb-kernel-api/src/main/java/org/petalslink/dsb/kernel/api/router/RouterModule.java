/**
 * 
 */
package org.petalslink.dsb.kernel.api.router;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * A router module. Everything can be done in such module since modules are
 * linked together in a linked list to provide a very extensible routing
 * mechanism.
 * 
 * @author chamerling
 * 
 */
@WebService
public interface RouterModule {

    /**
     * 
     * @param message
     */
    @WebMethod
    List<ServiceEndpoint> route(MessageExchange message) throws DSBException;

    /**
     * A unique name in the modules collection
     * 
     * @return
     */
    @WebMethod
    String getName();

}
