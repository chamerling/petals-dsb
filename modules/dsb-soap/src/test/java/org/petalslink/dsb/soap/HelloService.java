/**
 * 
 */
package org.petalslink.dsb.soap;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
@WebService(targetNamespace="http://api.ws.dsb.petalslink.org/")
public interface HelloService {

    @WebMethod
    String sayHello();
}
