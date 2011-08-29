/**
 * 
 */
package org.petalslink.dsb.notification.commons.api.client.simple;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.commons.NotificationException;
import org.w3c.dom.Document;

/**
 * @author chamerling
 *
 */
public interface ConsumerClient {
    
    /**
     * Send a notify to the given topic
     * 
     * @param payload
     * @param topic
     */
    void notify(Document payload, QName topic) throws NotificationException;

}
