package org.petalslink.dsb.kernel.service;

import javax.xml.namespace.QName;

import org.petalslink.dsb.kernel.service.EndpointHelper;

import junit.framework.TestCase;

public class EndpointHelperTest extends TestCase {
    
    public void testGetWithoutPrefix() throws Exception {
        String url = "dsb://localhost/EndpointName";
        String without = EndpointHelper.getInstance().getWithoutPrefix(url);
        assertEquals("localhost/EndpointName", without);
    }
    
    public void testGetHostLocalhost() throws Exception {
        String url = "dsb://localhost/EndpointName";
        String host = EndpointHelper.getInstance().getHost(url);
        assertEquals("localhost", host);
    }
    
    public void testGetHostAll() throws Exception {
        String url = "dsb://ServiceName:EndpointName";
        String host = EndpointHelper.getInstance().getHost(url);
        assertNull(host);
    }
    
    public void testGetEndpointName() throws Exception {
        String url = "dsb://localhost/EndpointName";
        String endpoint = EndpointHelper.getInstance().getEndpoint(url);
        assertEquals("EndpointName", endpoint);
    }

}
