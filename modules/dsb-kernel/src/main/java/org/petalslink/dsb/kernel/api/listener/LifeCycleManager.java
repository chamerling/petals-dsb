/**
 * 
 */
package org.petalslink.dsb.kernel.api.listener;

/**
 * @author chamerling
 * 
 */
public interface LifeCycleManager {

    /**
     * Call all the listener in the right order
     * 
     */
    void preStart();
    
    /**
     * Call all the listener in the right order
     * 
     */
    void onStart();
    
    /**
     * Call all the listener in the right order
     * 
     */
    void preStop();

    /**
     * Call all the listener in the right order
     * 
     */
    void onStop();
    
    /**
     * Call all the listener in the right order
     */
    void preShutdown();

    /**
     * Call all the listener in the right order
     */
    void onShutdown();

}
