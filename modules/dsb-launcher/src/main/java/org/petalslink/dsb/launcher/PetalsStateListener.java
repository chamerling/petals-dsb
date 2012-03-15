/**
 * 
 */
package org.petalslink.dsb.launcher;

/**
 * Start/Stop hook
 * 
 * @author chamerling
 *
 */
public interface PetalsStateListener {
    
    void onPetalsStarted();
    
    void onPetalsStopped(boolean success, Exception exception);


}
