/**
 * 
 */
package org.petalslink.dsb.kernel.api.messaging;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * A simple registry listener
 * 
 * @author chamerling
 * 
 */
public interface RegistryListener {

    /**
     * Do something after the endpoint registration
     * 
     * @param endpoint
     */
    void onRegister(ServiceEndpoint endpoint) throws DSBException;

    /**
     * Do something after the endpoint unregistration
     * 
     * @param endpoint
     */
    void onUnregister(ServiceEndpoint endpoint) throws DSBException;
    
    /**
     * Get the listener name
     * 
     * @return
     */
    String getName();

}
