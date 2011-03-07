package org.petalslink.dsb.kernel.gms;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Listeners needs to be registered into the {@link GMSManager}
 * 
 * @author chamerling
 *
 */
@WebService
public interface GMSListener {
    
    @WebMethod
    void hasJoined(GMSContext context) throws GMSException;
    
    @WebMethod
    void hasLeaved(GMSContext context) throws GMSException;

}
