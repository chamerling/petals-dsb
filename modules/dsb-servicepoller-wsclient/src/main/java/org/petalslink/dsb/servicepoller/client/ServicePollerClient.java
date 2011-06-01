/**
 * 
 */
package org.petalslink.dsb.servicepoller.client;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerService;
import org.petalslink.dsb.servicepoller.api.Utils;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ServicePollerClient implements ServicePoller {

    ServicePollerService client;

    String address;

    /**
     * 
     */
    public ServicePollerClient(String address) {
        this.address = address;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#start(java.lang.String
     * , javax.xml.namespace.QName, javax.xml.namespace.QName,
     * javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void start(String endpointName, QName service, QName itf, QName operation,
            Document inputMessage) throws ServicePollerException {
        DocumentHandler data = Utils.toDataHandler(inputMessage);
        getWSClient().start(endpointName, service, itf, operation, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#stop(java.lang.String,
     * javax.xml.namespace.QName, javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void stop(String endpointName, QName service, QName itf, QName operation)
            throws ServicePollerException {
        getWSClient().stop(endpointName, service, itf, operation);
    }

    private synchronized ServicePollerService getWSClient() {
        if (client == null) {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setAddress(address);
            factory.setServiceClass(ServicePollerService.class);
            client = (ServicePollerService) factory.create();
        }
        return this.client;
    }

}
