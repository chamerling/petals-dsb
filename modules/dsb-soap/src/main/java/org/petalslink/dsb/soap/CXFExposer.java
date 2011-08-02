/**
 * 
 */
package org.petalslink.dsb.soap;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.petalslink.dsb.cxf.Server;
import org.petalslink.dsb.soap.api.Exposer;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;

/**
 * @author chamerling
 * 
 */
public class CXFExposer implements Exposer {
    
    private List<Service> services;

    /**
     * 
     */
    public CXFExposer() {
        this.services = new ArrayList<Service>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Exposer#expose(org.petalslink.dsb.soap.api
     * .Service)
     */
    public Server expose(final Service service) throws ServiceException {
        // create the wrapper and push it to CXF...
        ServiceWrapper wrapper = new ServiceWrapper(service);
        JaxWsServiceFactoryBean sf = new JaxWsServiceFactoryBean();
        sf.setPopulateFromClass(false);
        sf.setValidate(false);
        sf.setWsdlURL(service.getWSDLURL());
        sf.setServiceName(service.getService());
        sf.setEndpointName(service.getEndpoint());
        final JaxWsServerFactoryBean ssf = new JaxWsServerFactoryBean(sf);
        ssf.setAddress(service.getURL());
        sf.setPopulateFromClass(false);
        ssf.setServiceBean(wrapper);

        return new Server() {
            org.apache.cxf.endpoint.Server cxfServer;

            public void stop() {
                if (cxfServer != null) {
                    cxfServer.stop();
                }
                services.remove(service);
            }

            public void start() {
                cxfServer = ssf.create();
                services.add(service);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Exposer#getServices()
     */
    public List<Service> getServices() {
        return this.services;
    }

}
