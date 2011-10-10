/**
 * 
 */
package org.petalslink.dsb.kernel.resources.service;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.ow2.petals.util.XMLUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.pubsub.service.NotificationCenter;
import org.petalslink.dsb.kernel.resources.service.utils.SOAException;
import org.petalslink.dsb.kernel.resources.service.utils.SOAJAXBContext;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;

import com.petalslink.easyresources.execution_environment_connection_model.ResourceIdentifier;

/**
 * This registry listener publish notification to the notification engine so
 * that interested parties can be notified on new endpoint activation.
 * 
 * @author chamerling
 * 
 */
@org.petalslink.dsb.annotations.registry.RegistryListener
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = org.petalslink.dsb.kernel.api.messaging.RegistryListener.class) })
public class RegistryListener implements org.petalslink.dsb.kernel.api.messaging.RegistryListener {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    public static final QName topicUsed = new QName(
            "http://www.petalslink.org/resources/event/1.0", "CreationResourcesTopic", "tns");

    public static final String dialect = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Concrete";

    private SOAJAXBContext context;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        try {
            context = SOAJAXBContext.getInstance();
            context.addOtherObjectFactory(com.petalslink.easyresources.execution_environment_connection_model.ObjectFactory.class);
        } catch (SOAException e) {
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onRegister(org
     * .petalslink.dsb.api.ServiceEndpoint)
     */
    public void onRegister(ServiceEndpoint endpoint) throws DSBException {
        if (log.isDebugEnabled()) {
            log.debug("A new endpoint has been registered, send a notification to the engine");
        }

        ResourceIdentifier rid = new ResourceIdentifier();
        rid.setId(ResourceIdBuilder.getId(endpoint));
        rid.setResourceType("endpoint");

        Document payload;
        try {
            payload = SOAJAXBContext.getInstance().unmarshallAnyElement(rid);
        } catch (SOAException e) {
            throw new DSBException(e);
        }

        if (log.isDebugEnabled()) {
            try {
                log.debug(XMLUtil.createStringFromDOMDocument(payload));
            } catch (TransformerException e) {
            }
        }

        NotificationCenter notificationCenter = NotificationCenter.get();
        if (notificationCenter == null) {
            throw new DSBException(
                    "Can not get the notification center to send new resource notification...");
        }

        NotificationSender sender = notificationCenter.getSender();
        if (sender == null) {
            throw new DSBException(
                    "Can not get the notification sender from the notification center...");
        }

        try {
            sender.notify(payload, topicUsed, dialect);
        } catch (NotificationException e) {
            throw new DSBException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onUnregister
     * (org.petalslink.dsb.api.ServiceEndpoint)
     */
    public void onUnregister(ServiceEndpoint endpoint) throws DSBException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListener#getName()
     */
    public String getName() {
        return "Theregistrylistenerforexecenv";
    }

}
