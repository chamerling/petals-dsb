/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.management.binder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
import org.ow2.petals.kernel.api.service.ServiceEndpoint;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.tools.generator.jbi.ws2jbi.Constants;
import org.ow2.petals.tools.generator.jbi2rest.Jbi2REST;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.util.JBIFileHelper;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceExposer.class) })
public class RESTServiceExposerImpl implements ServiceExposer {

    private static final String WORK_DIR = "work";

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "binder-checker", signature = BinderChecker.class)
    private BinderChecker binderChecker;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private File workPath;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");

        this.workPath = new File(this.configurationService.getContainerConfiguration()
                .getWorkDirectoryPath(), WORK_DIR);
        if (!this.workPath.exists()) {
            this.workPath.mkdirs();
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void expose(ServiceEndpoint endpoint) throws BinderException {
        if (!this.binderChecker.canExposeOnProtocol(this.getProtocol())) {
            throw new BinderException("No component found to expose REST service");
        }

        File sa = null;
        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put(Constants.OUTPUT_DIR, this.workPath.getAbsolutePath());
        QName itf = null;
        if ((endpoint.getInterfacesName() != null) && (endpoint.getInterfacesName().size() > 0)) {
            itf = endpoint.getInterfacesName().get(0);
        }
        QName service = null;
        if (endpoint.getServiceName() != null) {
            service = endpoint.getServiceName();
        }

        extensions.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                org.petalslink.dsb.kernel.Constants.DEFAULT_REST_COMPONENT_VERSION);

        // FIXME : Inject endpoint properties!!!!

        String endpointName = endpoint.getEndpointName();
        if (endpointName
                .startsWith(org.petalslink.dsb.kernel.Constants.REST_PLATFORM_ENDPOINT_PREFIX)) {
            endpointName = endpointName.substring(
                    org.petalslink.dsb.kernel.Constants.REST_PLATFORM_ENDPOINT_PREFIX.length(),
                    endpointName.length());
        }

        extensions.put(
                org.ow2.petals.tools.generator.jbi.restcommons.Constants.REST_ENDPOINT_ADDRESS,
                endpointName);

        Jbi2REST generator = new Jbi2REST(endpoint.getEndpointName(), service, itf, extensions);
        try {
            sa = generator.generate();
        } catch (JBIGenerationException e) {
            throw new BinderException(e.getMessage());
        }

        Jbi descriptor = JBIFileHelper.readDescriptor(sa);
        if (descriptor == null) {
            throw new BinderException("Can not get the JBI descriptor...");
        }
        String saName = descriptor.getServiceAssembly().getIdentification().getName();

        if (saName == null) {
            // it means that we will not be able to start the SA...
            throw new BinderException("Can not get the JBI service assembly name");
        }

        try {

            boolean success = this.deploymentService.deploy(sa.toURL());

            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to deploy the Service Assembly located at '" + sa.toURL()
                        + "'");
                throw new PEtALSWebServiceException("Deployment failure");
            }
        } catch (Exception e) {
            throw new BinderException(e.getMessage());
        }

        // Start the service assembly
        try {
            boolean success = this.deploymentService.start(saName);
            if (success) {
                // Extract the service assembly name
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to start the Service Assembly '" + saName + "'");
                throw new PEtALSWebServiceException("Start failure");
            }
        } catch (Exception e) {
            // FIXME
            // this.deploymentService.forceUndeploy(saName);
            throw new BinderException(e.getMessage());
        }

        // delete the generated file
        if (sa != null) {
            sa.delete();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getProtocol() {
        return org.petalslink.dsb.kernel.Constants.REST_SERVICE_EXPOSER;
    }
}
