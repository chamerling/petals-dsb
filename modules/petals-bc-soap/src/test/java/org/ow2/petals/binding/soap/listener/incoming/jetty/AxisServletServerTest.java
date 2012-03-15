
package org.ow2.petals.binding.soap.listener.incoming.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;

public class AxisServletServerTest {

    private static final String WELCOME_SERVLET_URL = "http://localhost:9080" + "/";

    private AxisServletServer server;

    @Before
    public void setUp() throws Exception {
        ConfigurationContext configContext = ConfigurationContextFactory
                .createDefaultConfigurationContext();
        final Logger logger = Logger.getLogger(AxisServletServer.class.getName());
        SoapServerConfig config = new SoapServerConfig(logger, null, 9080);
        this.server = new AxisServletServer(logger, config, configContext);
    }

    @Test
    public void testStart() throws Exception {
        this.server.start();

        URL url = new URL(WELCOME_SERVLET_URL);
        URLConnection connection = url.openConnection();
        Map<String, List<String>> fields = connection.getHeaderFields();
        List<String> statusList = fields.get(null);
        String status = statusList.get(0);
        assertEquals("HTTP/1.1 200 OK", status);
    }

    @Test
    public void testStop() throws Exception {
        this.server.start();
        this.server.stop();

        URL url = new URL(WELCOME_SERVLET_URL);

        InputStream is = null;
        try {
            is = url.openStream();
            fail();
        } catch (ConnectException ce) {
            assertTrue(true);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        if (this.server.isRunning()) {
            this.server.stop();
        }
    }

}
