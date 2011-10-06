/**
 * 
 */
package org.petalslink.dsb.ukernel.binding;

import org.petalslink.dsb.kernel.api.service.Server;

/**
 * @author chamerling
 *
 */
public interface Engine extends Server {
    
    Router getRouter();

}
