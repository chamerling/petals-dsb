/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.util.List;

/**
 * Scans all the kernel components to retrieve all the notification consumers.
 * 
 * @author chamerling
 * 
 */
public interface NotificationConsumerScanner {

    /**
     * Get all the notification aware components
     * 
     * @return
     */
    List<NotificationTargetBean> scan();

}
