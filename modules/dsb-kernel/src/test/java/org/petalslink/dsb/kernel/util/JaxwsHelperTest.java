/**
 * 
 */
package org.petalslink.dsb.kernel.util;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.petalslink.dsb.ws.api.HelloService;

/**
 * @author chamerling
 * 
 */
public class JaxwsHelperTest extends TestCase {

    public void testGetInterfaceNameFromInterface() throws Exception {
        QName portTypeName = JaxwsHelper.getInterfaceName(HelloService.class);
        assertEquals("HelloService", portTypeName.getLocalPart());
    }
    
    public void testGetServiceNameFromInterface() throws Exception {
        QName serviceName = JaxwsHelper.getServiceName(HelloService.class);
        assertEquals("HelloServiceService", serviceName.getLocalPart());
    }
    
    public void testGetEndpointNameFromInterface() throws Exception {
        QName endpointName = JaxwsHelper.getEndpointName(HelloService.class);
        assertEquals("HelloServicePort", endpointName.getLocalPart());
    }
}
