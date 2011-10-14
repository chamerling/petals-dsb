/**
 * 
 */
package org.petalslink.dsb.kernel.api.notification;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.DSBException;
import org.w3c.dom.Document;

/**
 * A really simple notification sender. Send notification on topics ie on all
 * the consumers which are registered in the topic... A call to
 * {@link #notify(Document, QName)} can reach N destinations. It is up to the
 * implementation to handle thread pools for efficiency for example...
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
    void notify(final Document payload, final QName topic) throws DSBException;

}
