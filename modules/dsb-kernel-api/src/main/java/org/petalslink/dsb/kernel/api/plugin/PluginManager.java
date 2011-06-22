/**
 * 
 */
package org.petalslink.dsb.kernel.api.plugin;

import java.util.List;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 * 
 */
public interface PluginManager {
    
    /**
     * Add a component. This is only possible on some lifecycle.
     * 
     * @param <T>
     * @param type
     * @param plugin
     * @throws EngineException
     */
    <T> void addComponent(Class<T> type, T component) throws DSBException;

    /**
     * Get a component, can return null if not found
     * 
     * @param <T>
     * @param type
     * @return
     */
    <T> T getComponent(Class<T> type);

    /**
     * Get all the component for the given type ie if type is an interface and
     * many components are implementing this interface, the method must return all
     * the interface implementation available in the components.
     * 
     * @param <T>
     * @param type
     * @return
     */
    <T> List<T> getComponents(Class<T> type);
}
