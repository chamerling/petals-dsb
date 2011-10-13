/**
 * 
 */
package org.petalslink.dsb.api.util;

import java.net.URI;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling
 * 
 */
public class EndpointHelper {

    public static final String DSB_PREFIX = "dsb";

    public static final String JAVA_PREFIX = "java";

    /**
     * 
     */
    private EndpointHelper() {
    }

    public static boolean isDSBService(String uri) {
        return uri != null && uri.startsWith(DSB_PREFIX + "://");
    }

    public static boolean isDSBService(URI uri) {
        return uri != null && uri.toString().startsWith(DSB_PREFIX + "://");
    }
    
    public static boolean isJavaService(URI uri) {
        return uri != null && uri.toString().startsWith(JAVA_PREFIX + "://");
    }

    public static org.petalslink.dsb.api.ServiceEndpoint getServiceEndpoint(URI uri) {
        ServiceEndpoint result = null;
        if (uri == null || !isDSBService(uri)) {
            return null;
        }
        result = new ServiceEndpoint();
        result.setEndpointName(getEndpoint(uri));
        result.setServiceName(getService(uri));
        return result;

    }

    public static QName getService(URI uri) {
        if (uri == null || !isDSBService(uri)) {
            return null;
        }

        String tmp = uri.toString();
        String serviceName = null;
        if (tmp.contains("@")) {
            serviceName = tmp.substring((DSB_PREFIX + "://").length(), tmp.indexOf('@'));
        } else {
            // we do not support URIs where service and endpoint are not both
            // defined
            return null;
        }

        String ns = "http://" + serviceName.substring(0, serviceName.lastIndexOf('/') + 1);
        String localPart = serviceName.substring(serviceName.lastIndexOf('/') + 1);
        return new QName(ns, localPart);
    }

    public static String getEndpoint(URI uri) {
        if (uri == null || !isDSBService(uri)) {
            return null;
        }

        String tmp = uri.toString();
        String endpointName = null;
        if (tmp.contains("@")) {
            endpointName = tmp.substring(tmp.indexOf('@') + 1);
        } else {
            // we do not support URIs where service and endpoint are not both
            // defined
            return null;
        }
        return endpointName;
    }
}
