/**
 * 
 */
package org.ow2.petals.component.framework.listeners;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

/**
 * @author chamerling
 * 
 */
public class EmptyJBIListener extends AbstractJBIListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.listener.AbstractJBIListener#onJBIMessage
     * (org.ow2.petals.component.framework.api.message.Exchange)
     */
    @Override
    public boolean onJBIMessage(Exchange exchange) {
        this.component.getLogger().warning("This component does not process any incoming message!");
        return false;
    }

}
