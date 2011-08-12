/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 * 
 */
public interface ServiceExposer {

    /**
     * Expose all the services
     * 
     * @throws DSBException
     */
    void expose() throws DSBException;

}
