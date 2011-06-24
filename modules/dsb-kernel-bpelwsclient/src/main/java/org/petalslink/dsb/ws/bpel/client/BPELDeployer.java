/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import java.io.File;

import org.petalslink.dsb.ws.api.DSBWebServiceException;

/**
 * Deployer API based on File
 * 
 * @author chamerling
 * 
 */
public interface BPELDeployer {

    /**
     * 
     * @param bpelFile
     *            the source BPEL file, may not be null
     * @param resources
     *            linked resources wuch as WSDL, XSD... May be null
     * @return
     * @throws PEtALSWebServiceException
     */
    boolean deploy(File bpelFile, File[] resources) throws DSBWebServiceException;

}
