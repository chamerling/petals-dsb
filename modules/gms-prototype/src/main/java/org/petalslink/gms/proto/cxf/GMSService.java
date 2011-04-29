/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * The annotated interface to be exposed by CXF
 * 
 * @author chamerling
 *
 */
@WebService
public interface GMSService {
    
    @WebMethod
    boolean receive(GMSMessage message);

}
