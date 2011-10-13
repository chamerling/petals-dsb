/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import javax.xml.namespace.QName;

import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * @author chamerling
 *
 */
public interface InternalNotificationConsumer extends INotificationConsumer {
    
    NotificationTargetBean getTarget();
    
    QName getTopic();

}
