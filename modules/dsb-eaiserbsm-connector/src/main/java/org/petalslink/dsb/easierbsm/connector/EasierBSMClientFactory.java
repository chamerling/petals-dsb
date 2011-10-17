/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import java.util.Map;
import java.util.Properties;
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

    public static final String BASE_URL = "easierbsm.base";

    public static final String ADMIN_URL = "easierbsm.admin";
    
    private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";
    
    private static final String RAW_REPORT_SERVICE_ENDPOINT = "rawReportEndpointClientProxyEndpoint";

    private static Log logger = LogFactory.getLog(EasierBSMClientFactory.class);

    private Map<String, MonitoringClient> monitoringClients;

    private Map<String, MonitoringAdminClient> monitoringAdminClients;

    private Properties props;

    /**
     * 
     */
    public EasierBSMClientFactory(Properties props) {
        this.props = props;
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
    public MonitoringClient getMonitoringClient(String endpointName) {
        logger.info("Getting new monitoring client for endpoint " + endpointName);
        String baseURL = props.getProperty(BASE_URL);
        if (baseURL != null) {
            baseURL = baseURL.trim();
        } else {
            // FIXME : Ouch!!!
            return null;
        }

        if (!baseURL.endsWith("/")) {
            baseURL = baseURL + "/";
        }

        String address = baseURL + endpointName + ENDPOINT_SUFFIX;

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
    public MonitoringAdminClient getMonitoringAdminClient() {
        logger.info("Getting new admin monitoring client");
        
        String baseURL = props.getProperty(ADMIN_URL);
        if (baseURL != null) {
            baseURL = baseURL.trim();
        } else {
            // FIXME : Ouch!!!
            return null;
        }
        
        if (this.monitoringAdminClients.get(baseURL) == null) {
            this.monitoringAdminClients.put(baseURL, new EasierBSMAdminClient(baseURL));
        }
        return this.monitoringAdminClients.get(baseURL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.monitoring.api.MonitoringClientFactory#
     * getRawMonitoringClient()
     */
    public MonitoringClient getRawMonitoringClient() {
        logger.info("Getting new RAW monitoring client");
        
        String baseURL = props.getProperty(BASE_URL);
        if (baseURL != null) {
            baseURL = baseURL.trim();
        } else {
            // FIXME : Ouch!!!
            return null;
        }

        if (!baseURL.endsWith("/")) {
            baseURL = baseURL + "/";
        }
        
        String address = baseURL + RAW_REPORT_SERVICE_ENDPOINT;
        
        if (this.monitoringClients.get(address) == null) {
            this.monitoringClients.put(address, new EasierBSMClient(address));
        }
        return this.monitoringClients.get(address);
    }
}
