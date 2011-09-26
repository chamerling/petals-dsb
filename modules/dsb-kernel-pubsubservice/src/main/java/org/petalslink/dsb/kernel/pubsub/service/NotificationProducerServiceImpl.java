/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.notification.commons.api.NotificationManager;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * This notification producer service is just a facade to the real notification
 * engine.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = INotificationProducer.class) })
public class NotificationProducerServiceImpl implements INotificationProducer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "notification-manager", signature = NotificationManager.class)
    private NotificationManager notificationManager;

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
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#
     * getCurrentMessage
     * (com.ebmwebsourcing.wsstar.basenotification.datatypes.api
     * .abstraction.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage getCurrentMessage)
            throws WsnbException, AbsWSStarFault {
        if (log.isDebugEnabled()) {
            log.debug("Got a getCurrentMessage message");
        }
        return getINotificationProducer().getCurrentMessage(getCurrentMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#subscribe
     * (com
     * .ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe
     * )
     */
    public SubscribeResponse subscribe(Subscribe subscribe) throws WsnbException, AbsWSStarFault {
        if (log.isDebugEnabled()) {
            log.debug("Got a subscribe message");
        }
        return getINotificationProducer().subscribe(subscribe);
    }

    private synchronized INotificationProducer getINotificationProducer() {
        return notificationManager.getNotificationProducerEngine();
    }

}
