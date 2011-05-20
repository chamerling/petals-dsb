/**
 * 
 */
package org.petalslink.dsb.ws.bpel.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * Deploy BPEL description files into the DSB. The files will be packaged in the
 * right format on the DSB side. It is up to the implementation to deploy
 * thinigs on the right nodes.
 * 
 * @author chamerling
 * 
 */
@WebService
public interface BPELDeployer {

    @WebMethod(operationName = "deploy")
    boolean deploy(@WebParam(name = "bpelDescriptor") BPELDescriptor bpelDescriptor,
            @WebParam(name = "partners") PartnerLinkDescriptor[] partners)
            throws PEtALSWebServiceException;

}
