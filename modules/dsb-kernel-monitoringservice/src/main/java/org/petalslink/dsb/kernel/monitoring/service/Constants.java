/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service;

/**
 * @author chamerling
 * 
 */
public interface Constants {

    static String CONFIG_FILE = "monitoring.cfg";

    static String ACTIVE_PARAM = "monitoring.active";

    static String BASEURL_PARAM = "monitoring.base";

    static String ADMINURL_PARAM = "monitoring.admin";

    static String LISTENERURL_PARAM = "monitoring.listener";
    
    static final String DEFAULT_MONITORING_REGISTRATION_URL = "http://localhost:8085/services/adminExternalEndpoint";
    
    static final String DEFAULT_BASE_URL = "http://localhost:8085/services/";

}
