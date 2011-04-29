/**
 * 
 */
package org.petalslink.gms;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the listeners ie some listeners are registered by the local container
 * in order to get notified when new nodes join or leave the network.
 * 
 * @author chamerling - PetalsLink
 * 
 */
public class GMSListenerManagerImpl implements GMSListenerManager {
    
    private static final Logger LOG = Logger.getLogger(GMSListenerManagerImpl.class.getName());

    private Set<GMSListener> listenerRegistry;

    /**
     * 
     */
    public GMSListenerManagerImpl() {
        this.listenerRegistry = new HashSet<GMSListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.gms.GMSManager#register(eu.soa4all.dsb.petals
     * .kernel.gms.GMSListener)
     */
    public void register(GMSListener listener) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering new listener : " + listener);
        }
        listenerRegistry.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.gms.GMSManager#getListeners()
     */
    public Set<GMSListener> getListeners() {
        return listenerRegistry;
    }

    /*
     * (non-Javadoc)
     * @see org.petalslink.dsb.kernel.gms.GMSListenerManager#unregister(org.petalslink.dsb.kernel.gms.GMSListener)
     */
    public void unregister(GMSListener listener) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering listener : " + listener);
        }
        this.listenerRegistry.remove(listener);
    }

}
