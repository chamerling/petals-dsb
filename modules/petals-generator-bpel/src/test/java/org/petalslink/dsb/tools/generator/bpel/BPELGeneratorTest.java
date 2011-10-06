/**
 * 
 */
package org.petalslink.dsb.tools.generator.bpel;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

/**
 * @author chamerling
 * 
 */
public class BPELGeneratorTest extends TestCase {

    public void testGenerateFromGoodFiles() throws Exception {
        URL url = UtilsTest.class.getResource("/army");
        File inputFolder = new File(url.toURI());
        System.out.println(inputFolder);
        File f = new File("test");
        File input = new File(f, UUID.randomUUID().toString());
        System.out.println(input);
        
        // copy input files
        FileUtils.copyDirectory(inputFolder, input, true);
        System.out.println("Let's work from : " + input.getAbsolutePath());
        File output = new File(input, "output");
        
        Map<String, String> extensions = new HashMap<String, String>();
        
        BPELGenerator gen = new BPELGenerator(inputFolder, output, "1.0", extensions);
        File out = gen.generate();
        System.out.println("Output generated in " + out);
        
        
        //FileUtils.deleteDirectory(input);

    }
    
    public void testGenerateFactorial() throws Exception {
        URL url = UtilsTest.class.getResource("/factorial");
        File inputFolder = new File(url.toURI());
        System.out.println(inputFolder);
        File f = new File("test");
        File input = new File(f, UUID.randomUUID().toString());
        System.out.println(input);
        
        // copy input files
        FileUtils.copyDirectory(inputFolder, input, true);
        System.out.println("Let's work from : " + input.getAbsolutePath());
        File output = new File(input, "output");
        
        Map<String, String> extensions = new HashMap<String, String>();
        
        BPELGenerator gen = new BPELGenerator(inputFolder, output, "1.0", extensions);
        File out = gen.generate();
        System.out.println("Output generated in " + out);
        
        
        //FileUtils.deleteDirectory(input);

    }

}
