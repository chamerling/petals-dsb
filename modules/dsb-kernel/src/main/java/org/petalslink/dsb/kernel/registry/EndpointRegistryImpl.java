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
package org.petalslink.dsb.kernel.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryListener;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.kernel.configuration.ContainerConfiguration.RegistryMode;
import org.ow2.petals.registry.api.config.Configuration;
import org.ow2.petals.registry.api.config.ConfigurationLoader;
import org.ow2.petals.registry.api.config.ConfigurationLoaderFactory;
import org.ow2.petals.registry.api.config.RemoteConfiguration;
import org.ow2.petals.registry.api.exception.LifeCycleException;
import org.ow2.petals.registry.api.exception.RegistryException;
import org.ow2.petals.registry.client.RegistryClientFactory;
import org.ow2.petals.registry.core.factory.RegistryFactory;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.jbi.Adapter;
import org.petalslink.dsb.kernel.api.PetalsService;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;

import static org.ow2.petals.kernel.configuration.ConfigurationService.MASTER;
import static org.ow2.petals.kernel.configuration.ConfigurationService.PEER;
import static org.ow2.petals.kernel.configuration.ConfigurationService.PROPERTY_REGISTRY_MODE;
import static org.ow2.petals.kernel.configuration.ConfigurationService.SLAVE;
import static org.ow2.petals.kernel.configuration.ConfigurationService.STANDALONE;


/**
 * NOTE : This registry is the clone of the petals registry. Since it is not possible
 * to override some methods, the only thing that changes is the use of the same
 * port for the registry and for the petals web services ie one less port!!!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointRegistryImpl extends BaseEndpointRegistry implements EndpointRegistry,
        PetalsService, RegistryListenerManager {

    // required services
    protected ConfigurationService configurationService;

    protected org.ow2.petals.communication.topology.TopologyService localTopologyService;

    protected List<RegistryListener> registryListeners = new ArrayList<RegistryListener>();

    // local things
    protected ContainerConfiguration localContainerConfiguration;

    private ScheduledExecutorService registryTopologyUpdater;

    private Configuration localConfig;

    private boolean registeredOnMaster = false;
    
    // a new manager
    protected RegistryListenerManager listenerManager;

    /**
     * @param log
     */
    public EndpointRegistryImpl(LoggingUtil log) {
        this.log = log;
        this.listenerManager = new RegistryListenerManagerImpl();
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        this.log.call();
    }

    /**
     * {@inheritDoc}
     */
    public void setup() throws Exception {
        this.log.call();
        this.localContainerConfiguration = this.configurationService.getContainerConfiguration();

        URL regConfigURL = this.getClass().getResource(CONFIG);
        if (regConfigURL == null) {
            throw new IOException(
                    "Registry configuration file has not been found, check classpath for " + CONFIG);
        }
        File f = new File(regConfigURL.toURI());
        try {
            // FIXME : To be moved in the registry configuration part
            // load the registry configuration file
            this.createRegistryConfig(f);
            this.loadRegistry();
            this.createRegistryTopology();

            this.registry.init();
            this.registry.start();

            // clean local data
            // FIXME : clean at init not after start !
            this.cleanData();

        } catch (RegistryException e) {
            this.log.error(e.getMessage(), e);
            if ((this.registry != null)
                    && (this.registry.isInitialized() || this.registry.isStarted())) {
                try {
                    this.registry.stop();
                } catch (LifeCycleException e1) {
                    throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e1);
                }
            }
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e);
        }

        // start the update thread when we are sure that the registry is really
        // started
        this.createTopologyUpdater();

        // recovering data from network
        try {
            this.registry.synchronizeData();
        } catch (RegistryException e) {
            final String message = "Can not synchronize data from network, mode will be downgraded";
            if (this.log.isDebugEnabled()) {
                this.log.warning(message, e);
            } else {
                this.log.warning(message);
            }
        }

        this.client = RegistryClientFactory.getNewClient(this.registry);

        this.log.info("The registry is ready process requests on "
                + this.registry.getContext().getConfiguration().getMessageReceiverURL());
    }

    /**
     * @throws RegistryException
     * @throws PetalsException
     */
    protected void loadRegistry() throws RegistryException, PetalsException {
        this.registry = RegistryFactory.getInstance().loadLocal(this.localConfig);
        if (this.registry == null) {
            throw new PetalsException("Registry can not be loaded!");
        }
    }

    /**
     * @param updatePeriod
     */
    protected void createTopologyUpdater() {
        if (this.localTopologyService.hasValidLocalContainerDynamicTopologyConfiguration()) {

            long updatePeriod = this.configurationService.getContainerConfiguration()
                    .getTopologyUpdatePeriod();
            this.registryTopologyUpdater = Executors.newSingleThreadScheduledExecutor();
            this.registryTopologyUpdater.scheduleAtFixedRate(new TopologyUpdater(), 30,
                    updatePeriod, TimeUnit.SECONDS);

            try {
                this.localTopologyService.registerLocalContainerOnMaster();
                this.localTopologyService.updateTopology();
                this.registeredOnMaster = true;
            } catch (Throwable ex) {
                this.registeredOnMaster = false;
                this.log
                        .warning("Can't register local node onto the master node (nor updating the topology)");
            }
        }
    }

    /**
     * @throws PetalsException
     */
    protected void createRegistryTopology() throws PetalsException {
        // add the remote registries, all the registries are embedded in
        // each petals runtime...
        Set<ContainerConfiguration> containers = this.localTopologyService
                .getContainersConfiguration(null);
        String currentContainerSubdomainName = null;

        // Retrieve the subdomain of the current container
        for (ContainerConfiguration containerConfiguration : containers) {
            if (containerConfiguration.getName().equals(this.localContainerConfiguration.getName())) {
                currentContainerSubdomainName = containerConfiguration.getSubdomainName();
                break;
            }
        }

        List<ContainerConfiguration> remoteContainerConfiguration = new ArrayList<ContainerConfiguration>();

        // Searching the master node
        for (ContainerConfiguration containerConfiguration : containers) {
            if (!containerConfiguration.getSubdomainName().equals(currentContainerSubdomainName)) {
                continue;
            }

            // Skipping local container
            if (containerConfiguration.getSubdomainName().equals(
                    this.localContainerConfiguration.getName())) {
                continue;
            }

            // Now handling local subdomain container configurations
            if (containerConfiguration.getRegistryMode().equals(RegistryMode.MASTER)
                    || containerConfiguration.getRegistryMode().equals(RegistryMode.PEER)) {
                remoteContainerConfiguration.add(containerConfiguration);
            }
        }

        if (this.localContainerConfiguration.isSlave() || this.localContainerConfiguration.isPeer()) {
            for (ContainerConfiguration containerConfiguration : remoteContainerConfiguration) {
                RemoteConfiguration configuration = new RemoteConfiguration("Registry@PETALSESB-"
                        + containerConfiguration.getName());
                configuration.setMessageSenderClassName(this.localConfig
                        .getMessageSenderClassName());

                // TO be updated when new transport will be available
                String url = "http://" + containerConfiguration.getHost() + ":"
                        + containerConfiguration.getWebservicePort();
                configuration.setUri(url);
                this.registry.getContext().getTopology().add(configuration);
            }
        }

        if (this.localContainerConfiguration.isPeer()) {
            this.registry.getContext().getProperties().put(PROPERTY_REGISTRY_MODE, PEER);
        }

        if (this.localContainerConfiguration.isMaster()) {
            this.registry.getContext().getProperties().put(PROPERTY_REGISTRY_MODE, MASTER);
        }

        if (this.localContainerConfiguration.isSlave()) {
            this.registry.getContext().getProperties().put(PROPERTY_REGISTRY_MODE, SLAVE);
        }

        if (this.localContainerConfiguration.isStandalone()) {
            this.registry.getContext().getProperties().put(PROPERTY_REGISTRY_MODE, STANDALONE);
        }
    }

    /**
     * @param f
     * @param localContainerConfiguration
     * @throws RegistryException
     */
    protected void createRegistryConfig(File f) throws RegistryException {
        ConfigurationLoader loader = ConfigurationLoaderFactory.getLoader(f);
        if (loader == null) {
            throw new RegistryException("Can not get a configuration loader for the file "
                    + f.getName());
        }

        try {
            this.localConfig = loader.loadLocal(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new RegistryException(e);
        }

        // update things...
        this.localConfig
                .setName("Registry@PEtALSESB-" + this.localContainerConfiguration.getName());
        this.localConfig.setRootPath(this.localContainerConfiguration.getWorkDirectoryPath());

        String receiverURL = "http://" + this.localContainerConfiguration.getHost() + ":"
                + this.localContainerConfiguration.getWebservicePort();
        this.localConfig.setMessageReceiverURL(receiverURL);
    }

    /**
     * 
     */
    private void cleanData() {
        // stop receiving things...
        this.registry.pauseReceive();

        // delete all the local entries
        try {
            this.registry.delete(this.getRootPath(), true);
        } catch (RegistryException e) {
            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage());
            }
        }

        try {
            this.registry.clean();
        } catch (RegistryException e) {
            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage());
            }
        }
        this.registry.resumeReceive();
    }

    public void shutdown() throws Exception {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Stopping...");
        }
        // stop receiving things...
        this.registry.pauseReceive();

        // and clean the remote data from the registry. This is because for now
        // there is no other solution to stay in sync with other registries when
        // this petals node come back alive... Remote data will be retrieved at
        // startup and runtime if needed next time this node will be up and
        // running...
        try {
            this.registry.cleanRemoteData();
        } catch (RegistryException e) {
            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage());
            }
        }

        // delete all the local entries
        try {
            this.registry.delete(this.getRootPath(), true);
        } catch (RegistryException e) {
            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage());
            }
        }

        // Note : Coming next, leave registry group and more...

        if (this.registryTopologyUpdater != null) {
            // this.configurationService.removeContainerConfiguration(localContainerConfiguration);
            this.registryTopologyUpdater.shutdownNow();
        }

        try {
            this.registry.stop();
        } catch (LifeCycleException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e);
        }
    }

    @Override
    protected final String getRootPath() {
        return "/endpoints/" + this.configurationService.getContainerConfiguration().getName()
                + "/";
    }

    /**
     * {@inheritDoc}
     * @deprecated use {@link #getList()}
     */
    public List<RegistryListener> getListeners() {
        return registryListeners;
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.registry.RegistryListenerManager#addListener(org.ow2.petals.jbi.messaging.registry.RegistryListener)
     */
    public void addListener(RegistryListener listener) throws DSBException {
        if (registryListeners == null) {
            this.registryListeners = new ArrayList<RegistryListener>();
        }
        if (listener == null) {
            throw new DSBException("The listener can not be null");
        }
        this.registryListeners.add(listener);
    }
    
    class TopologyExceptionHandler implements Thread.UncaughtExceptionHandler {

        public void uncaughtException(Thread t, Throwable e) {
            EndpointRegistryImpl.this.log.error(e.getMessage() + " : "
                    + e.getCause());
        }

    }

    class TopologyUpdater implements Runnable {

        public void run() {
            try {
                EndpointRegistryImpl.this.registry.pauseReceive();

                if (EndpointRegistryImpl.this.registeredOnMaster == false) {
                    EndpointRegistryImpl.this.localTopologyService.registerLocalContainerOnMaster();
                    EndpointRegistryImpl.this.registeredOnMaster = true;
                }

                EndpointRegistryImpl.this.localTopologyService.updateTopology();

                // Adds the containers of the local subdomain to the local
                // registry
                Set<ContainerConfiguration> localSubdomainContainers = EndpointRegistryImpl.this.localTopologyService
                        .getContainersConfigurationsForLocalSubdomain();

                for (ContainerConfiguration containerConfiguration : localSubdomainContainers) {
                    RemoteConfiguration configuration = new RemoteConfiguration(
                            "Registry@PETALSESB-" + containerConfiguration.getName());

                    configuration.setMessageSenderClassName(EndpointRegistryImpl.this.localConfig
                            .getMessageSenderClassName());

                    // TO be updated when new transport will be available
                    String url = "http://" + containerConfiguration.getHost() + ":"
                            + containerConfiguration.getWebservicePort();
                    configuration.setUri(url);

                    EndpointRegistryImpl.this.registry.getContext().getTopology()
                            .add(configuration);
                }
            } catch (Throwable e) {
                EndpointRegistryImpl.this.registeredOnMaster = false;
                EndpointRegistryImpl.this.log
                        .warning("Can't synchronize local topology with master topology : "
                                + e.getCause());
            } finally {
                EndpointRegistryImpl.this.registry.resumeReceive();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void synchronizeData() throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.registry == null) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Registry is null and can not be managed!");
        }
        try {
            this.registry.synchronizeData();
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e);
        }
    }

    // Accessors

    public ConfigurationService getConfigurationService() {
        return this.configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public org.ow2.petals.communication.topology.TopologyService getTopologyService() {
        return this.localTopologyService;
    }

    public void setTopologyService(
            org.ow2.petals.communication.topology.TopologyService topologyService) {
        this.localTopologyService = topologyService;
    }

    /**
     * Set the listeners from configuration
     * 
     * @param listeners
     */
    public void setListeners(Hashtable<String, Object> listeners) {
        if (listeners != null) {
            
            // oldies
            for (Object o : listeners.values()) {
                if (o != null && o instanceof RegistryListener) {
                    try {
                        this.addListener((RegistryListener) o);
                    } catch (DSBException e) {
                    }
                }
            }
            
            // new listeners with state and more...
            // create a DSB listener instance from the petals ESB one
            for (final String key : listeners.keySet()) {
                final Object o = listeners.get(key);
                if (o != null && o instanceof RegistryListener) {
                    final RegistryListener registryListener = (RegistryListener)o;
                    try {
                        this.add(new org.petalslink.dsb.kernel.api.messaging.RegistryListener() {
                            public void onUnregister(ServiceEndpoint endpoint) throws DSBException {
                                log.call("Calling onUnregister in registry listener '" + getName()
                                        + "' for " + endpoint.toString());
                                registryListener.onUnregister(Adapter.createJBIServiceEndpoint(endpoint));
                            }

                            public void onRegister(ServiceEndpoint endpoint) throws DSBException {
                                log.call("Calling onRegister in registry listener '" + getName()
                                        + "' for " + endpoint.toString());
                                registryListener.onUnregister(Adapter.createJBIServiceEndpoint(endpoint));
                            }

                            public String getName() {
                                return key;
                            }
                        });
                    } catch (DSBException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    // The listener manager stuff...

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getList()
     */
    public List<org.petalslink.dsb.kernel.api.messaging.RegistryListener> getList() {
        return this.listenerManager.getList();
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#add(org.petalslink.dsb.kernel.api.messaging.RegistryListener)
     */
    public void add(org.petalslink.dsb.kernel.api.messaging.RegistryListener listener)
            throws DSBException {
        if (log.isDebugEnabled()) {
            this.log.debug("Adding a registry listener " + listener.getName());
        }
        this.listenerManager.add(listener);
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#get(java.lang.String)
     */
    public org.petalslink.dsb.kernel.api.messaging.RegistryListener get(String name)
            throws DSBException {
        return this.listenerManager.get(name);
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#remove(java.lang.String)
     */
    public org.petalslink.dsb.kernel.api.messaging.RegistryListener remove(String name)
            throws DSBException {
        return this.listenerManager.remove(name);
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#setState(java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        this.listenerManager.setState(name, onoff);        
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager#getState(java.lang.String)
     */
    public boolean getState(String name) {
        return this.listenerManager.getState(name);
    }
}
