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

/**
 * Use this service to send notification to notification subscribers. This
 * service embeds the core engine which will get all the subscriptions. This
 * service is an internal one to be used by other DSB kernel services through
 * the NotificationCenter.
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
        // set the notification sender in the notification center
        this.log = new LoggingUtil(this.logger);
        NotificationCenter.get().setNotifificationSender(this);
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
        System.out.println("Got a notify request, sending the message to the core engine...");
    }

}
