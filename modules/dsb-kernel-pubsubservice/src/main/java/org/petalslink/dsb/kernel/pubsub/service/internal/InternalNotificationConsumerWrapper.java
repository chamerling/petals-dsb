/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.annotations.notification.Mode;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * A wrapper used so that we use the interface for the local subscriber...
 * 
 * @author chamerling
 * 
 */
public class InternalNotificationConsumerWrapper implements InternalNotificationConsumer {

    private static final Logger logger = Logger.getLogger(InternalNotificationConsumerWrapper.class
            .getName());

    private NotificationTargetBean bean;

    private QName topic;

    /**
     * 
     */
    public InternalNotificationConsumerWrapper(NotificationTargetBean bean, QName topic) {
        this.bean = bean;
        this.topic = topic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.internal.
     * InternalNotificationConsumer#getTarget()
     */
    public NotificationTargetBean getTarget() {
        return bean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.internal.
     * InternalNotificationConsumer#getTopic()
     */
    public QName getTopic() {
        return this.topic;
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
        if (logger.isLoggable(Level.FINE)) {
            logger.info("Got a notify message...");
        }

        if (bean != null && bean.target != null && bean.m != null) {
            Class<?>[] parameters = bean.m.getParameterTypes();
            Object[] args = new Object[1];
            if (parameters.length == 1) {
                // hope that the argument is really a document, must be checked!
                if (bean.mode == Mode.WSN) {
                    args[0] = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                } else if (bean.mode == Mode.PAYLOAD) {
                    // get the business message...
                    if (notify.getNotificationMessage() == null
                            || notify.getNotificationMessage().size() > 1) {
                        throw new WsnbException("Bad number of notification messages!");
                    }
                    Element element = notify.getNotificationMessage().get(0).getMessage().getAny();
                    if (element != null) {
                        args[0] = element.getOwnerDocument();
                    }
                }
            } else {
                // we do not support more than one parameter!
                System.out.println("Multiple parameters are not supported!");
            }
            try {
                bean.m.invoke(bean.target, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
