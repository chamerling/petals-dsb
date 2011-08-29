/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;

import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;

/**
 * The Notification Manager component. It is automatically initialized at DSB
 * startup.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationManager.class) })
public class NotificationManagerServiceImpl implements NotificationManager {

    @Requires(name = "configuration", signature = NotificationConfiguration.class)
    private NotificationConfiguration configuration;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private NotificationManager manager;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener
    public void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing notification manager...");
        }
        this.manager = new NotificationManagerImpl(configuration.getTopicNamespaces(),
                configuration.getSupportedTopics(), configuration.getServiceName(),
                configuration.getInterfaceName(), configuration.getEndpointName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.NotificationManager#getTopicNamespace
     * ()
     */
    public TopicNamespaceType getTopicNamespace() {
        return manager.getTopicNamespace();
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.notification.commons.api.NotificationManager#getSupportedTopics()
     */
    public List<String> getSupportedTopics() {
        return manager.getSupportedTopics();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.NotificationManager#getTopicSet()
     */
    public TopicSetType getTopicSet() {
        return manager.getTopicSet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getTopicsManagerEngine()
     */
    public TopicsManagerEngine getTopicsManagerEngine() {
        return manager.getTopicsManagerEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getSubscriptionManagerEngine()
     */
    public SubscriptionManagerEngine getSubscriptionManagerEngine() {
        return manager.getSubscriptionManagerEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getNotificationProducerEngine()
     */
    public NotificationProducerEngine getNotificationProducerEngine() {
        return manager.getNotificationProducerEngine();
    }

}
