/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.monitoring.api.MonitoringAdminClient;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.MonitoringClientFactory;

/**
 * A factory for easierBSM clients
 * 
 * @author chamerling
 * 
 */
public class EasierBSMClientFactory implements MonitoringClientFactory {

    private static Log logger = LogFactory.getLog(EasierBSMClientFactory.class);

    private Map<String, MonitoringClient> monitoringClients;

    private Map<String, MonitoringAdminClient> monitoringAdminClients;

    /**
     * 
     */
    public EasierBSMClientFactory() {
        System.out.println("instanciate");
        this.monitoringAdminClients = new ConcurrentHashMap<String, MonitoringAdminClient>();
        this.monitoringClients = new ConcurrentHashMap<String, MonitoringClient>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.monitoring.api.MonitoringClientFactory#getMonitoringClient
     * (java.lang.String)
     */
    public MonitoringClient getMonitoringClient(String address) {
        logger.info("Getting new monitoring client for " + address);
        if (this.monitoringClients.get(address) == null) {
            this.monitoringClients.put(address, new EasierBSMClient(address));
        }
        return this.monitoringClients.get(address);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.monitoring.api.MonitoringClientFactory#
     * getMonitoringAdminClient(java.lang.String)
     */
    public MonitoringAdminClient getMonitoringAdminClient(String address) {
        logger.info("Getting new admin monitoring client for " + address);
        if (this.monitoringAdminClients.get(address) == null) {
            this.monitoringAdminClients.put(address, new EasierBSMAdminClient(address));
        }
        return this.monitoringAdminClients.get(address);
    }
}
