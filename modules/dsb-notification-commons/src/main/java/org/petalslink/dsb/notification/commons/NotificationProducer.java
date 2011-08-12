/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * The notification producer implementation just forward incoming messages
 * coming from {@link #getCurrentMessage(GetCurrentMessage)} and
 * {@link #subscribe(Subscribe)} to the notification engine.
 * 
 * @author chamerling
 * 
 */
public class NotificationProducer implements INotificationProducer {

    private static Logger logger = Logger.getLogger(NotificationProducer.class.getName());

    private NotificationProducerEngine engine;

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    /**
	 * 
	 */
    public NotificationProducer(NotificationProducerEngine engine) {
        this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#
     * getCurrentMessage
     * (com.ebmwebsourcing.wsstar.basenotification.datatypes.api
     * .abstraction.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage currentMessage)
            throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("getCurrentMessage call");
        }
        return this.engine.getCurrentMessage(currentMessage);
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
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("subscribe call");
        }
        return this.engine.subscribe(subscribe);
    }

}
