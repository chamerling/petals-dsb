/**
 * 
 */
package org.petalslink.dsb.cloud.registry;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ow2.petals.registry.api.Endpoint;

/**
 * @author chamerling
 * 
 */
@WebService
public interface SimpleRegistry {

    @WebMethod
    void put(Endpoint endpoint);

    @WebMethod
    List<Endpoint> get(String key);

}
