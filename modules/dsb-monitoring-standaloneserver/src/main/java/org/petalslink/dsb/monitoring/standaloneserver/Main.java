/**
 * 
 */
package org.petalslink.dsb.monitoring.standaloneserver;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.monitoring.api.MonitoringClient;

/**
 * @author chamerling
 * 
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String baseURL = "http://localhost:6475/monitoring/";
        String monitoringURL = "http://localhost:6475/monitoringadmin/";

        System.out.println("Starting report server at " + baseURL);
        Service reportServer = CXFHelper.getService(baseURL, MonitoringClient.class,
                new MonitoringService());
        reportServer.start();

        Service monitoringService = CXFHelper.getService(monitoringURL,
                MonitoringAdminService.class, new MonitoringAdminService());
        monitoringService.start();

        // wait for something...
        try {
            Thread.sleep(9999999999L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        reportServer.stop();
        monitoringService.stop();
    }

}
