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

import org.petalslink.dsb.notification.commons.api.ConfigurationConsumer;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;

/**
 * @author chamerling
 *
 */
public class PropertiesConfigurationConsumer implements ConfigurationConsumer {
    
    private Properties properties;
    
    private PropertiesConfigurationProducer producer;
    
    public PropertiesConfigurationConsumer(Properties props) {
        this.properties = props;
        this.producer = new PropertiesConfigurationProducer(props);
    }

    public Map<String, List<Subscribe>> getSubscribes() {
        Map<String, List<Subscribe>> result = new HashMap<String, List<Subscribe>>();

        if (properties == null) {
            return result;
        }

        Set<String> keys = getAllKeys(properties);
        for (String string : keys) {
            List<Subscribe> subscribes = producer.loadList(properties, string);
            if (subscribes != null) {
                String endpoint = properties.getProperty(string + ".producerReference");
                if (result.get(endpoint) == null) {
                    result.put(endpoint, new ArrayList<Subscribe>());
                }
                result.get(endpoint).addAll(subscribes);
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
