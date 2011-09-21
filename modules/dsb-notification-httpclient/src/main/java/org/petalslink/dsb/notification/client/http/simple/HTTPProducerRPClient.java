/**
 * 
 */
package org.petalslink.dsb.notification.client.http.simple;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.HTTPNotificationProducerRPClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.api.client.simple.RPClient;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.topics.datatypes.api.WstopConstants;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.refinedabstraction.RefinedWstopFactory;

/**
 * @author chamerling
 * 
 */
public class HTTPProducerRPClient implements RPClient {

    private String endpoint;

    private HTTPNotificationProducerRPClient client;

    /**
     * 
     */
    public HTTPProducerRPClient(String endpoint) {
        this.endpoint = endpoint;
        this.client = new HTTPNotificationProducerRPClient(this.endpoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.api.client.simple.RPClient#getTopics
     * ()
     */
    public List<QName> getTopics() throws NotificationException {
        List<QName> result = new ArrayList<QName>();
        try {
            com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.GetResourcePropertyResponse response = this.client
                    .getResourceProperty(WstopConstants.TOPIC_SET_QNAME);
            List<Element> elements = response.getPropertyValue();
            for (Element element : elements) {
                if (element.getNodeName().contains("TopicSet")) {
                    com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType topicSet = RefinedWstopFactory
                            .getInstance().getWstopReader()
                            .readTopicSetType(element.getOwnerDocument());
                    for (Element e : topicSet.getTopicsTrees()) {
                        String nodeName = e.getNodeName();
                        String prefix = null;
                        if (nodeName.contains(":")) {
                            prefix = nodeName.substring(0, nodeName.lastIndexOf(':'));
                            nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
                        }
                        String ns = e.getAttribute("xmlns:"+prefix);
                        result.add(new QName(ns, nodeName, prefix));
                    }
                }
            }
        } catch (Exception e) {
            throw new NotificationException(e);
        }
        return result;
    }
}
