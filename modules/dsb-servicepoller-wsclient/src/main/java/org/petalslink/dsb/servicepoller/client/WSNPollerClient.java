/**
 * 
 */
package org.petalslink.dsb.servicepoller.client;

import javax.xml.namespace.QName;

import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.servicepoller.api.Utils;
import org.petalslink.dsb.servicepoller.api.WSNPoller;
import org.petalslink.dsb.servicepoller.api.WSNPollerService;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class WSNPollerClient implements WSNPoller {
    WSNPollerService client;

    String address;

    /**
     * 
     */
    public WSNPollerClient(String address) {
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
    public String start(ServicePollerInformation toPoll, Document inputMessage,
            String cronExpression, ServicePollerInformation replyTo, QName topic)
            throws ServicePollerException {
        DocumentHandler data = Utils.toDataHandler(inputMessage);
        return getWSClient().start(toPoll, data, cronExpression, replyTo, topic.getLocalPart(),
                topic.getNamespaceURI(), topic.getPrefix());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#stop(java.lang.String)
     */
    public boolean stop(String id) throws ServicePollerException {
        return getWSClient().stop(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#pause(java.lang.String
     * )
     */
    public boolean pause(String id) throws ServicePollerException {
        return getWSClient().pause(id);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#resume(java.lang.String
     * )
     */
    public boolean resume(String id) throws ServicePollerException {
        return getWSClient().resume(id);

    }

    private synchronized WSNPollerService getWSClient() {
        if (client == null) {
            client = CXFHelper.getClient(address, WSNPollerService.class);
        }
        return this.client;
    }
}
