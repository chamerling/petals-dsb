/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.ReportListBean;

/**
 * This client is in charge of sending report list to easierBSM.
 * 
 * @author chamerling
 * 
 */
public class EasierBSMClient implements MonitoringClient {
    
    private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.monitoring.api.MonitoringClient#send(org.petalslink
     * .dsb.monitoring.api.ReportListBean)
     */
    public void send(ReportListBean reportList) throws DSBException {
        System.out.println("TODO");
    }
}
