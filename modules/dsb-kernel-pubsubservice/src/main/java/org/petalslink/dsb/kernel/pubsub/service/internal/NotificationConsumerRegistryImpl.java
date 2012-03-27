/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationConsumerRegistry.class) })
public class NotificationConsumerRegistryImpl implements NotificationConsumerRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, InternalNotificationConsumer> consumers;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.consumers = new HashMap<String, InternalNotificationConsumer>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.internal.
     * NotificationConsumerRegistry
     * #add(org.petalslink.dsb.kernel.pubsub.service.
     * internal.InternalNotificationConsumer)
     */
    public void add(String id, InternalNotificationConsumer consumer) {
        if (consumer != null && consumer.getTarget() != null) {
            this.consumers.put(id, consumer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.internal.
     * NotificationConsumerRegistry#get(java.lang.String)
     */
    public InternalNotificationConsumer get(String id) {
        return this.consumers.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.internal.
     * NotificationConsumerRegistry#remove(java.lang.String)
     */
    public InternalNotificationConsumer remove(String id) {
        return this.consumers.remove(id);
    }

}
