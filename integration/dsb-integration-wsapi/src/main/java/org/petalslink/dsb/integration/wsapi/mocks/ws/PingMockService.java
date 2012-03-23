/**
 * 
 */
package org.petalslink.dsb.integration.wsapi.mocks.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
@WebService
public interface PingMockService {
    
    @WebMethod
    String ping(String input);

}
