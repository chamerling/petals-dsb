/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.GetResourcePropertyResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourceProperties;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourcePropertiesResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class NotificationProducerRP implements INotificationProducerRP {

    private static Logger logger = Logger.getLogger(NotificationProducerRP.class.getName());

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
    public NotificationProducerRP(NotificationProducerEngine engine) {
        this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * getResourceProperty(javax.xml.namespace.QName)
     */
    public GetResourcePropertyResponse getResourceProperty(QName qname) throws WsnbException,
            AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("getResourceProperty call, qname is %s", qname));
        }
        return this.engine.getResourceProperty(qname);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * updateResourceProperties
     * (com.ebmwebsourcing.wsstar.resourceproperties.datatypes
     * .api.abstraction.UpdateResourceProperties)
     */
    public UpdateResourcePropertiesResponse updateResourceProperties(
            UpdateResourceProperties updateResourceProperties) throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("updateResourceProperties call"));
        }
        return this.engine.updateResourceProperties(updateResourceProperties);
    }

}
