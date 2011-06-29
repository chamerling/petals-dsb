/**
 * 
 */
package org.petalslink.dsb.kernel.servicepoller;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ConfigurationLoaderTest extends TestCase {

    public void testLoadInputMessage() throws Exception {
        URL url = ConfigurationLoaderTest.class.getResource("/0.xml");
        File file = new File(url.toURI());
        File folder = file.getParentFile();
        Document in = ConfigurationLoader.loadInputMessage(folder, "0");
        assertNotNull(in);
    }

    public void testLoadMap() throws Exception {

        String cron = "* * * * *";
        String endpointr = "replyendpoint";
        QName itfr = QName.valueOf("itfr");
        QName operationr = QName.valueOf("opr");
        QName servicer = QName.valueOf("srvr");
        String endpointto = "toendpoint";
        QName itfto = QName.valueOf("itf2");
        QName operationto = QName.valueOf("op2");
        QName serviceto = QName.valueOf("srv2");

        Map<String, String> map = new HashMap<String, String>();
        map.put(ConfigurationLoader.CRON, cron);
        map.put(ConfigurationLoader.REPLYENDPOINT, endpointr);
        map.put(ConfigurationLoader.REPLYINTERFACE, itfr.toString());
        map.put(ConfigurationLoader.REPLYOPERATION, operationr.toString());
        map.put(ConfigurationLoader.REPLYSERVICE, servicer.toString());
        map.put(ConfigurationLoader.TOPOLLENDPOINT, endpointto);
        map.put(ConfigurationLoader.TOPOLLINTERFACE, itfto.toString());
        map.put(ConfigurationLoader.TOPOLLOPERATION, operationto.toString());
        map.put(ConfigurationLoader.TOPOLLSERVICE, serviceto.toString());

        Configuration config = ConfigurationLoader.load(map);

        assertEquals(config.cronExpression, cron);
        assertEquals(config.replyTo.getEndpointName(), endpointr);
        assertEquals(config.replyTo.getInterfaceName(), itfr);
        assertEquals(config.replyTo.getOperation(), operationr);
        assertEquals(config.replyTo.getServiceName(), servicer);
        assertEquals(config.toPoll.getEndpointName(), endpointto);
        assertEquals(config.toPoll.getInterfaceName(), itfto);
        assertEquals(config.toPoll.getOperation(), operationto);
        assertEquals(config.toPoll.getServiceName(), serviceto);
    }

    public void testGetAllKeys() throws Exception {
        Properties props = new Properties();
        props.put("0.foo", "");
        props.put("1.foo", "");
        props.put("1.bar", "");
        props.put("2.foobar", "");
        props.put("12345.foobar", "");

        Set<String> keys = ConfigurationLoader.getAllKeys(props);
        assertNotNull(keys);
        assertEquals(4, keys.size());

        assertTrue(keys.contains("0"));
        assertTrue(keys.contains("1"));
        assertTrue(keys.contains("2"));
        assertTrue(keys.contains("12345"));
    }

    public void testLoadFromFile() throws Exception {
        URL url = ConfigurationLoaderTest.class.getResource("/poller.cfg");
        assertNotNull(url);
        File configFile = new File(url.toURI());
        List<Configuration> config = ConfigurationLoader.load(configFile);
        assertNotNull(config);
        assertEquals(2, config.size());
        for (Configuration configuration : config) {
            System.out.println(configuration.toString());
        }
    }

}
