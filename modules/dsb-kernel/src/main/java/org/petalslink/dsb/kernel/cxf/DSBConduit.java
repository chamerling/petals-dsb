/**
 * 
 */
package org.petalslink.dsb.kernel.cxf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.AbstractConduit;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

/**
 * The Conduit is the layer between the client and the service and is in charge
 * of being able to send messages and to receive reponses to/from client/service cf <a
 * href="http://cxf.apache.org/custom-cxf-transport.html">http://cxf.apache.org/
 * custom-cxf-transport.html</a>
 * 
 * @author chamerling
 * 
 */
public class DSBConduit extends AbstractConduit {
    private static final Logger LOG = LogUtils.getL7dLogger(DSBConduit.class);
    
    private Bus bus;
    
    public DSBConduit(EndpointReferenceType target) {
        this(null, target);
    }

    public DSBConduit(Bus b, EndpointReferenceType target) {
        super(target);
        bus = b;
    }

    public Bus getBus() {
        return bus;
    }

    protected Logger getLogger() {
        return LOG;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cxf.transport.Conduit#prepare(org.apache.cxf.message.Message)
     */
    public void prepare(Message message) throws IOException {
        // Preparing the message is just creating sort of a fake output stream
        // which will be called by CXF when the message has to be sent.
        getLogger().log(Level.FINE, "DSBConduit send message");
        message.setContent(OutputStream.class, new DSBConduitOutputStream(message, target, this));
    }
}
