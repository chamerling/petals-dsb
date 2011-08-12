/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.consumer;

import javax.xml.transform.TransformerException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.ow2.petals.util.XMLUtil;
import org.petalslink.dsb.annotations.notification.Notify;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
@FractalComponent
// @Provides(interfaces = { @Interface(name = "service", signature =
// enclosing_type) })
public class LoggerConsumer {

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

    @Notify(topics = { "log", "foo", "bar" })
    public void interestedInNotificationButNotOnly(Document document) {
        System.out.println("I just received a notification!");
        try {
            System.out.println(XMLUtil.parseToString(document));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
