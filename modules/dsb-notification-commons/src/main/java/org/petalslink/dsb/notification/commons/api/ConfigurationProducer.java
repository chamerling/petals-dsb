/**
 * 
 */
package org.petalslink.dsb.notification.commons.api;

import java.util.Map;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;

/**
 * Create a list of subscribe from some configuration tools. Can be used to
 * automatically subscribe to topics by components.
 * 
 * @author chamerling
 * 
 */
public interface ConfigurationProducer {

    Map<String, Subscribe> getSubscribe();

}
