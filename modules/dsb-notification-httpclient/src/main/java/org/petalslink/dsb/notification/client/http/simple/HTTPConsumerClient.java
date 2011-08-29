/**
 * 
 */
package org.petalslink.dsb.notification.client.http.simple;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.HTTPNotificationConsumerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.notification.commons.api.client.simple.ConsumerClient;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;

/**
 * @author chamerling
 * 
 */
public class HTTPConsumerClient implements ConsumerClient {

    static Logger logger = Logger.getLogger(HTTPConsumerClient.class.getName());

    private String endpoint;

    private HTTPNotificationConsumerClient client;

    /**
     * 
     */
    public HTTPConsumerClient(String endpoint) {
        this.endpoint = endpoint;
        this.client = new HTTPNotificationConsumerClient(this.endpoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.api.client.simple.ConsumerClient
     * #notify(org.w3c.dom.Document, javax.xml.namespace.QName)
     */
    public void notify(Document payload, QName topic) throws NotificationException {
        // create the notification payload
        Notify notify = NotificationHelper.createNotification(null, endpoint, null, topic,
                "http://www.w3.org/TR/1999/REC-xpath-19991116", payload);
        try {
            this.client.notify(notify);
        } catch (WsnbException e) {
            throw new NotificationException(e);
        }
    }

}
