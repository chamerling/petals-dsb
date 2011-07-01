/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service;

/**
 * The monitoring configuration service with static parameters. If parameters
 * needs to be changed at runtime, add another component...
 * 
 * @author chamerling
 * 
 */
public interface ConfigurationService {

    String getBaseURL();

    String getAdminURL();

    String getListenerURL();

    boolean isActive();

}
