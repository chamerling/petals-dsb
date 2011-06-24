/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.bpel.api.BPELDeployer;
import org.petalslink.dsb.ws.bpel.api.BPELDescriptor;
import org.petalslink.dsb.ws.bpel.api.LinkedResourceDescriptor;

/**
 * A simple Web service client based on CXF
 * 
 * @author chamerling
 * 
 */
public class BPELDeployerClient implements BPELDeployer,
        org.petalslink.dsb.ws.bpel.client.BPELDeployer {
    
    private static Log logger = LogFactory.getLog(BPELDeployerClient.class.getName());

    private String baseAddress;

    private BPELDeployer clientProxy;

    /**
     * 
     */
    public BPELDeployerClient(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public boolean deploy(BPELDescriptor bpelDescriptor, LinkedResourceDescriptor[] resources)
            throws DSBWebServiceException {
        logger.debug("Calling Web service");
        return getClientProxy().deploy(bpelDescriptor, resources);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.ws.bpel.client.BPELDeployer#deploy(java.io.File,
     * java.io.File[])
     */
    public boolean deploy(File bpelFile, File[] resources) throws DSBWebServiceException {
        if (bpelFile == null) {
            throw new DSBWebServiceException("BPEL file can not be null");
        }

        if (!bpelFile.exists() || !bpelFile.isFile()) {
            throw new DSBWebServiceException("BPEL file %s does not exists or is not a file",
                    bpelFile.getName());
        }

        BPELDescriptor descriptor = new BPELDescriptor();
        descriptor.setFileName(bpelFile.getName());
        descriptor.setAttachment(new DataHandler(new FileDataSource(bpelFile)));

        List<LinkedResourceDescriptor> list = new ArrayList<LinkedResourceDescriptor>();
        if (resources != null) {
            for (File file : resources) {
                if (file != null && file.exists() && file.isFile()) {
                    LinkedResourceDescriptor resource = new LinkedResourceDescriptor();
                    resource.setFileName(file.getName());
                    resource.setWSDL(new DataHandler(new FileDataSource(bpelFile)));
                    list.add(resource);
                } else {
                    // warning
                }
            }
        }
        return deploy(descriptor, list.toArray(new LinkedResourceDescriptor[list.size()]));
    }

    /**
     * @return the clientProxy
     */
    private synchronized BPELDeployer getClientProxy() {
        if (clientProxy != null) {
            return clientProxy;
        }
        this.clientProxy = CXFHelper.getClient(baseAddress, BPELDeployer.class);
        return clientProxy;
    }

}
