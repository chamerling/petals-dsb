/**
 * 
 */
package org.petalslink.dsb.notification.client.http.simple;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.HTTPNotificationProducerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.notification.commons.api.client.simple.ProducerClient;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class HTTPProducerClient implements ProducerClient {

    private String endpoint;

    private HTTPNotificationProducerClient client;

    /**
     * 
     */
    public HTTPProducerClient(String me) {
        this.endpoint = me;
        this.client = new HTTPNotificationProducerClient(this.endpoint);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.api.client.simple.ProducerClient
     * #subscribe(javax.xml.namespace.QName, java.lang.String)
     */
    public String subscribe(QName topic, String me) throws NotificationException {
        String result = "";
        Subscribe subscribe = NotificationHelper.createSubscribe(me, topic);
        try {
            SubscribeResponse response = this.client.subscribe(subscribe);
            if (response.getSubscriptionReference() != null
                    && response.getSubscriptionReference().getReferenceParameters() != null) {
                result = Wsnb4ServUtils.getSubscriptionIdFromReferenceParams(response
                        .getSubscriptionReference().getReferenceParameters());

            }
        } catch (Exception e) {
            throw new NotificationException(e);
        }
        return result;
    }
}
