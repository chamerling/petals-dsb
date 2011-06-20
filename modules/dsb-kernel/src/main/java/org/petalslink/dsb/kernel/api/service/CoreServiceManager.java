/**
 * 
 */
package org.petalslink.dsb.kernel.api.service;

/**
 * @author chamerling
 *
 */
public interface CoreServiceManager {
    
    <T> T getClient(Class<T> serviceClass, String container);
    
    <T> Server createService(Class<T> serviceClass, Object implementation, String container);

}
