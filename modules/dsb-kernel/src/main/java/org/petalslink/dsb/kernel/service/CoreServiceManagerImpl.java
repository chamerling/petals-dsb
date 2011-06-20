/**
 * 
 */
package org.petalslink.dsb.kernel.service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.kernel.api.service.CoreServiceManager;
import org.petalslink.dsb.kernel.api.service.Server;
import org.petalslink.dsb.kernel.cxf.CXFBus;
import org.petalslink.dsb.kernel.util.JaxwsHelper;

/**
 * A client factory for JAXWS annotated classes. It creates Petals client
 * 
 * @author chamerling
 * 
 */
public class CoreServiceManagerImpl implements CoreServiceManager {

    public <T> T getClient(Class<T> clazz, String host) {
        // FIXME = Remove since the CXF bsus should have been initialized by the
        // CXFBus class
        CXFBus.getInstance();
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        // TODO, get the service name from the JAXWS annotated class
        factory.setAddress(org.petalslink.dsb.kernel.api.service.Constants.PREFIX + "://" + host + "/"
                + JaxwsHelper.getEndpointName(clazz).getLocalPart());
        factory.setServiceClass(clazz);
        Object client = factory.create();
        return clazz.cast(client);
    }

    public <T> Server createService(Class<T> serviceClass, Object implementation, String container) {
        // FIXME : singleton
        CXFBus.getInstance();

        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setAddress(org.petalslink.dsb.kernel.api.service.Constants.PREFIX + "://" + container + "/"
                + JaxwsHelper.getEndpointName(serviceClass).getLocalPart());
        sf.setServiceClass(serviceClass);
        sf.setServiceBean(implementation);
        final org.apache.cxf.endpoint.Server cxfServer = sf.create();
        return new Server() {
            
            public void stop() {
                cxfServer.stop();
            }
            
            public void start() {
                cxfServer.start();
            }
        };
    }
}
