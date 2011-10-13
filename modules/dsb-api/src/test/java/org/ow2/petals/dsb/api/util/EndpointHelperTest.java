/**
 * 
 */
package org.ow2.petals.dsb.api.util;

import java.net.URI;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.petalslink.dsb.api.util.EndpointHelper;

/**
 * @author chamerling
 * 
 */
public class EndpointHelperTest extends TestCase {

    public void testGetEndpoint() throws Exception {
        URI se = new URI("dsb://foo/bar/Service@endpoint");
        assertEquals("endpoint", EndpointHelper.getEndpoint(se));
    }

    public void testGetService() throws Exception {
        URI se = new URI("dsb://foo/bar/Service@endpoint");
        QName expected = new QName("http://foo/bar/", "Service");
        assertEquals(expected, EndpointHelper.getService(se));
    }

}
