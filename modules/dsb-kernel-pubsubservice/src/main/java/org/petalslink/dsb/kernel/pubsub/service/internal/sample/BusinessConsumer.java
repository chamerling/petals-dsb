/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal.sample;

import javax.xml.transform.TransformerException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.petalslink.dsb.annotations.notification.Mode;
import org.petalslink.dsb.annotations.notification.Notify;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * @author chamerling
 * 
 */
@FractalComponent
public class BusinessConsumer {

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @Notify(topics = { "{http://www.petalslink.org/resources/event/1.0}dsb:DSBTopic" }, mode = Mode.PAYLOAD)
    public void interestedInNotificationButNotOnly(Document document) {
        System.out.println("I just received a payload notification!");
        try {
            System.out.println("####\n" + XMLHelper.createStringFromDOMDocument(document) + " \n####");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
