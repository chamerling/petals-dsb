/**
 * 
 */
package org.petalslink.dsb.kernel.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.petalslink.dsb.kernel.rest.api.beans.Status;

/**
 * @author chamerling
 *
 */
@Path("test")
public interface TestService {
    
    static final String JSON = "application/json";
    
    @GET
    @Path("foo")
    @Produces(JSON)
    Status foo();
    
    @GET
    @Path("bar")
    Status bar();

}
