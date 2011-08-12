/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;

/**
 * @author chamerling
 * 
 */
public class InternalNotificationConsumerWrapper implements InternalNotificationConsumer {

    private static final Logger logger = Logger.getLogger(InternalNotificationConsumerWrapper.class
            .getName());

    private NotificationTargetBean bean;

    /**
     * 
     */
    public InternalNotificationConsumerWrapper(NotificationTargetBean bean) {
        this.bean = bean;
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
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer#notify(
     * com.ebmwebsourcing
     * .wsstar.basenotification.datatypes.api.abstraction.Notify)
     */
    public void notify(Notify notify) throws WsnbException {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Got a notify message...");
        }
        
        System.out.println(notify);

        // get the inner document...
        // TODO
        Document document = null;
        if (bean != null && bean.target != null && bean.m != null) {
            Class<?>[] parameters = bean.m.getParameterTypes();
            Object[] args = null;
            if (parameters.length > 0) {
                args = new Object[parameters.length];
                int i = 0;
                for (Class<?> param : parameters) {
                    // TODO : TEst the class type and not the name which is totally false...
                    // TODO = test if the param is a subclass of Notify
                    /*if (param.getClass().getName().equals(Notify.class.getName())) {
                        args[i] = notify;
                    } else */
                    if (param.getClass().getName().equals(Document.class.getName())) {
                        // create the notify as document...
                        if (notify != null) {
                            document = RefinedWsnbFactory.getInstance().getWsnbWriter()
                                    .writeNotifyAsDOM(notify);
                        }
                        args[i] = document;
                    }
                    i++;
                }
            }
            try {
                bean.m.invoke(bean.target, args);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
