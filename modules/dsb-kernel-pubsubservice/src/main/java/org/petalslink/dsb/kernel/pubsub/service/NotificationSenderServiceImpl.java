/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;

/**
 * Note : This is just a service if some need to use it with fractal. If not
 * just get it from the Notification Center.
 * 
 * Use this service to send notification to notification subscribers from any
 * kernel service. This service embeds the core engine which will get all the
 * subscriptions. This service is an internal one to be used by other DSB kernel
 * services through the NotificationCenter.<br>
 * Usage :<br>
 * <code>
 * NotificationCenter.get().getSender();
 * </code>
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationSender.class) })
public class NotificationSenderServiceImpl implements NotificationSender {

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
     * org.petalslink.dsb.notification.commons.NotificationSender#notify(org
     * .w3c.dom.Document, javax.xml.namespace.QName, java.lang.String)
     */
    public void notify(Document payload, QName topic, String dialect) throws NotificationException {
        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "Sending a notification message to topic '%s' with dialect '%s'",
                    topic.toString(), dialect));
        }
        NotificationSender sender = NotificationCenter.get().getSender();
        if (sender == null) {
            throw new NotificationException(
                    "Can not find the sender from the notification center, please check your configuration");
        }
        sender.notify(payload, topic, dialect);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.api.NotificationSender#notify
     * (com.
     * ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify)
     */
    public void notify(Notify notify) throws NotificationException {
        if (log.isDebugEnabled()) {
            log.debug(String
                    .format("Got a notify request, sending the message to the core engine..."));
        }
        NotificationSender sender = NotificationCenter.get().getSender();
        if (sender == null) {
            throw new NotificationException(
                    "Can not find the sender from the notification center, please check your configuration");
        }
        sender.notify(notify);
    }
}
