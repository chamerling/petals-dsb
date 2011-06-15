/**
 * 
 */
package org.petalslink.dsb.kernel.tools.rest;

import java.util.Set;


/**
 * @author chamerling
 *
 */
public interface RESTServiceRegistry {
    
    /**
     * Load the web service information from the same fractal container
     */
    void load();

    /**
     * Get the web services which have been loaded by {@link #load()}
     * 
     * @return
     */
    Set<RESTServiceInformationBean> getRESTServices();

}
