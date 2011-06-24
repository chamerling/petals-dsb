/**
 * 
 */
package org.petalslink.dsb.tools.generator.bpel;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class UtilsTest extends TestCase {

    public void testGetWSDLs() throws Exception {
        URL url = UtilsTest.class.getResource("/army");
        File folder = new File(url.toURI());
        File[] files = Utils.getWSDLFiles(folder);
        assertNotNull(files);
        assertEquals(5, files.length);
    }

    public void testGetXSDs() throws Exception {
        URL url = UtilsTest.class.getResource("/army");
        File folder = new File(url.toURI());
        File[] files = Utils.getXSDFiles(folder);
        assertNotNull(files);
        assertEquals(0, files.length);
    }

    public void testGetBPEL() throws Exception {
        URL url = UtilsTest.class.getResource("/army");
        File folder = new File(url.toURI());
        File[] files = Utils.getBPELFiles(folder);
        assertNotNull(files);
        assertEquals(1, files.length);
    }

    public void testGetFromEmptyFolder() throws Exception {
        URL url = UtilsTest.class.getResource("/empty");
        File folder = new File(url.toURI());
        File[] files = Utils.getBPELFiles(folder);
        assertNotNull(files);
        assertEquals(0, files.length);
    }
    
    public void testGetFromNotAFolder() throws Exception {
        URL url = UtilsTest.class.getResource("/foo/bar.txt");
        File folder = new File(url.toURI());
        File[] files = Utils.getBPELFiles(folder);
        assertNull(files);
    }

}
