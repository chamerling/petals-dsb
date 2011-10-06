/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.List;

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
     * 
     * @return
     */
    @WebMethod
    List<RouterModule> getSenders();

    /**
     * Get all the receiver names
     * 
     * @return
     */
    @WebMethod
    List<RouterModule> getReceivers();

    /**
     * 
     * @param name
     * @param onoff
     */
    @WebMethod
    void setSenderState(String name, boolean onoff);

    /**
     * 
     * @param name
     * @param onoff
     */
    @WebMethod
    void setReceiverState(String name, boolean onoff);
}
