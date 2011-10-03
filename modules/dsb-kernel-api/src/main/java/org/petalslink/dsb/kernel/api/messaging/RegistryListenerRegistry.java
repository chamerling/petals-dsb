/**
 * 
 */
package org.petalslink.dsb.kernel.api.messaging;

import java.util.List;

/**
 * Retrive all the annotated registry listeners from the framework
 * 
 * @author chamerling
 * 
 */
public interface RegistryListenerRegistry {

    List<RegistryListener> getListeners();

}
