/**
 * 
 */
package org.petalslink.dsb.monitoring.standaloneserver;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.monitoring.api.MonitoringAdminClient;

/**
 * @author chamerling
 * 
 */
public class MonitoringAdminService implements MonitoringAdminClient {

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.monitoring.api.MonitoringAdminClient#
     * createMonitoringEndpoint(org.petalslink.dsb.api.ServiceEndpoint)
     */
    public void createMonitoringEndpoint(ServiceEndpoint serviceEndpoint) throws DSBException {
        System.out.println("Create a monitoring endpoint for " + serviceEndpoint);
    }

}
