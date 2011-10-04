/**
 * 
 */
package org.petalslink.dsb.kernel.resources.service;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class ResourceIdBuilderTest extends TestCase {

    public static String id = String.format(ResourceIdBuilder.PATTERN, "component", "domain",
            "container");

    public void testGetComponent() throws Exception {
        String component = ResourceIdBuilder.getComponent(id);
        assertEquals("component", component);
    }

    public void testGetContainer() throws Exception {
        String container = ResourceIdBuilder.getContainer(id);
        assertEquals("container", container);
    }

    public void testGetDomain() throws Exception {
        String domain = ResourceIdBuilder.getDomain(id);
        assertEquals("domain", domain);
    }

}
