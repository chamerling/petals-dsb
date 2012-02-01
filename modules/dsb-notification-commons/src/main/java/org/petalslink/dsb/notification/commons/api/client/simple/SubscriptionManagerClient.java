/**
 * 
 */
package org.petalslink.dsb.notification.commons.api.client.simple;

import org.petalslink.dsb.notification.commons.NotificationException;

/**
 * @author chamerling
 * 
 */
public interface SubscriptionManagerClient {

    boolean unsubscribe(String uuid) throws NotificationException;

}
