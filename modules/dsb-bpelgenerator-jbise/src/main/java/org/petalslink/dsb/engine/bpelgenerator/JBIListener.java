/**
 * 
 */
package org.petalslink.dsb.engine.bpelgenerator;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;
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
        try {
            String in = exchange.getInMessageProperty(INFOLDER) != null ? exchange
                    .getInMessageProperty(INFOLDER).toString() : "";
            String out = exchange.getInMessageProperty(OUTFOLDER) != null ? exchange
                    .getInMessageProperty(OUTFOLDER).toString() : "";

            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().fine("In folder " + in);
                getLogger().fine("Out folder " + out);
            }
            BPELGenerator generator = new BPELGenerator(new File(in), new File(out), "1.0",
                    new HashMap<String, String>(0));
            File result = generator.generate();

            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().fine("Generated file : " + result.getAbsolutePath());
            }

        } catch (Exception e) {
            if (getLogger().isLoggable(Level.WARNING)) {
                getLogger().log(Level.WARNING, "Got an error on generation", e);
            }
        }

        return true;
    }
}
