/**
 * 
 */
package org.petalslink.dsb.kernel.bpel;

import java.io.File;
import java.util.Map;

import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.petalslink.dsb.tools.generator.bpel.BPELGenerator;

/**
 * @author chamerling
 * 
 */
public class BPELDelegate {

    private ClassLoader cl;

    /**
     * 
     */
    public BPELDelegate(ClassLoader cl) {
        this.cl = cl;
        System.out.println("Delegate");
    }

    File generate(File inputFolder, File outputFolder, String componentVersion,
            Map<String, String> extensions) {
        System.out.println("Generate");

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.cl);
        File result = null;
        try {
            System.out.println("Generate");
            try {
                BPELGenerator generator = new BPELGenerator(inputFolder, outputFolder,
                        componentVersion, extensions);

                result = generator.generate();
            } catch (JBIGenerationException e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
                if (t.getCause() != null) {
                    System.out.println("CAUSE : ");
                    t.getCause().printStackTrace();
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }

        return result;
    }

}
