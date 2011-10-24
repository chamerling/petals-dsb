/**
 * 
 */
package org.petalslink.dsb.kernel.rest.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.petalslink.dsb.kernel.rest.api.beans.SA;
import org.petalslink.dsb.kernel.rest.api.beans.Status;

/**
 * Reach it at http://localhost:7600/rest/kernel/$PATH/$METHODPATH
 * 
 * @author chamerling
 * 
 */
@Path("jbi/sa")
public interface ServiceAssembly {
    
    static final String JSON = "application/json";

    /*
     * @POST
     * 
     * @Path("/sa/{saId}/deploy")
     * 
     * @Produces(JSON) Status deploy(@PathParam("saId") String saURL);
     */

    @GET
    @Path("{saId}/start")
    @Produces(JSON)
    Status start(@PathParam("saId") String saName);

    @GET
    @Path("{saId}/stop")
    @Produces(JSON)
    Status stop(@PathParam("saId") String saName);

    @GET
    @Path("{saId}/shutdown")
    @Produces(JSON)
    Status shutdown(@PathParam("saId") String saName);

    @GET
    @Path("{saId}/undeploy")
    @Produces(JSON)
    Status undeploy(@PathParam("saId") String saName);

    @GET
    @Path("{saId}/status")
    @Produces(JSON)
    Status status(@PathParam("saId") String saName);
    
    @GET
    @Path("all")
    @Produces(JSON)
    List<SA> all();
}
