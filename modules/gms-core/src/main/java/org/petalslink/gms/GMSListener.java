package org.petalslink.gms;


/**
 * Listeners needs to be registered into the {@link GMSListenerManager}.
 * Implementations will do what they want on notifications...
 * 
 * @author chamerling
 * 
 */
public interface GMSListener {

    void onMessage(GMSMessage message);

}
