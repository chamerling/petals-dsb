/**
 * 
 */
package org.petalslink.dsb.wsn.service;

import org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr;

import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * @author chamerling
 * 
 */
public class NotificationConsumerServiceServiceStr implements NotificationConsumerServiceStr {

    INotificationConsumer notificationConsumer;

    public NotificationConsumerServiceServiceStr(INotificationConsumer notificationConsumer) {
        this.notificationConsumer = notificationConsumer;
    }

    public void notify(String notify) {
        // create
        System.out.println("###############NOTIFY##########");
        System.out.println(notify);
        // this.notificationConsumer.notify(Adapters.asModel(notify));
    }
}
