/**
 * 
 */
package org.petalslink.dsb.kernel.service;

import org.apache.cxf.endpoint.Server;

/**
 * @author chamerling
 *
 */
public interface CoreServiceManager {
    
    <T> T getClient(Class<T> serviceClass, String container);
    
    <T> Server createService(Class<T> serviceClass, Object implementation, String container);

}
