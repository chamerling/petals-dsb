/**
 * 
 */
package org.petalslink.dsb.kernel.resources.service;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class ResourceIdBuilderTest extends TestCase {

    public static String id = String.format(ResourceIdBuilder.PATTERN, "component", "domain",
            "container");
    
    public static QName qname = new QName(id, "FooEndpoint");

    public void testGetComponent() throws Exception {
        String component = ResourceIdBuilder.getComponent(qname.toString());
        assertEquals("component", component);
    }

    public void testGetContainer() throws Exception {
        String container = ResourceIdBuilder.getContainer(qname.toString());
        assertEquals("container", container);
    }

    public void testGetDomain() throws Exception {
        String domain = ResourceIdBuilder.getDomain(qname.toString());
        assertEquals("domain", domain);
    }

}
