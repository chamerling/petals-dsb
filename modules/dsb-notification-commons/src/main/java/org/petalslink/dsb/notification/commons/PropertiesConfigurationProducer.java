/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.commons.api.ConfigurationProducer;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;

/**
 * Create subscribes from a configuration file.
 * 
 * @author chamerling
 * 
 */
public class PropertiesConfigurationProducer implements ConfigurationProducer {

    private Properties properties;

    /**
     * 
     */
    public PropertiesConfigurationProducer(Properties props) {
        this.properties = props;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.api.ConfigurationProducer#
     * getSubscribe()
     */
    public List<Subscribe> getSubscribe() {
        List<Subscribe> result = new ArrayList<Subscribe>();

        // get all the names
        if (properties == null) {
            return result;
        }

        Set<String> keys = getAllKeys(properties);
        for (String string : keys) {
            Subscribe subscribe = load(properties, string);
            if (subscribe != null) {
                result.add(subscribe);
            }
        }

        return result;
    }

    /**
     * @param properties2
     * @param string
     * @return
     */
    private Subscribe load(Properties properties, String key) {
        Map<String, String> map = new HashMap<String, String>();
        for (Object o : properties.keySet()) {
            String k = o.toString();
            if (k.startsWith(key + ".")) {
                map.put(k.substring(key.length() + 1), properties.getProperty(k));
            }
        }

        if (map.size() == 0) {
            return null;
        }

        return load(map);
    }

    /**
     * @param map
     * @return
     */
    private Subscribe load(Map<String, String> map) {
        Subscribe result = null;
        String url = map.get("subscriber");
        String topic = map.get("tropicName");
        String topicPrefix = map.get("topicPrefix");
        String topicURI = map.get("topicURI");

        if (url != null && topic != null && topicPrefix != null && topicURI != null) {
            QName topicName = new QName(topicURI, topic, topicPrefix);
            try {
                result = NotificationHelper.createSubscribe(url, topicName);
            } catch (NotificationException e) {
            }
        }
        return result;
    }

    /**
     * @param props
     * @return
     */
    public static Set<String> getAllKeys(Properties props) {
        Set<String> result = new HashSet<String>();
        if (props == null) {
            return result;
        }
        for (Object o : props.keySet()) {
            String key = o.toString();
            if (key.indexOf('.') > 0 && key.indexOf('.') != key.length()) {
                result.add(key.substring(0, key.indexOf('.')));
            }
        }
        return result;
    }

}
