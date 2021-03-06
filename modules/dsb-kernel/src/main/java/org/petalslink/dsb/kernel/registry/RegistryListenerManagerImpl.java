/**
 * 
 */
package org.petalslink.dsb.kernel.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;

/**
 * A default implementation of the {@link RegistryListenerManager}
 * 
 * @author chamerling
 * 
 */
public class RegistryListenerManagerImpl implements RegistryListenerManager {

    private Map<String, ManagedRegistryListener> listeners;

    /**
     * 
     */
    public RegistryListenerManagerImpl() {
        this.listeners = new ConcurrentHashMap<String, ManagedRegistryListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#setState
     * (java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        ManagedRegistryListener listener = this.listeners.get(name);
        if (listener == null) {
            return;
        }
        listener.state = onoff;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getState
     * (java.lang.String)
     */
    public boolean getState(String name) {
        ManagedRegistryListener listener = this.listeners.get(name);
        if (listener == null) {
            return false;
        }
        return listener.state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getList()
     */
    public List<RegistryListener> getList() {
        List<RegistryListener> result = new ArrayList<RegistryListener>(this.listeners.size());
        for (ManagedRegistryListener registryListener : this.listeners.values()) {
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
        this.listeners.put(listener.getName(), new ManagedRegistryListener(listener));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#get(java
     * .lang.String)
     */
    public RegistryListener get(String name) throws DSBException {
        ManagedRegistryListener listener = this.listeners.get(name);
        if (listener == null) {
            throw new DSBException(String.format("No such listener %s", name));
        }
        return listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#remove
     * (java.lang.String)
     */
    public RegistryListener remove(String name) throws DSBException {
        ManagedRegistryListener listener = this.listeners.remove(name);
        if (listener == null) {
            throw new DSBException("No such listener %s", name);
        }
        return listener.listener;
    }

    class ManagedRegistryListener implements RegistryListener {

        RegistryListener listener;

        boolean state;

        ManagedRegistryListener(RegistryListener listener) {
            this.listener = listener;
            state = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onRegister
         * (org.petalslink.dsb.api.ServiceEndpoint)
         */
        public void onRegister(ServiceEndpoint endpoint) throws DSBException {
            if (state) {
                this.listener.onRegister(endpoint);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onUnregister
         * (org.petalslink.dsb.api.ServiceEndpoint)
         */
        public void onUnregister(ServiceEndpoint endpoint) throws DSBException {
            if (state) {
                this.listener.onUnregister(endpoint);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.petalslink.dsb.kernel.api.messaging.RegistryListener#getName()
         */
        public String getName() {
            return this.listener.getName();
        }
    }

}
