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
import java.util.StringTokenizer;

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
     * getSubscribes()
     */
    public List<Subscribe> getSubscribes() {
        List<Subscribe> result = new ArrayList<Subscribe>();
        if (this.properties == null) {
            return result;
        }

        Set<String> keys = getAllKeys(properties);
        for (String string : keys) {
            List<Subscribe> subscribes = loadList(properties, string);
            if (subscribes != null) {
                result.addAll(subscribes);
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

    public List<Subscribe> loadList(Properties properties, String key) {
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

        return loadList(map);
    }

    /**
     * @param map
     * @return
     */
    private Subscribe load(Map<String, String> map) {
        Subscribe result = null;
        String url = map.get("consumerReference");
        String topic = map.get("topicName");
        String topicPrefix = map.get("topicPrefix");
        String topicURI = map.get("topicURI");

        if (url != null && topic != null && topicPrefix != null && topicURI != null) {
            QName topicName = new QName(topicURI, topic, topicPrefix);
            try {
                result = NotificationHelper.createSubscribe(url, topicName);
            } catch (NotificationException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private List<Subscribe> loadList(Map<String, String> map) {
        List<Subscribe> result = new ArrayList<Subscribe>();
        String url = map.get("consumerReference");
        String topic = map.get("topicName");
        String topicPrefix = map.get("topicPrefix");
        String topicURI = map.get("topicURI");

        if (url != null && topic != null && topicPrefix != null && topicURI != null) {
            QName topicName = new QName(topicURI, topic, topicPrefix);

            List<String> urls = getURLs(url);
            for (String finalURL : urls) {
                try {
                    result.add(NotificationHelper.createSubscribe(finalURL, topicName));
                } catch (NotificationException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static List<String> getURLs(String csv) {
        List<String> result = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(csv, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();
            if (token.length() > 0) {
                result.add(token);
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

    public String getProperty(String key, String name) {
        return properties.getProperty(key + "." + name);
    }

}
