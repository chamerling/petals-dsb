/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.api.NotificationSender;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * This consumer service is a facade to the notification engine. It is just used
 * to forward internal kernel calls to interested parties...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = INotificationConsumer.class) })
public class NotificationConsumerServiceImpl implements INotificationConsumer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer#notify(
     * com.ebmwebsourcing
     * .wsstar.basenotification.datatypes.api.abstraction.Notify)
     */
    public void notify(Notify notify) throws WsnbException {
        if (log.isDebugEnabled()) {
            log.debug("Got a notify message at the kernel level!");
        }

        // Let see if the notification center is available...
        NotificationSender sender = NotificationCenter.get().getSender();
        if (sender == null) {
            log.warning("Can not find the notification sender");
            return;
        }

        try {
            sender.notify(notify);
        } catch (NotificationException e) {
            throw new WsnbException(e);
        }
    }

}
