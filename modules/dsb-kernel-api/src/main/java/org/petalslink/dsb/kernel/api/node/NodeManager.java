/**
 * 
 */
package org.petalslink.dsb.kernel.api.node;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 *
 */
@WebService
public interface NodeManager {
    
    @WebMethod
    void add(Node node) throws DSBException;
    
    @WebMethod
    Node get(String name) throws DSBException;
    
    @WebMethod
    Node remove(String name) throws DSBException;
    
    @WebMethod
    Node me();

}
