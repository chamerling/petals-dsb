/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal.sample;

import javax.xml.transform.TransformerException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.notification.Mode;
import org.petalslink.dsb.annotations.notification.Notify;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * @author chamerling
 * 
 */
@FractalComponent
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

    @Notify(topics = { "{http://www.petalslink.org/resources/event/1.0}dsb:DSBTopic" }, mode = Mode.WSN)
    public void interestedInNotificationButNotOnly(Document document) {
        System.out.println("I just received a WSN notification!");
        try {
            System.out.println(XMLHelper.createStringFromDOMDocument(document));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
