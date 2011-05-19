/**
 * 
 */
package org.petalslink.dsb.wsn.service;

import org.petalslink.dsb.wsn.api.NotificationConsumerService;
import org.petalslink.dsb.wsn.utils.Adapters;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.NotifyImpl;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * @author chamerling
 * 
 */
public class NotificationConsumerServiceService implements NotificationConsumerService {

    INotificationConsumer notificationConsumer;

    public NotificationConsumerServiceService(INotificationConsumer notificationConsumer) {
        this.notificationConsumer = notificationConsumer;
    }

    public void notify(Notify notify) {
        try {
            this.notificationConsumer.notify(Adapters.asModel(notify));
        } catch (WsnbException e) {
            e.printStackTrace();
        }
    }
}
