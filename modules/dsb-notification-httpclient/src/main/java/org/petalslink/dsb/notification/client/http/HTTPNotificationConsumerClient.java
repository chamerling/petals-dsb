/**
 * 
 */
package org.petalslink.dsb.notification.client.http;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class HTTPNotificationConsumerClient implements INotificationConsumer {

    static Logger logger = Logger.getLogger(HTTPNotificationConsumerClient.class.getName());

    private String endpoint;

    /**
     * 
     */
    public HTTPNotificationConsumerClient(String endpoint) {
        this.endpoint = endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer#notify(
     * com.ebmwebsourcing
     * .wsstar.basenotification.datatypes.api.abstraction.Notify)
     */
    public void notify(Notify notify) throws WsnbException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Sending notify message to " + endpoint);
        }
        
        if (notify == null) {
            throw new WsnbException("Notify message can not be null");
        }
        
        final Document payload = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);

        Client client = new org.petalslink.dsb.service.client.saaj.Client();
        try {
            client.sendReceive(new Message() {

                public QName getService() {
                    return null;
                }

                public Map<String, String> getProperties() {
                    return null;
                }

                public Document getPayload() {
                    return payload;
                }

                public QName getOperation() {
                    return WsnbConstants.NOTIFY_QNAME;
                }

                public QName getInterface() {
                    return null;
                }

                public Map<String, Document> getHeaders() {
                    return null;
                }

                public String getEndpoint() {
                    return endpoint;
                }
            });
        } catch (ClientException e) {
            e.printStackTrace();
            throw new WsnbException(e.getMessage());
        }
    }
}
