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
        this.client = new HTTPNotificationProducerRPClient(endpoint);
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
                System.out.println(element);
            }
        } catch (Exception e) {
            throw new NotificationException(e);
        }
        return result;
    }

}
