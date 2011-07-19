/**
 * 
 */
package org.petalslink.dsb.kernel.api.messaging;

import java.util.List;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 * 
 */
public interface RegistryListenerManager {

    /**
     * Get the complete list of listeners
     * 
     * @return
     */
    List<RegistryListener> getList();

    /**
     * Add a new listener instance to the list of registered listeners
     * 
     * @param listener
     * @throws DSBException
     *             if listener is null or something bad occurrs...
     */
    void add(RegistryListener listener) throws DSBException;

    /**
     * 
     * @param name
     * @return
     * @throws DSBException
     */
    RegistryListener get(String name) throws DSBException;

    /**
     * 
     * @param name
     * @return
     * @throws DSBException
     */
    RegistryListener remove(String name) throws DSBException;

    /**
     * Activate/Unactivate a listener base on its name
     * 
     * @param name
     * @param onoff
     */
    void setState(String name, boolean onoff);

    /**
     * Get a listener state based on its name
     * 
     * @param name
     * @return
     */
    boolean getState(String name);

}
