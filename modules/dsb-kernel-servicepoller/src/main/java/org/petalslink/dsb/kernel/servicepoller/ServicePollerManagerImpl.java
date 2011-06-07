/**
 * 
 */
package org.petalslink.dsb.kernel.servicepoller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.util.LoggingUtil;
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

    private Map<Key, List<String>> cache = new HashMap<Key, List<String>>();

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(logger);
        this.log.start();
        this.cache = new HashMap<ServicePollerManagerImpl.Key, List<String>>();
        System.out.println("######!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.start();
        System.out.println("######!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void start(ServicePollerInformation toPoll, Document inputMessage,
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

        Jbi descriptor = null;
        //JBIFileHelper.readDescriptor(saToDeploy);
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
        Key key = new Key(toPoll, replyTo);
        List<String> list = null;
        if (cache.get(key) == null) {
            list = new ArrayList<String>();
            cache.put(key, list);
        }
        list = cache.get(key);
        list.add(saName);
    }

    public void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo)
            throws ServicePollerException {
        if (log.isDebugEnabled()) {
            this.log.debug("Got a stop request for toPoll service = " + toPoll);
            this.log.debug("ReployTo is set to : " + replyTo);
        }
        
        Key key = new Key(toPoll, replyTo);
        if (cache.get(key) != null) {
            // which one???
            List<String> list = this.cache.get(key);
            // ...
        }

    }

    class Key {
        ServicePollerInformation to;

        ServicePollerInformation reply;

        public Key(ServicePollerInformation to, ServicePollerInformation reply) {
            super();
            this.to = to;
            this.reply = reply;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((reply == null) ? 0 : reply.hashCode());
            result = prime * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (reply == null) {
                if (other.reply != null) {
                    return false;
                }
            } else if (!reply.equals(other.reply)) {
                return false;
            }
            if (to == null) {
                if (other.to != null) {
                    return false;
                }
            } else if (!to.equals(other.to)) {
                return false;
            }
            return true;
        }

        private ServicePollerManagerImpl getOuterType() {
            return ServicePollerManagerImpl.this;
        }
    }

}
