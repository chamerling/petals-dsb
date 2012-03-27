/**
 * 
 */
package org.ow2.petals.component.framework.listeners;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

/**
 * Reintroduce the notification listener which handle all the WSN related
 * messages
 * 
 * @author chamerling
 * 
 */
public abstract class NotificationListener extends AbstractJBIListener {

    public static final String WS_BASE_NOTIFICATION_WSDL_NAMESPACE_URI = "http://docs.oasis-open.org/wsn/bw-2";

    @Override
    public boolean onJBIMessage(Exchange exchange) {
        if (isBaseNotification(exchange)) {
            return onNotificationMessage(exchange);
        } else {
            // we are just listening to notifications, reaise exception here...
            exchange.setError(new Exception(
                    "The notification listener can only handle notifications"));
        }
        return true;
    }

    /**
     * TODO
     * 
     * @param exchange
     * @return
     */
    public abstract boolean onNotificationMessage(Exchange exchange);

    private boolean isBaseNotification(final Exchange exchange) {
        if (exchange == null || exchange.getInterfaceName() == null
                || exchange.getInterfaceName().getNamespaceURI() == null) {
            return false;
        }
        return WS_BASE_NOTIFICATION_WSDL_NAMESPACE_URI.equals(exchange.getInterfaceName()
                .getNamespaceURI());
    }

}
