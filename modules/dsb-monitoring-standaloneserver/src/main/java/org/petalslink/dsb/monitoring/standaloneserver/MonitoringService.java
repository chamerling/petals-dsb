/**
 * 
 */
package org.petalslink.dsb.monitoring.standaloneserver;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.ReportListBean;

/**
 * A simple service which receives reports...
 * 
 * @author chamerling
 * 
 */
public class MonitoringService implements MonitoringClient {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.monitoring.api.MonitoringClient#send(org.petalslink
     * .dsb.monitoring.api.ReportList)
     */
    public void send(ReportListBean reportList) throws DSBException {
        System.out.println("+++++++++++++++++++++++++++++++");
        System.out.println("Receiving a report");
        System.out.println(reportList);
        System.out.println("-------------------------------");

    }

}
