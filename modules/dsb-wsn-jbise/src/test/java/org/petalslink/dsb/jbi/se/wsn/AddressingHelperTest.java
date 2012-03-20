/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn;

import java.net.URI;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class AddressingHelperTest extends TestCase {

    public void testIsExternalService() throws Exception {
        URI address = URI.create(Constants.DSB_EXTERNAL_SERVICE_NS + "/foo/bar");
        assertTrue(AddressingHelper.isExternalService(address));
    }

    public void testIsExternalServiceNull() throws Exception {
        assertFalse(AddressingHelper.isExternalService(null));
    }

    public void testIsInternalService() throws Exception {
        URI address = URI.create(Constants.DSB_INTERNAL_SERVICE_NS + "/foo/bar");
        assertTrue(AddressingHelper.isInternalService(address));
    }

    public void testIsInternalServiceNull() throws Exception {
        assertFalse(AddressingHelper.isInternalService(null));
    }

    public void testAddLocation() throws Exception {
        String str = "http://foo/bar::localhost";
        URI uri = AddressingHelper.addLocation(URI.create(str), "component", "container", "domain");
        assertEquals(
                "http://foo/bar?component=component&container=container&domain=domain::localhost",
                uri.toString());

        str = "http://foo/bar";
        uri = AddressingHelper.addLocation(URI.create(str), "component", "container", "domain");
        assertEquals("http://foo/bar?component=component&container=container&domain=domain",
                uri.toString());
    }

    public void testGetQuery() throws Exception {
        String query = AddressingHelper
                .getQuery(URI
                        .create("http://foo/bar?component=component&container=container&domain=domain::localhost"));
        assertEquals("component=component&container=container&domain=domain", query);

        query = AddressingHelper.getQuery(URI
                .create("http://foo/bar?component=component&container=container&domain=domain"));
        assertEquals("component=component&container=container&domain=domain", query);

        query = AddressingHelper.getQuery(URI.create("http://foo/bar"));
        assertNull(query);
    }

    public void testGetComponent() throws Exception {
        URI uri = URI.create("http://foo/bar?component=c&container=cc&domain=d::localhost");
        assertEquals(AddressingHelper.getComponent(uri), "c");
        assertEquals(AddressingHelper.getContainer(uri), "cc");
        assertEquals(AddressingHelper.getDomain(uri), "d");
    }
    
    public void testGetService() throws Exception {
        URI uri = URI.create("http://foo/bar?component=c&container=cc&domain=d::Service@Endpoint");
        assertEquals("Service", AddressingHelper.getServiceName(uri));
    }
    
    public void testGetEndpoint() throws Exception {
        URI uri = URI.create("http://foo/bar?component=c&container=cc&domain=d::Service@Endpoint");
        assertEquals("Endpoint", AddressingHelper.getEndpointName(uri));
    }
    
    public void testGetInitialAddress() throws Exception {
        URI uri = URI.create("http://foo/bar?component=c&container=cc&domain=d::http://localhost:9998/service/root");
        assertEquals("http://localhost:9998/service/root", AddressingHelper.getInitialAddress(uri));
    }
}
