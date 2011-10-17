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
    MonitoringClient getMonitoringClient(String endpointName);

    /**
     * Get a raw monitoring client used to send raw reports to an endpoint which
     * can manage them.
     * 
     * @return
     */
    MonitoringClient getRawMonitoringClient();

    /**
     * Get a management client which is in charge of creating stuff on the
     * monitoring size on endpoint creation.
     * 
     * @param adress
     * @return
     */
    MonitoringAdminClient getMonitoringAdminClient();

}
