/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.commons.api.NotificationSender;

/**
 * 
 * @author chamerling
 * 
 */
public class NotificationCenter {

    private static NotificationCenter instance;

    public static final NotificationCenter get() {
        if (instance == null) {
            instance = new NotificationCenter();
        }
        return instance;
    }

    private NotificationSender sender;
    
    private NotificationManager manager;

    private NotificationCenter() {
    }

    protected void setNotifificationSender(NotificationSender sender) {
        this.sender = sender;
    }

    /**
     * @return the sender
     */
    public NotificationSender getSender() {
        return sender;
    }

}
