/**
 * 
 */
package org.petalslink.dsb.kernel;

/**
 * @author chamerling
 *
 */
public interface ServiceFinder {
    
    final String DEFAULT_SERVICE_NAME = "service";
    
    <T> T get(Class<T> t) throws DSBException;
    
    <T> T get(Class<T> t, String componentName) throws DSBException;
    
    <T> T get(Class<T> t, String componentName, String serviceName) throws DSBException;

}
