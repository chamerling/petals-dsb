/**
 * 
 */
package org.petalslink.dsb.notification.commons.api;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.commons.NotificationException;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;

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
     * Notify with a given payload. This will build the notification message
     * with the given parameters.
     * 
     * @param notify
     * @param topic
     * @param dialect
     * @throws NotificationException
     */
    void notify(final Document payload, final QName topic, final String dialect)
            throws NotificationException;

    /**
     * Notify with a prefilled notification message.
     * 
     * @param notify
     * @throws NotificationException
     */
    void notify(Notify notify) throws NotificationException;

}
