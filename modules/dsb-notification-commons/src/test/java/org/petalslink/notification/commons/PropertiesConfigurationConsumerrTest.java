/**
 * 
 */
package org.petalslink.notification.commons;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.petalslink.dsb.notification.commons.PropertiesConfigurationConsumer;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class PropertiesConfigurationConsumerrTest extends TestCase {

    public void testLoadFromFile() throws Exception {
        
        String prefix = "http://localhost:9000/services/RawService";
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 4; i++) {
            set.add(prefix + i);
        }
        
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());

        Properties props = new Properties();
        props.load(PropertiesConfigurationConsumerrTest.class
                .getResourceAsStream("/test_consumers.cfg"));
        
        PropertiesConfigurationConsumer consumer = new PropertiesConfigurationConsumer(props);

        Map<String, List<Subscribe>> subscribes = consumer.getSubscribes();
        assertEquals(2, subscribes.size());
        assertNotNull(subscribes.get("http://localhost:9000/services/Producer0"));
        assertNotNull(subscribes.get("http://localhost:9000/services/Producer1"));

        assertEquals(3, subscribes.get("http://localhost:9000/services/Producer0").size());
        assertEquals(1, subscribes.get("http://localhost:9000/services/Producer1").size());
        
        for (String string : subscribes.keySet()) {
            for (Subscribe subscribe : subscribes.get(string)) {
                set.remove(subscribe.getConsumerReference().getAddress().getValue().toString());
            }
        }
        assertTrue(set.size() == 0);
    }
    
    

}
