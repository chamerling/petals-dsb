package org.petalslink.dsb.kernel.tools.remote;

import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.kernel.ws.client.WebServiceHelper;

/**
 * A Service to create the component address
 * 
 * @author chamerling - PetalsLink
 * 
 */
public class ComponentAddressResolver {

    public static final String PREFIX = "/container/services/";

    /**
     * Get a component address from its class name
     * 
     * @param <T>
     * @param containerConfiguration
     * @param clazz
     * @return
     */
    public static String getComponentAddress(ContainerConfiguration containerConfiguration,
            Class<?> clazz) {
        StringBuffer sb = new StringBuffer("http://");
        sb.append(containerConfiguration.getHost());
        sb.append(":");
        sb.append(containerConfiguration.getWebservicePort());
        sb.append(PREFIX);
        sb.append(WebServiceHelper.getWebServiceName(clazz));
        return sb.toString();
    }
}
