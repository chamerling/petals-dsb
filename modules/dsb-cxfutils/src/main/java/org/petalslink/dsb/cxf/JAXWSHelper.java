/**
 * 
 */
package org.petalslink.dsb.cxf;

import javax.jws.WebService;

import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

/**
 * @author chamerling
 * 
 */
public class JAXWSHelper {

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
        address = address + getWebServiceName(clazz);
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(address);
        factory.setServiceClass(clazz);
        Object client = factory.create();
        return clazz.cast(client);
    }

    public static <T> Server getService(String baseURL, Class<T> clazz, Object bean) {
        final JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setDataBinding(new JAXBDataBinding());
        sf.setServiceBean(bean);
        Class<?> wsClass = getWebServiceClass(clazz);
        String serviceName = getWebServiceName(wsClass);
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

    public static final Class<?> getWebServiceClass(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        if (null != cls.getAnnotation(WebService.class)) {
            return cls;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(WebService.class)) {
                return inf;
            }
        }
        return getWebServiceClass(cls.getSuperclass());
    }

    public static final String getWebServiceName(Class<?> wsClass) {
        String serviceName = null;
        WebService anno = wsClass.getAnnotation(WebService.class);
        if ((anno.serviceName() == null) || (anno.serviceName().trim().length() == 0)) {
            serviceName = wsClass.getSimpleName();
        } else {
            serviceName = anno.serviceName();
        }
        return serviceName;
    }

    public static boolean hasWebServiceAnnotation(Class<?> cls) {
        if (cls == null) {
            return false;
        }
        if (null != cls.getAnnotation(WebService.class)) {
            return true;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(WebService.class)) {
                return true;
            }
        }
        return hasWebServiceAnnotation(cls.getSuperclass());
    }

}
