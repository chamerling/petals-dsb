/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service;

import java.util.List;

import org.petalslink.dsb.soap.api.Service;

/**
 * @author chamerling
 * 
 */
public interface ServiceRegistry {

    /**
     * Get all the services
     * 
     * @return
     */
    List<Service> getServices();

}
