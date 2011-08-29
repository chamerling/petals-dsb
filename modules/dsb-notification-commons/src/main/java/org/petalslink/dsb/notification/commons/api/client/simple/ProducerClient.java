/**
 * 
 */
package org.petalslink.dsb.notification.commons.api.client.simple;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.commons.NotificationException;

/**
 * @author chamerling
 * 
 */
public interface ProducerClient {

    /**
     * 
     * @param topic
     *            the topic to subscribe to
     * @param me
     *            the endpoint address of the service which will receive the
     *            notifications
     * @return the subscription ID
     */
    String subscribe(QName topic, String me) throws NotificationException;

}
