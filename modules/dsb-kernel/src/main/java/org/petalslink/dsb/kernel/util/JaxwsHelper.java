/**
 * 
 */
package org.petalslink.dsb.kernel.util;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.support.JaxWsImplementorInfo;

/**
 * Helper for JAXWS annotation processing based on CXF.
 * 
 * @author chamerling
 * 
 */
public class JaxwsHelper {

    public static QName getInterfaceName(Class<?> serviceEndpointInterface) {
        JaxWsImplementorInfo info = new JaxWsImplementorInfo(serviceEndpointInterface);
        return info.getInterfaceName();
    }

    public static QName getServiceName(Class<?> serviceEndpointInterface) {
        JaxWsImplementorInfo info = new JaxWsImplementorInfo(serviceEndpointInterface);
        return info.getServiceName();
    }

    public static QName getEndpointName(Class<?> serviceEndpointInterface) {
        JaxWsImplementorInfo info = new JaxWsImplementorInfo(serviceEndpointInterface);
        return info.getEndpointName();
    }

}
