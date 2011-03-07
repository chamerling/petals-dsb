package org.petalslink.dsb.kernel.gms.client;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * A client to notify others that the current node joined or leaved the group...
 * 
 * @author chamerling
 * 
 */
@WebService
public interface GMSClient {
    
    @WebMethod
    void join();
    
    @WebMethod
    void leave();

}
