/**
 * 
 */
package org.petalslink.dsb.notification.commons.api;

import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;

/**
 * The notification manager is the entry point of the notification engines and
 * services. ALl should be accessible from this class.
 * 
 * @author chamerling
 * 
 */
public interface NotificationManager {

    /**
     * @return the topicNamespace
     */
    TopicNamespaceType getTopicNamespace();

    /**
     * @return the topicSet
     */
    TopicSetType getTopicSet();

    /**
     * @return the topicsManagerEngine
     */
    TopicsManagerEngine getTopicsManagerEngine();

    /**
     * @return the subscriptionManagerEngine
     */
    SubscriptionManagerEngine getSubscriptionManagerEngine();

    /**
     * @return the notificationProducerEngine
     */
    NotificationProducerEngine getNotificationProducerEngine();

}
