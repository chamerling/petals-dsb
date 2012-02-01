/**
 * 
 */
package org.petalslink.dsb.notification.client.http.simple;

import org.petalslink.dsb.notification.client.http.HTTPubscriptionManagerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.notification.commons.api.client.simple.SubscriptionManagerClient;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class HTTPSubscriptionManagerClient implements SubscriptionManagerClient {

    private HTTPubscriptionManagerClient client;

    /**
     * 
     */
    public HTTPSubscriptionManagerClient(String endpoint) {
        this.client = new HTTPubscriptionManagerClient(endpoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.api.client.simple.
     * SubscriptionManagerClient#unsubscribe(java.lang.String)
     */
    public boolean unsubscribe(String uuid) throws NotificationException {
        if (uuid == null) {
            throw new NotificationException("Null input subscription UUID!");
        }
        boolean result = false;
        Unsubscribe unsubscribe = NotificationHelper.createUnsubscribe(uuid);
        try {
            UnsubscribeResponse response = this.client.unsubscribe(unsubscribe);
            result = response != null;
        } catch (Exception e) {
            throw new NotificationException(e);

        }
        return result;
    }

}
