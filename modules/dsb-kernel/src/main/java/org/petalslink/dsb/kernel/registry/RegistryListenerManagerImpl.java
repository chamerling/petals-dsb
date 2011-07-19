/**
 * 
 */
package org.petalslink.dsb.kernel.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.NotImplementedException;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;

/**
 * A default implementation of the {@link RegistryListenerManager}
 * 
 * @author chamerling
 * 
 */
public class RegistryListenerManagerImpl implements RegistryListenerManager {

    private Map<String, RegistryListener> listeners;

    /**
     * 
     */
    public RegistryListenerManagerImpl() {
        this.listeners = new ConcurrentHashMap<String, RegistryListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#setState
     * (java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        // TODO
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getState
     * (java.lang.String)
     */
    public boolean getState(String name) {
        // TODO
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getList()
     */
    public List<RegistryListener> getList() {
        List<RegistryListener> result = new ArrayList<RegistryListener>(this.listeners.size());
        for (RegistryListener registryListener : this.listeners.values()) {
            result.add(registryListener);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#add(org
     * .petalslink.dsb.kernel.api.messaging.RegistryListener)
     */
    public void add(RegistryListener listener) throws DSBException {
        if (listener == null) {
            throw new DSBException("listener is null");
        }

        String name = listener.getName();
        if (name == null) {
            throw new DSBException("listener name null");
        }
        this.listeners.put(listener.getName(), listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#get(java
     * .lang.String)
     */
    public RegistryListener get(String name) throws DSBException {
        return this.listeners.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#remove
     * (java.lang.String)
     */
    public RegistryListener remove(String name) throws DSBException {
        return this.listeners.remove(name);
    }

}
