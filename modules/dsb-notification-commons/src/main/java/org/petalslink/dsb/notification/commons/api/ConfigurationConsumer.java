/**
 * 
 */
package org.petalslink.dsb.notification.commons.api;

import java.util.List;
import java.util.Map;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;

/**
 * @author chamerling
 * 
 */
public interface ConfigurationConsumer {

    /**
     * Get a list of subscribes to send per entry. The key is the endpoint to
     * send the subscribe to.
     * 
     * @return
     */
    Map<String, List<Subscribe>> getSubscribes();

}
