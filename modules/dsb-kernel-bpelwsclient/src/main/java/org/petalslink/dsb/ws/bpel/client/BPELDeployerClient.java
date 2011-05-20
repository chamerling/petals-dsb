/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.petalslink.dsb.ws.bpel.api.BPELDeployer;
import org.petalslink.dsb.ws.bpel.api.BPELDescriptor;
import org.petalslink.dsb.ws.bpel.api.PartnerLinkDescriptor;

/**
 * A simple Web service client based on CXF
 * 
 * @author chamerling
 * 
 */
public class BPELDeployerClient implements BPELDeployer {


    public boolean deploy(BPELDescriptor bpelDescriptor, PartnerLinkDescriptor[] partners)
            throws PEtALSWebServiceException {
        // TODO Auto-generated method stub
        return false;
    }

}
