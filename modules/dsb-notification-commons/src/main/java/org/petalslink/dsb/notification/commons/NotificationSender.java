/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * Send notification on topics ie on all the consumers which are registered in
 * the topic... A call to {@link #notify(Document, QName, String)} can reach N
 * destination. It is up to the implementation to handle thread pools for
 * efficiency for example...
 * 
 * @author chamerling
 * 
 */
public interface NotificationSender {

    /**
     * 
     * @param notify
     * @param topic
     * @param dialect
     * @throws NotificationException
     */
    void notify(final Document payload, final QName topic, final String dialect)
            throws NotificationException;

}
