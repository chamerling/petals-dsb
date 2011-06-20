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
import java.net.URI;
import java.net.URISyntaxException;
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
import org.ow2.petals.container.lifecycle.ServiceAssemblyLifeCycle;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.descriptor.original.generated.Services;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.jbi.management.deployment.AtomicDeploymentService;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.tools.generator.jbi.ws2jbi.Constants;
import org.ow2.petals.tools.generator.jbi.ws2jbi.WS2Jbi;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.management.binder.BinderChecker;
import org.petalslink.dsb.kernel.api.management.binder.BinderException;
import org.petalslink.dsb.kernel.api.management.binder.ServiceBinder;
import org.petalslink.dsb.kernel.api.management.binder.ServiceRegistry;
import org.petalslink.dsb.kernel.util.JBIFileHelper;
import org.petalslink.dsb.ws.api.ServiceEndpoint;

/**
 * Let's bind a web service!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceBinder.class) })
public class SOAPServiceBinderImpl implements ServiceBinder {

    public static final String WSDL_PROPERTY = "wsdl";

    public static final String WORK_DIR = "servicebinder";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    private AtomicDeploymentService deploymentService;

    @Requires(name = "service-registry", signature = ServiceRegistry.class)
    private ServiceRegistry serviceRegistry;

    @Requires(name = "binder-checker", signature = BinderChecker.class)
    private BinderChecker binderChecker;

    @Requires(name = "admin", signature = AdminService.class)
    protected AdminService adminService;

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
    public List<ServiceEndpoint> bind(Map<String, Object> serviceProperties) throws BinderException {
        List<ServiceEndpoint> result = null;
        if (!this.binderChecker.canBindOnProtocol(this.getProtocol())) {
            throw new BinderException("No component found to bind SOAP service");
        }

        Object o = serviceProperties.get(WSDL_PROPERTY);
        if (o == null) {
            throw new BinderException("WSDL can not be null");
        }

        String wsdlURI = (String) o;

        File sa = null;
        URI uri;
        try {
            uri = new URI(wsdlURI);
        } catch (URISyntaxException e1) {
            throw new BinderException("Bad URI " + e1.getMessage());
        }

        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put(Constants.OUTPUT_DIR, this.workPath.getAbsolutePath());
        // TODO : Get the component version from the component descriptor
        extensions.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                org.petalslink.dsb.kernel.Constants.DEFAULT_SOAP_COMPONENT_VERSION);

        // met's say that it is a platform service in the JBI endpoint name...
        // this is not valid match with WSDL description...
        // extensions.put(org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_PREFIX,
        // org.petalslink.dsb.kernel.Constants.SOAP_PLATFORM_ENDPOINT_PREFIX);
        // TODO : let's put some additional propertues which are soa4all
        // related!

        WS2Jbi generator = new WS2Jbi(uri, extensions);
        try {
            sa = generator.generate();
        } catch (JBIGenerationException e) {
            throw new BinderException(e.getMessage());
        }

        Jbi descriptor = JBIFileHelper.readDescriptor(sa);
        if (descriptor == null) {
            throw new BinderException("Can not get the JBI descriptor from generated SA...");
        }
        String saName = descriptor.getServiceAssembly().getIdentification().getName();

        if (saName == null) {
            // it means that we will not be able to start the SA...
            throw new BinderException("Can not get the JBI service assembly name from generated SA");
        }

        try {

            boolean success = this.deploymentService.deploy(sa.toURL());
            if (success) {
                this.log.info("Service assembly '" + saName + "' has been deployed");
            } else {
                this.log.warning("Failed to deploy the Service Assembly located at '" + sa.toURL());
                throw new PEtALSWebServiceException("Deployment failure");
            }
        } catch (Exception e) {
            throw new BinderException(e);
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
            throw new BinderException(e.getMessage());
        }

        // at this step all is ok...
        // TODO : Store somewhere that the Web service has been bound!
        // this.bindCache.put(wsdlURI, saName);
        this.serviceRegistry.addService(org.petalslink.dsb.kernel.Constants.SOAP_SERVICE_BINDER,
                wsdlURI, null);

        // TODO : delete the generated file in all cases
        if (sa != null) {
            sa.delete();
        }

        result = new ArrayList<ServiceEndpoint>();
        try {
            ServiceAssemblyLifeCycle salc = adminService.getServiceAssemblyByName(saName);
            List<ServiceUnitLifeCycle> sulcs = salc.getServiceUnitLifeCycles();
            for (ServiceUnitLifeCycle su : sulcs) {
                Jbi jbi = su.getServiceUnitDescriptor();
                if (jbi != null) {
                    Services services = jbi.getServices();
                    if (services != null) {
                        List<org.ow2.petals.jbi.descriptor.original.generated.Provides> provides = services
                                .getProvides();
                        if (provides != null) {
                            for (org.ow2.petals.jbi.descriptor.original.generated.Provides provides2 : provides) {
                                ServiceEndpoint se = new ServiceEndpoint();
                                se.setEndpoint(provides2.getEndpointName());
                                se.setItf(provides2.getInterfaceName());
                                se.setService(provides2.getServiceName());
                                result.add(se);
                            }
                        } else {
                            log.debug("Can not find any provides");
                        }
                    } else {
                        log.debug("Can not find any service");
                    }
                } else {
                    log.debug("Can not find any JBI descriptor");
                }
            }
        } catch (Exception e) {
            log.warning("Can not retrieve information...", e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getProtocol() {
        return org.petalslink.dsb.kernel.Constants.SOAP_SERVICE_BINDER;
    }
}
