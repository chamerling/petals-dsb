/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

/**
 * A factory to get monitoring clients...
 * 
 * @author chamerling
 * 
 */
public interface MonitoringClientFactory {

    /**
     * Get a monitoring client for the given adress. Up to the implementation to
     * cache it or create it when needed...
     * 
     * @param adress
     * @return
     */
    MonitoringClient getMonitoringClient(String address);

    /**
     * Get a management client which is in charge of creating stuff on the
     * monitoring size on endpount creation.
     * 
     * @param adress
     * @return
     */
    MonitoringAdminClient getMonitoringAdminClient(String adress);

}
