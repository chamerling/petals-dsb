/**
 * 
 */
package org.petalslink.dsb.kernel.gms;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chamerling - PetalsLink
 * 
 */
public class GMSManagerImpl implements GMSManager {

    private Set<GMSListener> listenerRegistry;

    public GMSManagerImpl() {
        listenerRegistry = new HashSet<GMSListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.gms.GMSManager#register(eu.soa4all.dsb.petals
     * .kernel.gms.GMSListener)
     */
    public void register(GMSListener listener) {
        listenerRegistry.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.gms.GMSManager#getListeners(eu.soa4all.dsb
     * .petals.kernel.gms.GMSContext)
     */
    public Set<GMSListener> getListeners(GMSContext context) {
        return listenerRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.gms.GMSManager#getListeners()
     */
    public Set<GMSListener> getListeners() {
        return listenerRegistry;
    }

}
