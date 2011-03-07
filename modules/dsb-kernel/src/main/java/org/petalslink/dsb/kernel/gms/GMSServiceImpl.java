package org.petalslink.dsb.kernel.gms;

import java.util.Set;

/**
 * 
 * @author chamerling - PetalsLink
 *
 */
public class GMSServiceImpl implements GMSService {
    
    private GMSManager gmsManager;

    public void hasJoined(GMSContext context) throws GMSException {
        Set<GMSListener> listeners = gmsManager.getListeners(context);
        // For now it is sequential...
        for(GMSListener listener : listeners) {
            try {
                listener.hasJoined(context);
            } catch (GMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void hasLeaved(GMSContext context) throws GMSException {
        Set<GMSListener> listeners = gmsManager.getListeners(context);
        // For now it is sequential...
        for(GMSListener listener : listeners) {
            try {
                listener.hasLeaved(context);
            } catch (GMSException e) {
                e.printStackTrace();
            }
        }
    }

}
