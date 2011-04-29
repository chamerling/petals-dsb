package org.petalslink.gms;

import java.util.Set;

/**
 * Group Manager
 * 
 * @author chamerling
 *
 */
public interface GMSListenerManager {
    
    /**
     * Register a new listener which will be used to dispatch things on group
     * 
     * @param listener
     */
    void register(GMSListener listener);
    
    /**
     * 
     * @param listener
     */
    void unregister(GMSListener listener);

    /**
     * Get all the listeners
     * 
     * @return
     */
    Set<GMSListener> getListeners();

    
}
