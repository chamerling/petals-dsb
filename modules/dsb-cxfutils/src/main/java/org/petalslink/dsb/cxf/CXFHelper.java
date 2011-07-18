/**
 * 
 */
package org.petalslink.dsb.cxf;

import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.jaxws.JAXWSHelper;

/**
 * @author chamerling
 * 
 */
public class CXFHelper {

    /**
     * Get a client for the given class
     * 
     * @param <T>
     * @param baseURL
     * @param clazz
     * @return
     */
    public static <T> T getClient(String baseURL, Class<T> clazz) {
        String address = baseURL;
        if (!address.endsWith("/")) {
            address = address + "/";
        }
        address = address + JAXWSHelper.getWebServiceName(clazz);
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(address);
        factory.setServiceClass(clazz);
        Object client = factory.create();
        return clazz.cast(client);
    }

    /**
     * Get a client instance for a given URL. The URL is a final one and is not
     * built from the JAXWS annotations like in
     * {@link #getClient(String, Class)}
     * 
     * @param <T>
     * @param fullURL
     * @param clazz
     * @return
     */
    public static <T> T getClientFromFinalURL(String fullURL, Class<T> clazz) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(fullURL);
        factory.setServiceClass(clazz);
        Object client = factory.create();
        return clazz.cast(client);
    }

    public static <T> Server getService(String baseURL, Class<T> clazz, Object bean) {
        final JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setDataBinding(new JAXBDataBinding());
        sf.setServiceBean(bean);
        Class<?> wsClass = JAXWSHelper.getWebServiceClass(clazz);
        String serviceName = JAXWSHelper.getWebServiceName(wsClass);
        if (serviceName == null) {
            serviceName = clazz.getSimpleName();
        }
        String address = baseURL;
        if (!address.endsWith("/")) {
            address = address + "/";
        }
        address = address + serviceName;

        sf.setAddress(address);
        sf.setServiceClass(wsClass);

        return new Server() {
            org.apache.cxf.endpoint.Server server;

            public void stop() {
                server.stop();
            }

            public void start() {
                server = sf.create();
            }
        };
    }
}
