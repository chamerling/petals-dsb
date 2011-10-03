/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Manages the registry listeners
 * 
 * @author chamerling
 * 
 */
@WebService
public interface RegistryListenerService {

    @WebMethod
    Set<String> getListeners();
    
    @WebMethod
    boolean getState(String name);

    @WebMethod
    void setState(String name, boolean state);

}
