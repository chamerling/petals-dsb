package org.petalslink.dsb.kernel.gms;

import java.util.Set;

/**
 * Group Manager
 * 
 * @author chamerling
 *
 */
public interface GMSManager {
    
    /**
     * Register a new listener which will be used to dispatch things on group
     * 
     * @param listener
     */
    void register(GMSListener listener);

    /**
     * Get the listeners which are aware of the given context
     * 
     * @param context
     * @return
     */
    Set<GMSListener> getListeners(GMSContext context);
    
    /**
     * Get all the listeners
     * 
     * @return
     */
    Set<GMSListener> getListeners();

    
}
