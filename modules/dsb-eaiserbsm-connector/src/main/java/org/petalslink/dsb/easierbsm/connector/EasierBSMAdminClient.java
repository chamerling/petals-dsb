/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.monitoring.api.MonitoringAdminClient;

import easyesb.petalslink.com.service.wsdmadmin._1_0.AdminExceptionMsg;
import easyesb.petalslink.com.service.wsdmadmin._1_0.WSDMAdminItf;

/**
 * @author chamerling
 * 
 */
public class EasierBSMAdminClient implements MonitoringAdminClient {

    private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";

    private String address;

    private WSDMAdminItf wsdmAdmin;

    private static Log logger = LogFactory.getLog(EasierBSMAdminClient.class);

    /**
     * 
     */
    public EasierBSMAdminClient(final String address) {
        this.address = address;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.monitoring.api.MonitoringAdminClient#
     * createMonitoringEndpoint(org.petalslink.dsb.api.ServiceEndpoint)
     */
    public void createMonitoringEndpoint(ServiceEndpoint serviceEndpoint) throws DSBException {
        if (logger.isInfoEnabled()) {
            logger.info("Creating monitoring endpoint for service " + serviceEndpoint);
        }
        if (serviceEndpoint == null) {
            final String message = "Can not create monitoring endpoint from null endpoint...";
            throw new DSBException(message);
        }
        WSDMAdminItf wsdmAdminItf = getAdminClient();
        String wsdmProviderEndpointName = serviceEndpoint.getEndpointName() + ENDPOINT_SUFFIX;
        String result = null;
        try {
            result = wsdmAdminItf.createMonitoringEndpoint(serviceEndpoint.getServiceName(),
                    wsdmProviderEndpointName, true);
        } catch (AdminExceptionMsg e) {
            final String message = "Error while sending request to monitoring layer";
            if (logger.isWarnEnabled()) {
                logger.warn(message, e);
            }
            throw new DSBException(message, e);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Monitoring bus returned " + result);
        }
    }

    /**
     * @return
     */
    private synchronized WSDMAdminItf getAdminClient() {
        if (wsdmAdmin == null) {
            wsdmAdmin = CXFHelper.getClientFromFinalURL(address, WSDMAdminItf.class);
        }
        return wsdmAdmin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.monitoring.api.MonitoringAdminClient#
     * deleteMonitoringEndpoint(org.petalslink.dsb.api.ServiceEndpoint)
     */
    public void deleteMonitoringEndpoint(ServiceEndpoint serviceEndpoint) throws DSBException {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting monitoring endpoint for service " + serviceEndpoint);
        }
        throw new DSBException("deleteMonitoringEndpoint is not implemented");
    }
}
