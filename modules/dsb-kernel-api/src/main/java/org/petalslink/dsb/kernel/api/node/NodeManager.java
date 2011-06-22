/**
 * 
 */
package org.petalslink.dsb.kernel.api.node;

import java.util.List;

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
    
    /**
     * Get a node from its name
     * 
     * @param name
     * @return
     * @throws DSBException
     */
    @WebMethod
    Node get(String name) throws DSBException;
    
    @WebMethod
    Node remove(String name) throws DSBException;
    
    @WebMethod
    Node me();
    
    /**
     * Get all the registered nodes
     * @return
     */
    @WebMethod
    List<Node> get();

}
