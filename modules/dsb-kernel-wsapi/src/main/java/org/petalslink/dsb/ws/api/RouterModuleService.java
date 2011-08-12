/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Manage router modules
 * 
 * @author chamerling
 * 
 */
@WebService
public interface RouterModuleService {

    /**
     * Get all the senders names
     * @return
     */
    @WebMethod
    Set<String> getSenders();

    /**
     * Get all the receiver names
     * @return
     */
    @WebMethod
    Set<String> getReceivers();

    /**
     * Set the state of a module
     * 
     * @param name
     * @param onoff
     * @return
     */
    @WebMethod
    boolean setState(String name, boolean onoff);

    @WebMethod
    boolean getState(String name);

}
