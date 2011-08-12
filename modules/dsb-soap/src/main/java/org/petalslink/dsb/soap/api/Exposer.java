/**
 * 
 */
package org.petalslink.dsb.soap.api;

import java.util.List;

/**
 * A really simple service exposer
 * 
 * @author chamerling
 * 
 */
public interface Exposer {

    /**
     * Expose the given service. Up to the implementation to expose it where it
     * wants...
     * 
     * @param service
     * @throws ServiceException
     */
    org.petalslink.dsb.commons.service.api.Service expose(Service service) throws ServiceException;
    
    /**
     * Get all the exposed services
     * 
     * @return
     */
    List<Service> getServices();

}
