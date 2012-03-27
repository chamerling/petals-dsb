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
package org.petalslink.dsb.kernel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.jbi.management.AdminServiceMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.jmx.agent.Introspector;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.Monolog;
import org.objectweb.util.monolog.wrapper.remote.lib.MonologFactoryMBeanImpl;
import org.ow2.petals.communication.jndi.client.JNDIService;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.container.ContainerService;
import org.ow2.petals.jbi.management.ManagementException;
import org.ow2.petals.jbi.management.deployment.DeploymentServiceMBean;
import org.ow2.petals.jbi.management.installation.InstallationServiceMBean;
import org.ow2.petals.jbi.management.recovery.SystemRecoveryService;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistryMBean;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.kernel.admin.PetalsAdminInterface;
import org.ow2.petals.kernel.admin.PetalsAdminServiceMBean;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.api.server.util.SystemUtil;
import org.ow2.petals.kernel.api.service.ServiceEndpoint;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.kernel.configuration.DomainConfiguration;
import org.ow2.petals.kernel.server.FractalHelper;
import org.ow2.petals.kernel.server.MBeanHelper;
import org.ow2.petals.kernel.server.PetalsStopThread;
import org.ow2.petals.service.ServiceEndpointImpl;
import org.ow2.petals.tools.ws.WebServiceException;
import org.ow2.petals.tools.ws.WebServiceManager;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.util.JNDIUtil;
import org.petalslink.dsb.kernel.api.listener.LifeCycleManager;

import static org.ow2.petals.kernel.server.FractalHelper.AUTOLOADER_COMPONENT;
import static org.ow2.petals.kernel.server.FractalHelper.COMMUNICATION_COMPOSITE;
import static org.ow2.petals.kernel.server.FractalHelper.CONFIGURATION_COMPONENT;
import static org.ow2.petals.kernel.server.MBeanHelper.ADMIN_MBEAN;
import static org.ow2.petals.kernel.server.MBeanHelper.DEPLOYMENT_MBEAN;
import static org.ow2.petals.kernel.server.MBeanHelper.DOMAIN;
import static org.ow2.petals.kernel.server.MBeanHelper.ENDPOINT_MBEAN;
import static org.ow2.petals.kernel.server.MBeanHelper.INSTALLATION_MBEAN;
import static org.ow2.petals.kernel.server.MBeanHelper.LOGGER_MBEAN;
import static org.ow2.petals.kernel.server.MBeanHelper.PETALS_ADMIN;
import static org.ow2.petals.kernel.server.MBeanHelper.findLocalJMXServer;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class PetalsServerImpl extends org.ow2.petals.kernel.server.PetalsServerImpl {

    private static final String LOGGER_PROPERTIES_FILE_NAME = "loggers.properties";

    private static final String[] MONOLOG_FILE_HANDLERS = { "petalsFile" };

    /**
     * Thread used to stop Petals in an isolate thread
     */
    private final PetalsStopThread petalsStopThread;

    /**
     * The domain configuration
     */
    private DomainConfiguration domainConfiguration;

    /**
     * The container configuration
     */
    private ContainerConfiguration containerConfiguration;

    /**
     * Petals composite
     */
    private Component petalsComposite;

    /**
     * Petals Content Controller
     */
    private ContentController petalsContentController;

    private EndpointRegistry registry;

    /**
     * Creates a new instance of {@link PetalsServerImpl}
     * 
     * @throws PetalsException
     */
    public PetalsServerImpl() throws PetalsException {
        this(false);
    }

    /**
     * Creates a new instance of {@link PetalsServerImpl}
     * 
     * @throws PetalsException
     */
    public PetalsServerImpl(boolean observer) throws PetalsException {
        this.petalsStopThread = new PetalsStopThread(this);
    }

    /**
     * Initialize the PEtALS environment. Init Qname, JMX, Monolog, Fractal.
     * 
     * @see org.ow2.petals.kernel.api.server.PetalsServer#init()
     */
    @Override
    public void init() throws PetalsException {

        // set the JMX server
        System
                .setProperty("javax.management.builder.initial",
                        "mx4j.server.MX4JMBeanServerBuilder");

        try {
            // initialize MONOLOG
            this.initializeMonolog();

            // initialize Petals Fractal composite
            this.initializePetalsComposite();

        } catch (IOException exception) {
            // clean up the petals server
            throw new PetalsException("Problem while initializing Petals DSB", exception);
        }
    }

    /**
     * Start the Petals Server. Start the fractal components. Register the
     * different component, then try to recover the Petals container.
     * 
     * @see org.ow2.petals.kernel.api.server.PetalsServer#start()
     */
    @Override
    public void start() throws PetalsException {

        try {
            // start PEtALS Fractal composite
            this.startPetalsComposite();

            // register the PEtALS server in the Petals admin.
            this.registerPetalsServer();

            // expose PEtALS MBean object into the JMX server
            this.registerMBeans();

            this.startTools();

            // recover all JBI entities
            this.recoverSystem();

            // set the container as active in the topology
            this.startupDone();

        } catch (Exception exception) {
            exception.printStackTrace();
            // clean up the petals server
            if (this.petalsComposite != null) {
                System.err.println("Problem while starting Petals DSB, trying to stop Petals DSB cleanly...");
                try {
                    FractalHelper.stopComponent(this.petalsComposite);
                } catch (Throwable e) {
                    System.err.println("Failed to stop Petals DSB cleanly");
                }
            }
            throw new PetalsException("Failed to start Petals DSB", exception);
        }

        this.launchStartActions();

        System.out.println("");
        System.out.println("### The Petals Distributed Service Bus is started at " + new Date()
                + " ###");
        System.out.println("");
    }

    private void launchStartActions() {
        Component lcm = FractalHelper.getRecursiveComponentByName(petalsContentController, "LifeCycleManagerImpl");
        if (lcm != null) {
            try {
                LifeCycleManager manager = (LifeCycleManager) lcm.getFcInterface("service");
                manager.onStart();
            } catch (NoSuchInterfaceException e) {
            }
        }
    }
    
    private void launchStopActions() {
        Component lcm = FractalHelper.getRecursiveComponentByName(petalsContentController, "LifeCycleManagerImpl");
        if (lcm != null) {
            try {
                LifeCycleManager manager = (LifeCycleManager) lcm.getFcInterface("service");
                manager.onStop();
            } catch (NoSuchInterfaceException e) {
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.server.PetalsServer#stop()
     */
    @Override
    public void stop() throws PetalsException {
        Exception exception = null;
        try {
            this.stopPetalsComposite();
        } catch (Exception e) {
            exception = e;
        }

        this.launchStopActions();

        // NOTE : unreachable part if the listener exists the system

        if (exception != null) {
            if (exception instanceof PetalsException) {
                throw (PetalsException) exception;
            } else {
                throw new PetalsException(exception);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.kernel.api.server.PetalsServer#getContainerConfiguration()
     */
    @Override
    public String getContainerConfiguration() throws PetalsException {
        if (this.containerConfiguration == null) {
            throw new PetalsException("The container configuration is not properly set");
        }

        return this.containerConfiguration.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.server.PetalsServer#browseJNDI()
     */
    @Override
    public String browseJNDI() throws PetalsException {
        if (this.containerConfiguration == null) {
            throw new PetalsException("The container configuration is not properly set");
        }

        String result = null;
        Component jndiComponent = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, FractalHelper.JNDI_COMPONENT);
        try {
            JNDIService jndiService = (JNDIService) jndiComponent.getFcInterface("service");
            InitialContext initialContext = jndiService.getInitialContext();
            if (this.domainConfiguration.getJndiConfiguration() == null) {
                result = JNDIUtil.browseJNDI(initialContext, (String) null, 0);
            } else {
                URI providerUrl = this.domainConfiguration.getJndiConfiguration()
                        .getJndiProviderUrl();
                result = JNDIUtil.browseJNDI(initialContext, providerUrl.getHost(), providerUrl
                        .getPort());
            }

        } catch (NoSuchInterfaceException e) {
            throw new PetalsException(e);
        } catch (NamingException e) {
            throw new PetalsException(e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServiceEndpoint> getServiceEndpoints(final boolean global) throws PetalsException {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        try {
            List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> eps = this.registry
                    .getEndpoints();
            if (eps != null) {
                for (ServiceEndpoint serviceEndpoint : eps) {
                    ServiceEndpointImpl se = new ServiceEndpointImpl();
                    // TODO !
                    se.setInterfacesName(serviceEndpoint.getInterfacesName());
                    se.setEndpointName(serviceEndpoint.getEndpointName());
                    se.setServiceName(serviceEndpoint.getServiceName());
                    se.setDescription(serviceEndpoint.getDescription());
                    se.setLocation(serviceEndpoint.getLocation());
                    result.add(se);
                }
            }
        } catch (RegistryException e) {
            throw new PetalsException("Can not get endpoints");
        }
        return result;
    }

    /**
     * Initialize petals composite
     * 
     * @throws PetalsException
     * 
     */
    private void initializePetalsComposite() throws PetalsException {
        try {
            // Class<Factory> petalsFactoryClass =
            // (Class<Factory>)PetalsServerImpl.class.getClassLoader().loadClass("Petals");
            // Factory petalsFactory = petalsFactoryClass.newInstance();
            // this.petalsComposite = petalsFactory.newFcInstance();
            this.petalsComposite = FractalHelper.createNewComponent(FractalHelper.PETALS_COMPOSITE);
            this.petalsContentController = Fractal.getContentController(this.petalsComposite);
        } catch (NoSuchInterfaceException e) {
            throw new PetalsException("Error creating PEtALS Fractal Composite", e);
        } catch (ADLException e) {
            throw new PetalsException("Error creating PEtALS Fractal Composite", e);
        }
    }

    /**
     * Initialize the environment for Monolog and start the Monolog factory
     * 
     * @throws IOException
     */
    private void initializeMonolog() throws IOException {
        // get the loggers properties
        URL logConfUrl = this.getClass().getResource("/" + LOGGER_PROPERTIES_FILE_NAME);
        if (logConfUrl == null) {
            throw new IOException("Failed to reach Monolog resource '"
                    + LOGGER_PROPERTIES_FILE_NAME + "'");
        }
        Properties logProperties = new Properties();
        logProperties.load(logConfUrl.openStream());

        // configure the Monolog handlers
        this.configureMonologHandlers(logProperties);

        Monolog.getMonologFactory(logProperties);
    }

    /**
     * Configure the Monolog handlers
     * 
     * @param logProperties
     */
    private void configureMonologHandlers(Properties logProperties) {
        // create logs directory is not present
        File logDir = new File(SystemUtil.getPetalsInstallDirectory(), "logs");
        logDir.mkdirs();

        // get the current date as a string
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(currentDate);

        for (String handlerName : MONOLOG_FILE_HANDLERS) {
            // set the path names of the Monologs file handlers
            String handlerOutput = logProperties.getProperty("handler." + handlerName + ".output");
            if (handlerOutput != null) {
                handlerOutput = handlerOutput.replace("%d", dateString);
                logProperties.setProperty("handler." + handlerName + ".output", new File(logDir,
                        handlerOutput).getAbsolutePath());
            }

            // TODO: Monolog bug on the appendMode field
            String handlerAppendMode = logProperties.getProperty("handler." + handlerName
                    + ".appendMode");
            if ("true".equals(handlerAppendMode) || "false".equals(handlerAppendMode)) {
                System.setProperty(handlerAppendMode, handlerAppendMode);
            }
        }
    }

    /**
     * Start the Petals Fractal composite. TODO create a Fractal component to
     * handle the starting and stoping of Fractal composites/components.
     * 
     * @throws NoSuchInterfaceException
     * @throws IllegalLifeCycleException
     * @throws ADLException
     * @throws IllegalBindingException
     * @throws IllegalContentException
     * @throws PetalsException
     */
    private void startPetalsComposite() throws NoSuchInterfaceException, IllegalLifeCycleException,
            ADLException, IllegalContentException, IllegalBindingException, PetalsException {

        // start first the configuration component
        Component configurationComponent = FractalHelper.getComponentByName(
                this.petalsContentController, CONFIGURATION_COMPONENT);
        if (!FractalHelper.startComponent(configurationComponent)) {
            throw new PetalsException("Failed to start PEtALS Fractal component "
                    + CONFIGURATION_COMPONENT);
        }

        // retrieve the configuration from the Configuration Service
        ConfigurationService configurationService = (ConfigurationService) configurationComponent
                .getFcInterface("service");
        this.containerConfiguration = configurationService.getContainerConfiguration();

        this.domainConfiguration = configurationService.getDomainConfiguration();

        // remove the autoloader component if not activated
        if (!this.containerConfiguration.isActivateAutoloader()) {
            Component autoloaderComponent = FractalHelper.getComponentByName(
                    this.petalsContentController, AUTOLOADER_COMPONENT);
            BindingController autoLoaderBC = Fractal.getBindingController(autoloaderComponent);
            for (String bindingName : autoLoaderBC.listFc()) {
                autoLoaderBC.unbindFc(bindingName);
            }
            this.petalsContentController.removeFcSubComponent(autoloaderComponent);
        }

        // start the communication composite
        Component communicationComposite = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, COMMUNICATION_COMPOSITE);
        if (!FractalHelper.startComponent(communicationComposite)) {
            throw new PetalsException("Failed to start PEtALS Fractal composite "
                    + COMMUNICATION_COMPOSITE);
        }

        // finally, start the others components
        if (!FractalHelper.startComponent(this.petalsComposite)) {
            throw new PetalsException("Failed to start PEtALS Fractal components");
        }

        Component registryComponent = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, FractalHelper.ENDPOINT_COMPONENT);

        if (registryComponent == null) {
            throw new PetalsException("Can not find the registry component "
                    + FractalHelper.ENDPOINT_COMPONENT);
        }
        this.registry = (EndpointRegistry) registryComponent.getFcInterface("service");
    }

    /**
     * Stop the Petals Fractal composite.
     * 
     * @throws Exception
     */
    private void stopPetalsComposite() throws Exception {

        LifeCycleController lifeCycleController = Fractal
                .getLifeCycleController(this.petalsComposite);

        if (LifeCycleController.STARTED.equals(lifeCycleController.getFcState())) {

            Component containerComponent = FractalHelper.getComponentByName(
                    this.petalsContentController, FractalHelper.CONTAINER_COMPOSITE);
            ContentController containerContentController = Fractal
                    .getContentController(containerComponent);
            // First stop and shutdown all the JBI SAs and components
            List<Component> sas = FractalHelper.getComponentListByPrefix(
                    containerContentController,
                    ContainerService.PREFIX_SERVICE_ASSEMBLY_LIFE_CYCLE_NAME);
            Collections.reverse(sas);
            for (Component component : sas) {
                FractalHelper.stopComponent(component);
            }

            List<Component> components = FractalHelper.getComponentListByPrefix(
                    containerContentController, ContainerService.PREFIX_COMPONENT_LIFE_CYCLE_NAME);
            Collections.reverse(components);
            for (Component component : components) {
                FractalHelper.stopComponent(component);
            }

            if (DomainConfiguration.DomainMode.STANDALONE
                    .equals(this.domainConfiguration.getMode())) {
                // Standalone mode
                // Then, prepare the stop of the Transporter, to stop the
                // ongoing transfers
                Component standaloneTransporterComponent = FractalHelper
                        .getRecursiveComponentByName(this.petalsContentController,
                                FractalHelper.STANDALONE_TRANSPORTER_COMPONENT);
                Transporter standaloneTransporter = (Transporter) standaloneTransporterComponent
                        .getFcInterface("service");
                standaloneTransporter.stopTraffic();
            } else {
                // Platform (distributed) mode
                // Then, prepare the stop of the Transporters, to stop the
                // oongoing transfers
                Component tcpTransporterComponent = FractalHelper.getRecursiveComponentByName(
                        this.petalsContentController, FractalHelper.TCP_TRANSPORTER_COMPONENT);
                Transporter tcpTransporter = (Transporter) tcpTransporterComponent
                        .getFcInterface("service");
                tcpTransporter.stopTraffic();
                Component localTransporterComponent = FractalHelper.getRecursiveComponentByName(
                        this.petalsContentController, FractalHelper.LOCAL_TRANSPORTER_COMPONENT);
                Transporter localTransporter = (Transporter) localTransporterComponent
                        .getFcInterface("service");
                localTransporter.stopTraffic();

            }

            // After, prepare the stop of the router
            Component routerComponent = FractalHelper.getRecursiveComponentByName(
                    this.petalsContentController, FractalHelper.ROUTER_COMPONENT);
            RouterService router = (RouterService) routerComponent.getFcInterface("service");
            router.stopTraffic();

            // Finally, stop the others components, we must stop the Composite
            // elements in an opposite way than their start order
            FractalHelper.stopComposite(this.petalsComposite);
        }
    }

    /**
     * Register to the PetalsAdmin service this Petals server instance
     * 
     * @throws NoSuchInterfaceException
     * @throws PetalsException
     */
    private void registerPetalsServer() throws NoSuchInterfaceException, PetalsException {
        Component petalsAdminComponent = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, FractalHelper.PETALSADMIN_COMPONENT);
        PetalsAdminInterface petalsAdminService = (PetalsAdminInterface) petalsAdminComponent
                .getFcInterface("service");

        petalsAdminService.setPetalsStopThread(this.petalsStopThread);
    }

    /**
     * Recover the system state. All state elements are recovered (components,
     * share libraries, service assemblies).
     * 
     * @throws NoSuchInterfaceException
     * @throws ManagementException
     * @throws ADLException
     * @throws PetalsException
     * @throws IllegalLifeCycleException
     * @throws IllegalBindingException
     * @throws IllegalContentException
     */
    private void recoverSystem() throws NoSuchInterfaceException, ManagementException,
            ADLException, PetalsException, IllegalBindingException, IllegalLifeCycleException,
            IllegalContentException {
        Component managementComposite = FractalHelper.getComponentByName(
                this.petalsContentController, FractalHelper.JBI_MANAGEMENT_COMPOSITE);
        ContentController managementContentController = Fractal
                .getContentController(managementComposite);

        Component systemRecoveryComponent = FractalHelper.getComponentByName(
                managementContentController, FractalHelper.SYSTEMRECOVERY_COMPONENT);
        SystemRecoveryService systemRecoveryService = (SystemRecoveryService) systemRecoveryComponent
                .getFcInterface("service");

        systemRecoveryService.recoverAllEntities();

        // Then remove this service as it is no more necessary
        FractalHelper.stopComponent(systemRecoveryComponent);
        BindingController systemRecoveryBC = Fractal.getBindingController(systemRecoveryComponent);
        for (String bindingName : systemRecoveryBC.listFc()) {
            systemRecoveryBC.unbindFc(bindingName);
        }
        managementContentController.removeFcSubComponent(systemRecoveryComponent);
    }

    /**
     * Set the local container state to started. The local container state is in
     * unknown state until all is really started.
     * 
     * @throws Exception
     */
    private void startupDone() throws Exception {
        Component topologyComponent = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, FractalHelper.TOPOLOGY_COMPONENT);

        if (topologyComponent == null) {
            if (!DomainConfiguration.DomainMode.STANDALONE.equals(this.domainConfiguration
                    .getMode())) {
                throw new NullPointerException("The topology fractal component is null");
            }
        } else {
            TopologyService service = (TopologyService) topologyComponent.getFcInterface("service");
            service.setContainerState(this.containerConfiguration.getName(),
                    ContainerConfiguration.ContainerState.STARTED);
        }
    }

    /**
     * @throws NoSuchInterfaceException
     * @throws PetalsException
     * 
     */
    private void startTools() throws NoSuchInterfaceException, PetalsException {
        // Expose services as web services
        Component webServiceComponent = FractalHelper.getRecursiveComponentByName(
                this.petalsContentController, FractalHelper.WEBSERVICEMANAGER_COMPONENT);
        if (webServiceComponent != null) {
            WebServiceManager webServiceManager = (WebServiceManager) webServiceComponent
                    .getFcInterface("service");
            try {
                webServiceManager.exposeServices();
            } catch (WebServiceException e) {
                throw new PetalsException("Fail to expose services : " + e.getMessage());
            }
        }

        // TODO get all the listeners and add them to the listeners list
        // List<Component> components = FractalHelper.getComponentListByPrefix(
        // this.petalsContentController, "webservice-");
        // for (Component component : components) {
        //
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizeRegistry() throws PetalsException {
        try {
            this.registry.synchronizeData();
        } catch (RegistryException e) {
            throw new PetalsException("Fail to synchronize registry : " + e.getMessage());
        }
    }

    protected void registerMBeans() throws Exception {
        // CHA 2012 : check the difference with the MBeanHeler!
        ContentController contentController = Fractal.getContentController(this.petalsComposite);

        // unactivate the method invokation cache for the Fractal MBeans
        Introspector.CURRENCY_TIME_LIMIT = "-1";

        MBeanServer jmxServer = findLocalJMXServer(this.petalsComposite);

        // register AdminService
        MBeanHelper.registerComponent(contentController, FractalHelper.ADMIN_COMPONENT, jmxServer,
                AdminServiceMBean.class, DOMAIN + ":name=" + ADMIN_MBEAN + ",type=service");

        // register DeploymentService
        MBeanHelper.registerComponent(contentController, FractalHelper.DEPLOYMENT_COMPONENT,
                jmxServer, DeploymentServiceMBean.class, DOMAIN + ":name=" + DEPLOYMENT_MBEAN
                        + ",type=service");

        // register InstallationService
        MBeanHelper.registerComponent(contentController, FractalHelper.INSTALLATION_COMPONENT,
                jmxServer, InstallationServiceMBean.class, DOMAIN + ":name=" + INSTALLATION_MBEAN
                        + ",type=service");

        // register EndpointRegistry
        MBeanHelper.registerComponent(contentController, FractalHelper.ENDPOINT_COMPONENT,
                jmxServer, EndpointRegistryMBean.class, DOMAIN + ":name=" + ENDPOINT_MBEAN
                        + ",type=service");

        // register Transporter Monitoring
        // registerComponent(contentController,
        // FractalHelper.TRANSPORTMONITORING_COMPONENT,
        // jmxServer,
        // org.ow2.petals.monitoring.transporter.TransportMonitoringMBean.class,
        // DOMAIN + ":name=" + TRANSPORTER_MONITORING + ",type=service");

        // register Petals Admin
        MBeanHelper.registerComponent(contentController, FractalHelper.PETALSADMIN_COMPONENT,
                jmxServer, PetalsAdminServiceMBean.class, DOMAIN + ":name=" + PETALS_ADMIN
                        + ",type=service");

        // register the monitoring tool
        //Monitoring monitoring = new Monitoring();
        //jmxServer.registerMBean(monitoring, new ObjectName(DOMAIN + ":name=" + MONITORING_MBEAN
         //       + ",type=service"));

        // bind the fractal router monitoring to the monitoring tool
        // TODO: Uncomment
        /*
         * Component router_monitor_component =
         * FractalHelper.getRecursiveComponentByName( contentController,
         * FractalHelper.MONITORING_MODULE_IMPL); RouterMonitorImpl
         * router_monitor = (RouterMonitorImpl) router_monitor_component
         * .getFcInterface("/content");
         * router_monitor.setMonitoring_util(monitoring);
         * monitoring.setRouterMonitor(router_monitor);
         */

        // register the Monolog
        
        // CHA 2012 : This is not the same in Petals Kernel 3.2
        jmxServer.registerMBean(Introspector.createMBean(new MonologFactoryMBeanImpl()),
                new ObjectName(DOMAIN + ":name=" + LOGGER_MBEAN + ",type=service"));
    }

}
