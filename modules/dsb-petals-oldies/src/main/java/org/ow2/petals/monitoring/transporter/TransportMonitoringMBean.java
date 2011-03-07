
package org.ow2.petals.monitoring.transporter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This MBean gives information about transporters provided by the Petals
 * container. This information includes transporter queue size monitoring and
 * transporter listing.
 * 
 * @author ofabre - eBM Websourcing
 * 
 */
public interface TransportMonitoringMBean {

    /**
     * Returns the queue size (number of pending message in the queue) of all
     * installed components for the given transporter protocol
     * 
     * @param transporterName
     *            the transporter protocol name
     * @return a {@link Map} containing the queue size of all installed
     *         components for the given transporter protocol. Key is a component
     *         name and value is the queue size for this component. Queue sizes
     *         are positive integers
     * @throws Exception
     *             if an error occurs during queue size retrieval
     */
    Map<String, Integer> getCurrentQueueSizes(String transporterName) throws Exception;

    /**
     * Returns the max queue size (maximal number of messages in the queue) of
     * component queues for the given transporter protocol
     * 
     * @param transporterName
     *            the transporter protocol name
     * @return an integer value containing the max queue size of component
     *         queues for the given transporter protocol. Value is a positive
     *         integer or -1 if there's no maximum
     * @throws Exception
     *             if an error occurs during max queue size retrieval
     */
    int getMaxQueueSize(String transporterName) throws Exception;

    /**
     * Returns the list of all transporter names provided by the Petals platform
     * 
     * @return a {@link Set} of all available transporter names provided by the Petals
     *         platform
     */
    List<String> getTransporters();

}
