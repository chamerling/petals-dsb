/**
 * 
 */
package org.petalslink.dsb.integration.wsapi.mocks.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Does nothing, just to have an unused service...
 * 
 * @author chamerling
 *
 */
@WebService
public interface ResourceCreationTestService {

    @WebMethod
    void foo();
}
