/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import org.petalslink.gms.GMSListener;
import org.petalslink.gms.GMSMessage.Type;
import org.petalslink.gms.Peer;

/**
 * @author chamerling
 *
 */
public class CXFGMSServiceImpl implements GMSService {
    
    private GMSListener listener;

    public CXFGMSServiceImpl(GMSListener listener) {
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see org.petalslink.gms.proto.cxf.GMSService#receive(org.petalslink.gms.proto.cxf.GMSMessage)
     */
    public boolean receive(GMSMessage message) {
        org.petalslink.gms.GMSMessage gmsMessage = new org.petalslink.gms.GMSMessage();
        gmsMessage.setSource(new Peer(message.source));
        gmsMessage.setType(Type.valueOf(message.type));
        
        // TODO : Async
        this.listener.onMessage(gmsMessage);
        
        // TODO : Fixme...
        return true;
    }

}
