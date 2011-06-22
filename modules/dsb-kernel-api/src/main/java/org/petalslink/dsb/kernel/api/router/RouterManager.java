/**
 * 
 */
package org.petalslink.dsb.kernel.api.router;

/**
 * Manages {@link RouterModule}s.
 * 
 * @author chamerling
 *
 */
public interface RouterManager {
    
    /**
     * Get a module based on its name
     * 
     * @param name
     * @return
     */
    RouterModule getModule(String name);
    
    /**
     * Add a module to the modules collection
     * 
     * @param module
     */
    void addModule(RouterModule module);
    
    /**
     * Removes a module from the modules collection
     * 
     * @param name
     * @return
     */
    RouterModule deleteModule(String name);

}
