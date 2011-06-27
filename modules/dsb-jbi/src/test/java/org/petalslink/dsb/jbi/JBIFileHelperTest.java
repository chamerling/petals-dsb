/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

/**
 * @author chamerling
 *
 */
public class JBIFileHelperTest extends TestCase {
    
    public void testReadDescriptor() throws Exception {
        URL url = JBIFileHelperTest.class.getResource("/SA.zip");
        File f = new File(url.toURI());
        Jbi descriptor = JBIFileHelper.readDescriptor(f);
        assertNotNull(descriptor);
    }
    
    public void testUnknowSA() throws Exception {
        Jbi descriptor = JBIFileHelper.readDescriptor(new File("foo.zip"));
        assertNull(descriptor);
    }

}
