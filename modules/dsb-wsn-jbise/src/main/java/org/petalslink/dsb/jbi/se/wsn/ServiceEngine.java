/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;

/**
 * @author chamerling
 * 
 */
public class ServiceEngine implements Service {

    Map<QName, Service> services;

    /**
     * 
     */
    public ServiceEngine() {
        this.services = new HashMap<QName, Service>();
    }

    /**
     * 
     */
    public void addService(Service service, QName[] operation) {
        if (service != null && operation != null && operation.length > 0) {
            for (QName qName : operation) {
                services.put(qName, service);
            }
        }
    }

    public Service getService(QName qname) {
        return services.get(qname);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getWSDLURL()
     */
    public String getWSDLURL() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getURL()
     */
    public String getURL() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getEndpoint()
     */
    public QName getEndpoint() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getInterface()
     */
    public QName getInterface() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getService()
     */
    public QName getService() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public void invoke(SimpleExchange exchange) throws ServiceException {

        Service s = getService(exchange.getOperation());
        if (s == null) {
            throw new ServiceException(String.format("Unsupported operation %s in engine",
                    exchange.getOperation()));
        }
        s.invoke(exchange);
    }

}
