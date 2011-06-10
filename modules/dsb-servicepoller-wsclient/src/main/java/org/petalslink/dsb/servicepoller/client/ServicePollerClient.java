/**
 * 
 */
package org.petalslink.dsb.servicepoller.client;

import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
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
    public void start(ServicePollerInformation toPoll, Document inputMessage,
            String cronExpression, ServicePollerInformation replyTo) throws ServicePollerException {
        DocumentHandler data = Utils.toDataHandler(inputMessage);
        getWSClient().start(toPoll, data, cronExpression, replyTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#stop(java.lang.String,
     * javax.xml.namespace.QName, javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo)
            throws ServicePollerException {
        getWSClient().stop(toPoll, replyTo);
    }

    private synchronized ServicePollerService getWSClient() {
        if (client == null) {
            client = CXFHelper.getClient(address, ServicePollerService.class);
        }
        return this.client;
    }

}
