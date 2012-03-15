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
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.tools.generator.jbi.ws2jbi.Constants;
import org.ow2.petals.tools.generator.jbi2ws.Jbi2WS;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.jbi.JBIFileHelper;
import org.petalslink.dsb.kernel.api.management.binder.BinderChecker;
import org.petalslink.dsb.kernel.api.management.binder.BinderException;
import org.petalslink.dsb.kernel.api.management.binder.ServiceExposer;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceExposer.class) })
public class SOAPServiceExposerImpl implements ServiceExposer {

    private static final String WORK_DIR = "work";

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "binder-checker", signature = BinderChecker.class)
    private BinderChecker binderChecker;

    private File workPath;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
    public void expose(org.petalslink.dsb.ws.api.ServiceEndpoint endpoint) throws BinderException {
        if (!this.binderChecker.canExposeOnProtocol(this.getProtocol())) {
            throw new BinderException("No component found to expose SOAP service");
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "Trying to expose service wich is defined as EP = %s, SRV=%s and ITF = %s",
                    endpoint.getEndpoint(), endpoint.getService().toString(), endpoint.getItf()
                            .toString()));
        }

        File sa = null;
        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put(Constants.OUTPUT_DIR, this.workPath.getAbsolutePath());

        QName itf = endpoint.getItf();
        QName service = endpoint.getService();

        extensions.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                org.petalslink.dsb.kernel.Constants.DEFAULT_SOAP_COMPONENT_VERSION);

        String soapServiceName = endpoint.getEndpoint();
        if (soapServiceName == null && itf != null) {
            // get the interface which is the only field which should not be
            // null
            soapServiceName = itf.getLocalPart();
        }

        if (soapServiceName == null) {
            throw new BinderException(
                    "Can not find a valid name to create service name from given endpoint and interface");
        }

        if (soapServiceName
                .startsWith(org.petalslink.dsb.kernel.Constants.SOAP_PLATFORM_ENDPOINT_PREFIX)) {
            soapServiceName = soapServiceName.substring(
                    org.petalslink.dsb.kernel.Constants.SOAP_PLATFORM_ENDPOINT_PREFIX.length(),
                    soapServiceName.length());
        }

        extensions.put(
                org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_ADDRESS,
                soapServiceName);

        // FIXME : Inject endpoint properties!!!!
        Jbi2WS generator = new Jbi2WS(endpoint.getEndpoint(), service, itf, extensions);
        try {
            sa = generator.generate();
        } catch (JBIGenerationException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
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

            boolean success = this.deploymentService.deploy(sa.toURI().toURL());

            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to deploy the Service Assembly located at '"
                        + sa.toURI().toURL() + "'");
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
        return org.petalslink.dsb.kernel.Constants.SOAP_SERVICE_EXPOSER;
    }

}
