/**
 * 
 */
package org.petalslink.dsb.kernel.servicepoller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.management.deployment.AtomicDeploymentService;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.tools.generator.poller2jbi.Poller2Jbi;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServicePoller.class) })
public class ServicePollerManagerImpl implements ServicePoller {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, PollerInformation> cache = new HashMap<String, PollerInformation>();

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(logger);
        this.log.start();
        this.cache = new HashMap<String, PollerInformation>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.start();
    }

    public String start(ServicePollerInformation toPoll, Document inputMessage,
            String cronExpression, ServicePollerInformation replyTo) throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a start request for toPoll service = " + toPoll);
            this.log.debug("Cron expression is : " + cronExpression);
            this.log.debug("ReployTo is set to : " + replyTo);
        }

        if (toPoll == null) {
            throw new ServicePollerException("Service to poll can not be null");
        }

        // create the JBI artefacts and deploy them
        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put(Constants.COMPONENT_VERSION, "1.0");
        Poller2Jbi generator = new Poller2Jbi(toPoll.getEndpointName(), toPoll.getInterfaceName(),
                toPoll.getServiceName(), toPoll.getOperation(), inputMessage,
                replyTo != null ? replyTo.getEndpointName() : null,
                replyTo != null ? replyTo.getInterfaceName() : null,
                replyTo != null ? replyTo.getServiceName() : null,
                replyTo != null ? replyTo.getOperation() : null, cronExpression, extensions);
        File saToDeploy = null;
        try {
            saToDeploy = generator.generate();
        } catch (JBIGenerationException e) {
            log.warning(e.getMessage());
            throw new ServicePollerException(e);
        }

        Jbi descriptor = readDescriptor(saToDeploy);
        if (descriptor == null) {
            throw new ServicePollerException("Can not get the JBI descriptor from generated SA...");
        }
        String saName = descriptor.getServiceAssembly().getIdentification().getName();

        if (saName == null) {
            // it means that we will not be able to start the SA...
            throw new ServicePollerException(
                    "Can not get the JBI service assembly name from generated SA");
        }

        boolean success;
        try {
            success = this.deploymentService.deploy(saToDeploy.toURI().toURL());
            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to deploy the Service Assembly located at '"
                        + saToDeploy.toURI().toURL());
                throw new ServicePollerException("Deployment failure");
            }
        } catch (Exception e) {
            throw new ServicePollerException(e.getMessage());
        }

        try {
            success = this.deploymentService.start(saName);
            // FIXME : Need some update on the petals JMX side...
            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to start the Service Assembly '" + saName + "'");
                throw new ServicePollerException("Start failure, the SA can not be started");
            }
        } catch (Exception e) {
            // undeploy
            // FIXME !!! This is not available in the atomic service!
            // this.deploymentService.forceUndeploy(saName);
            throw new ServicePollerException(e.getMessage());
        }

        // all done, cache it...
        PollerInformation info = new PollerInformation();
        info.cronExpression = cronExpression;
        info.inputMessage = inputMessage;
        info.replyTo = replyTo;
        info.toPoll = toPoll;
        this.cache.put(saName, info);
        
        return saName;
    }

    /**
     * @param saToDeploy
     * @return
     */
    private Jbi readDescriptor(File saToDeploy) {
        if (saToDeploy == null) {
            return null;
        }
            
        Jbi result = null;
        try {
            final ZipFile zipFile = new ZipFile(saToDeploy);
            final ZipEntry jbiDescriptorZipEntry = zipFile.getEntry("META-INF/jbi.xml");
            final InputStream jbiDescriptorInputStream = zipFile
                    .getInputStream(jbiDescriptorZipEntry);

            // Load the JBI descriptor
            result = JBIDescriptorBuilder.buildJavaJBIDescriptor(jbiDescriptorInputStream);

        } catch (JBIDescriptorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo)
            throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a stop request for toPoll service = " + toPoll);
            this.log.debug("ReployTo is set to : " + replyTo);
        }

        throw new ServicePollerException("This method is deprecated");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePoller#stop(java.lang.String)
     */
    public boolean stop(String id) throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a stop request for ID = " + id);
        }
        boolean result = false;
        try {
            result = deploymentService.stop(id);
            result = deploymentService.shutdown(id);
            result = deploymentService.undeploy(id);
        } catch (PetalsException e) {
            throw new ServicePollerException("Can not stop poller " + id, e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePoller#pause(java.lang.String)
     */
    public boolean pause(String id) throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a pause request for ID = " + id);
        }
        boolean result = false;
        try {
            result = deploymentService.stop(id);
        } catch (PetalsException e) {
            throw new ServicePollerException("Can not pause poller " + id, e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePoller#resume(java.lang.String)
     */
    public boolean resume(String id) throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a resume request for ID = " + id);
        }
        boolean result = false;
        try {
            result = deploymentService.start(id);
        } catch (PetalsException e) {
            throw new ServicePollerException("Can not resume poller " + id, e);
        }
        return result;
    }
    
    
    class PollerInformation {
        ServicePollerInformation toPoll; Document inputMessage;
        String cronExpression; ServicePollerInformation replyTo;
    }

}
