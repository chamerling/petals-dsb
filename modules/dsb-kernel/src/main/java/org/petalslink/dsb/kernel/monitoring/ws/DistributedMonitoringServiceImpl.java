/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id: Router.java,v 1.2 2006/03/17 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.kernel.monitoring.ws;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.tools.monitoring.to.ContainerInformations;
import org.ow2.petals.tools.monitoring.to.MessageExchange;
import org.ow2.petals.tools.monitoring.to.RuntimeInformations;
import org.ow2.petals.tools.monitoring.to.ServiceEndpoint;
import org.ow2.petals.tools.monitoring.wsapi.MonitoringException;
import org.ow2.petals.tools.monitoring.wsapi.MonitoringService;
import org.ow2.petals.tools.ws.KernelWebService;
import org.petalslink.dsb.kernel.monitoring.router.MonitoringStorageService;
import org.petalslink.dsb.kernel.monitoring.router.MonitoringModuleImpl.ExchangeContext;
import org.petalslink.dsb.kernel.monitoring.util.MonitoringUtil;
import org.petalslink.dsb.kernel.monitoring.util.TOConverter;
import org.w3c.dom.Document;


/**
 * @author aruffie - EBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "webservice", signature = MonitoringService.class),
        @Interface(name = "service", signature = KernelWebService.class) })
public class DistributedMonitoringServiceImpl implements MonitoringService, KernelWebService {

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Requires(name = "topologyService", signature = org.ow2.petals.communication.topology.TopologyService.class)
    private TopologyService topologyService;

    @Requires(name = "storageService", signature = MonitoringStorageService.class)
    private MonitoringStorageService storageService;

    @Requires(name = "registryService", signature = EndpointRegistry.class)
    private EndpointRegistry registryService;

    public DistributedMonitoringServiceImpl() {

    }

    public ContainerInformations getContainerInformations(final String containerName)
            throws MonitoringException {
        try {
            return this.createContainerInformations(containerName);
        } catch (final PetalsException e) {
            throw new MonitoringException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.monitoring.wsapi.MonitoringService#getEndpoints(
     * java.lang.String)
     */
    public List<ServiceEndpoint> getEndpoints(final String containerId) throws MonitoringException {
        final List<ServiceEndpoint> serviceEndpoints = new ArrayList<ServiceEndpoint>();
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.registryService.getEndpoints();
        } catch (final RegistryException e) {
            throw new MonitoringException(e);
        }
        if (endpoints != null) {
            for (final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint : endpoints) {
                final ServiceEndpoint ed = TOConverter.convert(endpoint);
                if (ed.getContainerLocation().equals(containerId) || (containerId == null)) {
                    serviceEndpoints.add(ed);
                }
            }
        }
        return serviceEndpoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.monitoring.wsapi.MonitoringService#getMessageExchange
     * (java.util.Date, java.util.Date)
     */
    public List<MessageExchange> getMessageExchanges(final Date begin, final Date ending) {
        final List<MessageExchange> exchanges = new ArrayList<MessageExchange>();
        final Set<String> keys = this.storageService.getStorage().keySet();
        final Iterator<String> it = keys.iterator();

        if ((begin == null) && (ending == null)) {
            while (it.hasNext()) {
                exchanges.add(this.storageService.getStorage().get(it.next()).getExchange());
            }
        } else {
            if (begin == null) {
                throw new IllegalArgumentException(
                        "The 'begin' parameter must not be null if 'ending' parameter is null too ");
            }

            if (ending == null) {
                throw new IllegalArgumentException(
                        "The 'ending' parameter must not be null if 'begin' parameter is null too ");
            }
            while (it.hasNext()) {
                final String exchangeId = it.next();
                final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);

                /*
                 * Get all exchanges began between "begin" and "ending"
                 * parameters
                 */
                if ((eContext.getBeginExchange().getTime() > begin.getTime())
                        && (eContext.getEndingExchange().getTime() < ending.getTime())) {
                    exchanges.add(eContext.getExchange());
                }
            }
        }
        return exchanges;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.tools.monitoring.wsapi.MonitoringService#
     * getRuntimeInformations()
     */
    public RuntimeInformations getRuntimeInformations() {
        return this.createRuntimeInformations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.monitoring.wsapi.MonitoringService#getServices()
     */
    public List<String> getServices() {
        final List<String> services = new ArrayList<String>();
        final Set<String> set = this.storageService.getStorage().keySet();
        final Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            ExchangeContext eContext = this.storageService.getStorage().get(it.next());
            services.add(eContext.getExchange().getService().toString());
        }
        return services;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.tools.monitoring.wsapi.MonitoringService#
     * resolveContainerForEndpoint(java.lang.String)
     */
    public String resolveContainerForEndpoint(final String endpointName) throws MonitoringException {
        /*
         * final Set<java.util.Map.Entry<String, ExchangeContext>> set =
         * this.storageService.getStorage().entrySet(); final
         * Iterator<java.util.Map.Entry<String, ExchangeContext>> it =
         * set.iterator(); while(it.hasNext()){ java.util.Map.Entry<String,
         * ExchangeContext> entry = it.next(); final ServiceEndpoint endpoint =
         * entry.getValue().getExchange().getEndpoint();
         * if(endpoint.getEndpointName().equals(endpointName)){ return
         * endpoint.getContainerLocation(); } }
         */
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.registryService.getEndpoints();
        } catch (final RegistryException e) {
            throw new MonitoringException(e);
        }
        if (endpoints != null) {
            for (final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint : endpoints) {
                if (endpoint.getEndpointName().equals(endpointName)) {
                    return endpoint.getLocation().getContainerName();
                }
            }
        }
        return null;
    }

    private ContainerInformations createContainerInformations(final String containerName)
            throws PetalsException {
        final ContainerInformations cInfos = new ContainerInformations();
        final ContainerConfiguration cConf = this.topologyService
                .getContainerConfiguration(containerName);
        cInfos.setContainerId(containerName);
        cInfos.setDaemonThreadCount(ManagementFactory.getThreadMXBean().getDaemonThreadCount());
        cInfos.setDescription(cConf.getDescription());
        cInfos.setHeapMemoryUsage(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
                .getUsed());
        cInfos.setHost(cConf.getHost());
        cInfos.setJmxJNDIPort(Integer.toString(cConf.getJmxRMIConnectorPort()));
        cInfos.setObjectPendingFinalizationCount(ManagementFactory.getMemoryMXBean()
                .getObjectPendingFinalizationCount());
        cInfos.setPeakThreadCount(ManagementFactory.getThreadMXBean().getPeakThreadCount());
        cInfos.setStatus(cConf.getState().toString());
        System.out.println("state: " + cConf.getState().toString());
        cInfos.setTcpPort(Integer.toString(cConf.getTCPPort()));
        cInfos.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
        cInfos.setTotalStartedThreadCount(ManagementFactory.getThreadMXBean()
                .getTotalStartedThreadCount());
        cInfos.setUpTime(ManagementFactory.getRuntimeMXBean().getUptime());
        cInfos.setVersion(cConf.getDescription());
        return cInfos;
    }

    private RuntimeInformations createRuntimeInformations() {
        final RuntimeInformations rInfos = new RuntimeInformations();
        rInfos.setAvailableProcessors(ManagementFactory.getOperatingSystemMXBean()
                .getAvailableProcessors());
        rInfos.setClassLoading(DistributedMonitoringServiceImpl.class.getClassLoader().getClass()
                .getName());
        rInfos.setFreeMemory(Runtime.getRuntime().freeMemory());
        rInfos.setMaxMemory(Runtime.getRuntime().maxMemory());
        rInfos.setMemory(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().toString());
        rInfos.setOperationSystem(ManagementFactory.getOperatingSystemMXBean().getName());
        rInfos.setProcessCPUTime(ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime());
        rInfos.setRuntime(ManagementFactory.getRuntimeMXBean().getName());
        rInfos.setThreading(Boolean.toString(ManagementFactory.getThreadMXBean()
                .isCurrentThreadCpuTimeSupported()));
        rInfos.setTotalMemory(Runtime.getRuntime().totalMemory());
        return rInfos;
    }

    public Component getComponent() {
        return this.component;
    }

    public List<String> getAllContainerName() throws MonitoringException {
        final List<String> containerNames = new ArrayList<String>();
        Set<ContainerConfiguration> configurations = null;
        configurations = this.topologyService.getContainersConfiguration(null);
        if (configurations != null) {
            for (final ContainerConfiguration config : configurations) {
                containerNames.add(config.getName());
            }
        }
        return containerNames;
    }

    public List<ContainerInformations> getContainersInformations() throws MonitoringException {
        final List<ContainerInformations> cInfos = new ArrayList<ContainerInformations>();
        Set<ContainerConfiguration> configurations = null;
        configurations = this.topologyService.getContainersConfiguration(null);
        if (configurations != null) {
            for (final ContainerConfiguration config : configurations) {
                final ContainerInformations infos = new ContainerInformations();
                infos.setContainerId(config.getName());
                infos.setDaemonThreadCount(ManagementFactory.getThreadMXBean()
                        .getDaemonThreadCount());
                infos.setDescription(config.getDescription());
                infos.setHeapMemoryUsage(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
                        .getUsed());
                infos.setHost(config.getHost());
                infos.setJmxJNDIPort(Integer.toString(config.getJmxRMIConnectorPort()));
                infos.setObjectPendingFinalizationCount(ManagementFactory.getMemoryMXBean()
                        .getObjectPendingFinalizationCount());
                infos.setPeakThreadCount(ManagementFactory.getThreadMXBean().getPeakThreadCount());
                infos.setStatus(config.getState().toString());
                infos.setTcpPort(Integer.toString(config.getTCPPort()));
                infos.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
                infos.setTotalStartedThreadCount(ManagementFactory.getThreadMXBean()
                        .getTotalStartedThreadCount());
                infos.setUpTime(ManagementFactory.getRuntimeMXBean().getUptime());
                infos.setVersion(config.getDescription());
                cInfos.add(infos);
            }
        }
        return cInfos;
    }

    public String getDescriptionEndpoint(final String endpointName) throws MonitoringException {
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.registryService.getEndpoints();
        } catch (final RegistryException e) {
            throw new MonitoringException(e);
        }
        if (endpoints != null) {
            for (final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint : endpoints) {
                if (endpoint.getEndpointName().equals(endpointName)) {
                    Document doc = null;
                    try {
                        doc = this.registryService.getEndpointDescriptorForEndpoint(endpoint);
                    } catch (final RegistryException e) {
                        throw new MonitoringException(e);
                    }
                    if (doc != null) {
                        try {
                            return MonitoringUtil.parseToString(doc.cloneNode(true));
                        } catch (final TransformerFactoryConfigurationError e) {
                            throw new MonitoringException(e);
                        } catch (final TransformerException e) {
                            throw new MonitoringException(e);
                        }
                    }
                }
            }
        }
        return null;
    }
}
