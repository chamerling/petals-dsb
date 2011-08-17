/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Renew;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.RenewResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.ISubscriptionManager;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class SubscriptionManager implements ISubscriptionManager {

    private static Logger logger = Logger.getLogger(SubscriptionManager.class.getName());

    private SubscriptionManagerEngine engine;

    /**
     * 
     */
    public SubscriptionManager(SubscriptionManagerEngine engine) {
        this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.ISubscriptionManager#renew(com
     * .ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Renew)
     */
    public RenewResponse renew(Renew renew) throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Got a renew request...");
        }
        return this.engine.renew(renew);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.ISubscriptionManager#unsubscribe
     * (com
     * .ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe
     * )
     */
    public UnsubscribeResponse unsubscribe(Unsubscribe unsubscribe) throws WsnbException,
            AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Got a unsubscribe request...");
        }
        return this.engine.unsubscribe(unsubscribe);
    }
}
