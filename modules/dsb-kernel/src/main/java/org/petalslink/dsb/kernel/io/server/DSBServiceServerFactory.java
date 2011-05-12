/**
 * 
 */
package org.petalslink.dsb.kernel.io.server;


/**
 * @author chamerling
 *
 */
public interface DSBServiceServerFactory {
    
    /**
     * Get a service server which is able to receive messages from the lower layers
     * 
     * @return
     */
    DSBServiceServer getServiceServer();

}
