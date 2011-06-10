/**
 * 
 */
package org.petalslink.dsb.cxf;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class JAXWSHelperTest extends TestCase {

    String url = "http://localhost:6756/foo/bar";

    public void testGetClient() {
        HelloService client = CXFHelper.getClient(url, HelloService.class);
        assertNotNull(client);
        // check call but no service is available
        try {
            client.sayHello(null);
            fail();
        } catch (Exception e) {
        }
    }

    public void testGetService() {
        Server server = CXFHelper.getService(url, HelloService.class, new HelloServiceImpl());
        assertNotNull(server);
        server.start();
        server.stop();
    }

    public void testGetServiceAndCallIt() {
        Server server = null;
        try {
            server = CXFHelper.getService(url, HelloService.class, new HelloServiceImpl());
            server.start();
            HelloService client = CXFHelper.getClient(url, HelloService.class);
            String in = "IN";
            String out = client.sayHello(in);
            assertEquals(in, out);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
