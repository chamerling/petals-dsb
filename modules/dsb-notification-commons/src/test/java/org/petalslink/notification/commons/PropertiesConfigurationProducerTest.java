/**
 * 
 */
package org.petalslink.notification.commons;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.petalslink.dsb.notification.commons.PropertiesConfigurationProducer;

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
public class PropertiesConfigurationProducerTest extends TestCase {

    public void testGetURLs() throws Exception {
        String csv = "http://foo/bar, http://bar/foo";
        List<String> urls = PropertiesConfigurationProducer.getURLs(csv);

        assertEquals(2, urls.size());

        for (String string : urls) {
            assertTrue(string.equals("http://foo/bar") || string.equals("http://bar/foo"));
        }
        System.out.println(urls);
    }

    public void testGetBadSeparatorEnd() throws Exception {
        String csv = "http://foo/bar,";

        List<String> urls = PropertiesConfigurationProducer.getURLs(csv);

        assertEquals(1, urls.size());

        for (String string : urls) {
            assertTrue(string.equals("http://foo/bar"));
        }
        System.out.println(urls);
    }

    public void testGetBadSeparatorBegin() throws Exception {
        String csv = ",http://foo/bar";

        List<String> urls = PropertiesConfigurationProducer.getURLs(csv);

        assertEquals(1, urls.size());

        for (String string : urls) {
            assertTrue(string.equals("http://foo/bar"));
        }
        System.out.println(urls);
    }

    public void testGetNoData() throws Exception {
        String csv = "";
        List<String> urls = PropertiesConfigurationProducer.getURLs(csv);
        assertEquals(0, urls.size());
    }

    public void testGetNoDataJustComma() throws Exception {
        String csv = ",";
        List<String> urls = PropertiesConfigurationProducer.getURLs(csv);
        assertEquals(0, urls.size());
    }

    public void testFromFile() throws Exception {
        String prefix = "http://localhost:9000/services/RawService";
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 7; i++) {
            System.out.println(i);
            set.add(prefix + i);
        }

        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());

        Properties props = new Properties();
        InputStream is = PropertiesConfigurationProducerTest.class
                .getResourceAsStream("/test_subscribers.cfg");
        props.load(is);
        PropertiesConfigurationProducer producer = new PropertiesConfigurationProducer(props);
        List<Subscribe> list = producer.getSubscribes();

        System.out.println(list.get(0).getConsumerReference().getAddress().toString());

        assertEquals(7, list.size());

        for (int i = 0; i < list.size(); i++) {
            Subscribe subscribe = list.get(i);
            String address = subscribe.getConsumerReference().getAddress().getValue().toString();
            set.remove(address);
        }
        assertTrue(set.size() == 0);
    }
}
