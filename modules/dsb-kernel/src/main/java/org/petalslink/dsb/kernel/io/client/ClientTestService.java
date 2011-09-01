/**
 * 
 */
package org.petalslink.dsb.kernel.io.client;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
@WebService
public interface ClientTestService {

    /**
     * 
     * @param times
     * @return
     */
    @WebMethod
    boolean invoke(int times);    
}
