/**
 * 
 */
package org.petalslink.dsb.kernel.management.generator.bpel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.management.deployment.AtomicDeploymentService;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.GeneratorConstants;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.jbi.JBIFileHelper;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.bpel.api.BPELDeployer;
import org.petalslink.dsb.ws.bpel.api.BPELDescriptor;
import org.petalslink.dsb.ws.bpel.api.LinkedResourceDescriptor;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = BPELDeployer.class) })
public class BPELDeployerImpl implements BPELDeployer {

    public static final String WORK_DIR = "bpel-generator";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    private File workPath;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        System.out.println("START");
        this.log = new LoggingUtil(this.logger);
        this.workPath = new File(this.configurationService.getContainerConfiguration()
                .getWorkDirectoryPath(), WORK_DIR);
        if (!this.workPath.exists()) {
            this.workPath.mkdirs();
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        if (this.workPath.exists()) {
            this.workPath.delete();
        }
    }

    /**
     * @param inputFolder
     * @param bpelDescriptor
     * @param resources
     */
    private void storeFiles(File inputFolder, BPELDescriptor bpelDescriptor,
            LinkedResourceDescriptor[] resources) {
        File bpel = new File(inputFolder, bpelDescriptor.getFileName());
        storeFile(bpel, bpelDescriptor.getAttachment());

        if (resources != null) {
            for (LinkedResourceDescriptor linkedResourceDescriptor : resources) {
                File resourceFile = new File(inputFolder, linkedResourceDescriptor.getFileName());
                storeFile(resourceFile, linkedResourceDescriptor.getResource());
            }
        }
    }

    /**
     * @param resourceFile
     * @param wsdl
     */
    private void storeFile(File writeTo, DataHandler dh) {
        if (writeTo != null) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(writeTo);
                dh.writeTo(fos);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    /**
     * @return
     */
    private File getOutputFolder() {
        File output = new File(workPath, UUID.randomUUID().toString() + "-output");
        if (!output.exists())
            output.mkdirs();

        return output;
    }

    /**
     * @return
     */
    private File getNewWorkingFolder() {
        File working = new File(workPath, UUID.randomUUID().toString());
        working.mkdirs();
        return working;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.bpel.api.BPELDeployer#deploy(org.petalslink.dsb
     * .ws.bpel.api.BPELDescriptor,
     * org.petalslink.dsb.ws.bpel.api.BPELDescriptor,
     * org.petalslink.dsb.ws.bpel.api.LinkedResourceDescriptor[])
     */
    public boolean deploy(BPELDescriptor bpelDescriptor, LinkedResourceDescriptor[] resources)
            throws DSBWebServiceException {
        File sa = null;
        File inputFolder = null;
        try {
            inputFolder = getNewWorkingFolder();
            File outputFolder = getOutputFolder();
            storeFiles(inputFolder, bpelDescriptor, resources);

            ServiceEndpoint service = new ServiceEndpoint();
            String ns = String.format(GeneratorConstants.NS_TEMPLATE, "bpel");
            service.setEndpointName(GeneratorConstants.ENDPOINT_NAME);
            service.setServiceName(new QName(ns, GeneratorConstants.SERVICE_NAME));
            service.setInterfaces(new QName[] { new QName(ns, GeneratorConstants.INTERFACE_NAME) });
            Client client = ClientFactoryRegistry.getFactory().getClient(service);
            Message message = new MessageImpl();
            message.setEndpoint(GeneratorConstants.ENDPOINT_NAME);
            message.setInterface(new QName(ns, GeneratorConstants.INTERFACE_NAME));
            message.setService(new QName(ns, GeneratorConstants.SERVICE_NAME));

            message.setProperty("bpel.generator.in", inputFolder.getAbsolutePath());
            message.setProperty("bpel.generator.out", outputFolder.getAbsolutePath());

            client.sendReceive(message);

            // get the SA in the output folder
            File[] files = outputFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    System.out.println("Got file = " + name);
                    return name.endsWith("zip");
                }
            });

            if (files != null && files.length > 0) {
                sa = files[0];
            }

            if (sa == null) {
                throw new DSBWebServiceException("Unable to generate Service Assembly");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DSBWebServiceException(e.getMessage());
        } finally {
            if (inputFolder != null) {
                inputFolder.delete();
            }
        }

        // let's call the installation services...
        Jbi descriptor = JBIFileHelper.readDescriptor(sa);
        if (descriptor == null) {
            throw new DSBWebServiceException("Can not get the JBI descriptor from generated SA...");
        }
        String saName = descriptor.getServiceAssembly().getIdentification().getName();
        System.out.println("SA name : " + saName);

        if (saName == null) {
            // it means that we will not be able to start the SA...
            throw new DSBWebServiceException(
                    "Can not get the JBI service assembly name from generated SA");
        }

        try {
            boolean success = this.deploymentService.deploy(sa.toURI().toURL());
            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to deploy the Service Assembly located at '" + sa.toURI());
                throw new PEtALSWebServiceException("Deployment failure");
            }
        } catch (Exception e) {
            throw new DSBWebServiceException(e);
        }

        // Start the service assembly
        try {
            boolean success = this.deploymentService.start(saName);
            // FIXME : Need some update on the petals JMX side...
            if (success) {
                this.log.info("Service assembly '" + saName + "' has been started");
            } else {
                this.log.warning("Failed to start the Service Assembly '" + saName + "'");
                throw new PEtALSWebServiceException("Start failure, the SA can not be started");
            }
        } catch (Exception e) {
            // undeploy
            // FIXME !!! This is not available in the atomic service!
            // this.deploymentService.forceUndeploy(saName);
            throw new DSBWebServiceException(e.getMessage());
        } finally {

        }

        if (sa != null) {
            // sa.delete();
        }
        return true;
    }

}
