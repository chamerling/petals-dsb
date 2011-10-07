/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public interface NotificationConfiguration {
    
    URL getTopicSet();

    URL getTopicNamespaces();

    List<String> getSupportedTopics();

    QName getServiceName();

    QName getInterfaceName();

    String getEndpointName();

}
