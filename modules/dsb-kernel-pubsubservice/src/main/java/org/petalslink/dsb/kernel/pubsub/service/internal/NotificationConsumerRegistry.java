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
    void add(String id, InternalNotificationConsumer consumer);

    /**
     * Get a consumer from its ID...
     * 
     * @param id
     * @return
     */
    InternalNotificationConsumer get(String id);

    /**
     * Remove and return the consumer if any registered
     * 
     * @param id
     * @return
     */
    InternalNotificationConsumer remove(String id);

}
