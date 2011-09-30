/**
 * 
 */
package org.petalslink.dsb.engine.bpelgenerator;

import java.io.File;
import java.util.HashMap;

import javax.jbi.messaging.MessagingException;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.petalslink.dsb.tools.generator.bpel.BPELGenerator;

/**
 * @author chamerling
 * 
 */
public class JBIListener extends AbstractJBIListener {

    public static final String INFOLDER = "bpel.generator.in";

    public static final String OUTFOLDER = "bpel.generator.out";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.listener.AbstractJBIListener#onJBIMessage
     * (org.ow2.petals.component.framework.api.message.Exchange)
     */
    @Override
    public boolean onJBIMessage(Exchange exchange) {
        System.out.println("INput!");
        // get the input and output folders, then generate
        try {
            String in = exchange.getInMessageProperty(INFOLDER) != null ? exchange
                    .getInMessageProperty(INFOLDER).toString() : "";
            String out = exchange.getInMessageProperty(OUTFOLDER) != null ? exchange
                    .getInMessageProperty(OUTFOLDER).toString() : "";

            System.out.println("In : " + in);
            System.out.println("Out : " + out);

            System.out.println("1");
            BPELGenerator generator = new BPELGenerator(new File(in), new File(out), "1.0",
                    new HashMap<String, String>(0));
            System.out.println("2");
            File result = generator.generate();
            System.out.println("Result " + result.getAbsolutePath());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (JBIGenerationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
