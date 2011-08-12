/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;


/**
 * A registry used to register all the notification consumers
 * 
 * @author chamerling
 *
 */
public interface NotificationConsumerRegistry {
    
    /**
     * Add a notification consumer to the registry
     * 
     * @param consumer
     */
    void add(InternalNotificationConsumer consumer);
    
}
