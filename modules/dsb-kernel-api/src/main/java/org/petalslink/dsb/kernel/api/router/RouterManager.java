/**
 * 
 */
package org.petalslink.dsb.kernel.api.router;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Manages {@link RouterModule}s.
 * 
 * @author chamerling
 *
 */
@WebService
public interface RouterManager {
    
    /**
     * Get a module based on its name
     * 
     * @param name
     * @return
     */
    @WebMethod
    RouterModule getModule(String name);
    
    /**
     * Add a module to the modules collection
     * 
     * @param module
     */
    @WebMethod
    void addModule(RouterModule module);
    
    /**
     * Removes a module from the modules collection
     * 
     * @param name
     * @return
     */
    @WebMethod
    RouterModule deleteModule(String name);

}
