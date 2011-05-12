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
     * @param endpointName
     * @return
     */
    @WebMethod
    boolean invoke(String endpointName);    
}
