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

    public void testGetWebServiceClass() throws Exception {
        Class<?> clazz = JAXWSHelper.getWebServiceClass(HelloService.class);
        assertEquals(HelloService.class, clazz);
        assertNotSame(FooService.class, clazz);
    }

    public void testGetWebServiceClassFromObject() throws Exception {
        Class<?> clazz = JAXWSHelper.getWebServiceClass(HelloServiceImpl.class);
        assertEquals(HelloService.class, clazz);
        assertNotSame(FooService.class, clazz);
    }

    public void testHasWebServiceAnno() throws Exception {
        assertTrue(JAXWSHelper.hasWebServiceAnnotation(HelloService.class));
        assertTrue(JAXWSHelper.hasWebServiceAnnotation(HelloServiceImpl.class));
        assertFalse(JAXWSHelper.hasWebServiceAnnotation(NotWebService.class));
    }

    public void testGetWebServiceName() throws Exception {
        assertEquals(HelloService.class.getSimpleName(),
                JAXWSHelper.getWebServiceName(HelloService.class));
        assertEquals("MyServiceName", JAXWSHelper.getWebServiceName(WithNameService.class));
    }

    public void testGetClient() {
        HelloService client = JAXWSHelper.getClient(url, HelloService.class);
        assertNotNull(client);
        // check call but no service is available
        try {
            client.sayHello(null);
            fail();
        } catch (Exception e) {
        }
    }

    public void testGetService() {
        Server server = JAXWSHelper.getService(url, HelloService.class, new HelloServiceImpl());
        assertNotNull(server);
        server.start();
        server.stop();
    }

    public void testGetServiceAndCallIt() {
        Server server = null;
        try {
            server = JAXWSHelper.getService(url, HelloService.class, new HelloServiceImpl());
            server.start();
            HelloService client = JAXWSHelper.getClient(url, HelloService.class);
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
