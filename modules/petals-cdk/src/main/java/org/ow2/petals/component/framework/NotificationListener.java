/**
 * 
 */
package org.ow2.petals.component.framework;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

/**
 * Reintroduce the notification listener which handle all the WSN related messages
 * 
 * @author chamerling
 *
 */
public abstract class NotificationListener extends AbstractJBIListener {

    
//    @Override
//    public boolean onJBIMessage(Exchange exchange) {
//        return false;
//    }
    
    /**
     * TODO
     * 
     * @param exchange
     * @return
     */
    public abstract boolean onNotificationMessage(Exchange exchange);

}
