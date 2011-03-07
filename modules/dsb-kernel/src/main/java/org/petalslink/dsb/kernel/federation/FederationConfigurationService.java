/**
 * 
 */
package org.petalslink.dsb.kernel.federation;

/**
 * The service to configure the federation component
 * 
 * @author chamerling
 *
 */
public interface FederationConfigurationService {
    
    /**
     * The federation feature can be set to active or not
     * 
     * @return
     */
    boolean isActive();
    
    /**
     * The federation URL is used by the local node to send messages to the federation
     * 
     * @return
     */
    String getFederationURL();

}
