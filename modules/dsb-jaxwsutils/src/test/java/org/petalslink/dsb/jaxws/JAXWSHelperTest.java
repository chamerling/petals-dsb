/**
 * 
 */
package org.petalslink.dsb.jaxws;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class JAXWSHelperTest extends TestCase {
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
}
