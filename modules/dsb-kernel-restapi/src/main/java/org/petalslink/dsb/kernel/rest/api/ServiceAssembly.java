/**
 * 
 */
package org.petalslink.dsb.kernel.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author chamerling
 * 
 */
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
    @Path("/sa/{saId}/start")
    @Produces(JSON)
    Status start(@PathParam("saId") String saName);

    @GET
    @Path("/sa/{saId}/stop")
    @Produces(JSON)
    Status stop(@PathParam("saId") String saName);

    @GET
    @Path("/sa/{saId}/shutdown")
    @Produces(JSON)
    Status shutdown(@PathParam("saId") String saName);

    @GET
    @Path("/sa/{saId}/undeploy")
    @Produces(JSON)
    Status undeploy(@PathParam("saId") String saName);

    @GET
    @Path("/sa/{saId}/status")
    @Produces(JSON)
    Status status(@PathParam("saId") String saName);
}
