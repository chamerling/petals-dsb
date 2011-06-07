package org.petalslink.dsb.component.poller;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

/**
 * 
 * @author chamerling
 *
 */
public class JBIListener extends AbstractJBIListener {

    @Override
    public boolean onJBIMessage(Exchange exchange) {
        this.component.getLogger().warning("This component does not process any incoming message");
        return false;
    }

}
