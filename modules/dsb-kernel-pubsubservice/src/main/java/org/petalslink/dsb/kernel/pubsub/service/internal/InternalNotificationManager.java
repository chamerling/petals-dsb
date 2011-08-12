/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;

/**
 * Register all the internal notification aware components in the notification
 * center
 * 
 * @author chamerling
 * 
 */
@FractalComponent
// @Provides(interfaces = { @Interface(name = "service", signature =
// enclosing_type) })
public class InternalNotificationManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "scanner", signature = NotificationConsumerScanner.class)
    protected NotificationConsumerScanner scanner;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /**
     * Register all the components in the notification center. Done when all is
     * ready...
     */
    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void register() {
        List<NotificationTargetBean> beans = this.scanner.scan();
        for (NotificationTargetBean notificationTargetBean : beans) {
            log.info(String.format(
                    "Registering a notification subscriber in the notification center : %s",
                    notificationTargetBean.toString()));
        }

    }

}
