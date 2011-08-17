/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.dsb.notification.commons.api.NotificationManager;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * A notification conumer implementation which does nothing... It is up to the
 * implementation to deal with incoming notify messages...
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumer implements INotificationConsumer {

    private static Logger logger = Logger.getLogger(NotificationConsumer.class.getName());

    private NotificationManager notificationManager;

    /**
	 * 
	 */
    public NotificationConsumer(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
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
            logger.fine("Got a notify from external service, let's forward to engine...");
        }
        System.out.println("TODO NOTIFY...");
    }

}
