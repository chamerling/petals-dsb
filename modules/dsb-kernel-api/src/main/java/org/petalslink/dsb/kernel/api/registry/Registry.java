/**
 * 
 */
package org.petalslink.dsb.kernel.api.registry;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling
 *
 */
@WebService
public interface Registry {
    
    @WebMethod
    String store(ServiceEndpoint serviceEndpoint);
    
    @WebMethod
    ServiceEndpoint remove(String key);
    
    @WebMethod
    List<ServiceEndpoint> lookup(Query query);

}
