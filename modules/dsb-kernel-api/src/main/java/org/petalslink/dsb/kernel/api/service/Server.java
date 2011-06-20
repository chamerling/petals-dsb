/**
 * 
 */
package org.petalslink.dsb.kernel.api.service;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 *
 */
public interface Server {
    
    void start() throws DSBException;
    
    void stop() throws DSBException;

}
