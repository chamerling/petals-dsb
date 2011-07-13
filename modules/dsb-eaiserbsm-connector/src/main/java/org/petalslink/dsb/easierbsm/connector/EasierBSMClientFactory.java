/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import org.petalslink.dsb.monitoring.api.MonitoringAdminClient;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.MonitoringClientFactory;

/**
 * TODO
 * 
 * @author chamerling
 *
 */
public class EasierBSMClientFactory implements MonitoringClientFactory {

    /* (non-Javadoc)
     * @see org.petalslink.dsb.monitoring.api.MonitoringClientFactory#getMonitoringClient(java.lang.String)
     */
    public MonitoringClient getMonitoringClient(String address) {
        return new EasierBSMClient();
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.monitoring.api.MonitoringClientFactory#getMonitoringAdminClient(java.lang.String)
     */
    public MonitoringAdminClient getMonitoringAdminClient(String adress) {
        return new EasierBSMAdminClient();
    }

}
