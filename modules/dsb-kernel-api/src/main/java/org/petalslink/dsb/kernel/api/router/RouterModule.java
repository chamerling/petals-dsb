/**
 * 
 */
package org.petalslink.dsb.kernel.api.router;

import org.petalslink.dsb.api.MessageExchange;

/**
 * A router module. Everything can be done in such module since modules are
 * linked together in a linked list to provide a very extensible routing
 * mechanism.
 * 
 * @author chamerling
 * 
 */
public interface RouterModule {

    /**
     * 
     * @param message
     */
    void route(MessageExchange message);

    /**
     * A unique name in the modules collection
     * 
     * @return
     */
    String getName();

}
