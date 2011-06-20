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

import java.util.HashMap;
import java.util.Iterator;
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
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.kernel.api.service.ServiceEndpoint;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.Constants;
import org.petalslink.dsb.kernel.api.management.binder.BinderException;
import org.petalslink.dsb.kernel.api.management.binder.NewServiceExposer;
import org.petalslink.dsb.kernel.api.management.binder.ServiceExposer;
import org.petalslink.dsb.kernel.api.management.binder.ServiceExposerRegistry;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NewServiceExposer.class) })
public class NewServiceExposerImpl implements NewServiceExposer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "registry", signature = EndpointRegistry.class)
    protected EndpointRegistry endpointRegistry;

    @Requires(name = "exposer-registry", signature = ServiceExposerRegistry.class)
    protected ServiceExposerRegistry serviceExposerRegistry;

    @Requires(name = "adminService", signature = AdminService.class)
    private AdminService adminService;

    private Map<String, ServiceEndpoint> exposedEndpoints;

    private final Object object = new Object();

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.exposedEndpoints = new HashMap<String, ServiceEndpoint>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void expose() {
        this.log.debug("Got a #expose call, waiting previous call to complete...");
        synchronized (this.object) {
            this.log.debug("Let's expose new endpoints!");
            // wait if another thread is already calling this...
            try {

                // TODO : Do not get all the endpoints but just endpoints which
                // have been exposed with management API
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = this.endpointRegistry.getEndpoints();

                for (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint : endpoints) {
                    if (this.isNew(serviceEndpoint) && this.isPlatformService(serviceEndpoint)) {
                        this.bind(serviceEndpoint);
                    } else {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Endpoint " + serviceEndpoint.getEndpointName()
                                    + " is not new or is not a platform service");
                        }
                    }
                }

            } catch (RegistryException e) {
                this.log.warning(e.getMessage());
            }
        }
    }

    /**
     * @param serviceEndpoint
     * @return
     */
    protected boolean isPlatformService(ServiceEndpoint serviceEndpoint) {
        return true;
        // return (serviceEndpoint != null)
        // && ((serviceEndpoint.getEndpointName() != null) &&
        // (serviceEndpoint
        // .getEndpointName().startsWith(Constants.SOAP_PLATFORM_ENDPOINT_PREFIX)
        // || serviceEndpoint
        // .getEndpointName().startsWith(Constants.REST_PLATFORM_ENDPOINT_PREFIX)));
    }

    /**
     * @param serviceEndpoint
     * @return
     */
    public boolean isNew(ServiceEndpoint serviceEndpoint) {
        boolean result = true;

        boolean inCache = (this.exposedEndpoints.get(this.getKey(serviceEndpoint)) != null);

        if (!inCache) {
            // look at the service unit life cycles and to the consumes to see
            // if
            // the service endpoint has already been exposed...
            Map<String, ServiceAssemblyLifeCycle> saLc = this.adminService.getServiceAssemblies();
            for (ServiceAssemblyLifeCycle serviceAssemblyLifeCycle : saLc.values()) {
                List<ServiceUnitLifeCycle> sus = serviceAssemblyLifeCycle
                        .getServiceUnitLifeCycles();
                Iterator<ServiceUnitLifeCycle> iter = sus.iterator();
                boolean found = false;
                while (iter.hasNext() && !found) {
                    ServiceUnitLifeCycle su = iter.next();
                    Jbi jbi = su.getServiceUnitDescriptor();
                    List<org.ow2.petals.jbi.descriptor.original.generated.Consumes> consumes = jbi
                            .getServices().getConsumes();
                    if ((consumes != null) && (consumes.size() > 0)) {
                        Iterator<org.ow2.petals.jbi.descriptor.original.generated.Consumes> consumesIter = consumes
                                .iterator();
                        while (consumesIter.hasNext() && !found) {

                            org.ow2.petals.jbi.descriptor.original.generated.Consumes consumesDescriptor = consumesIter
                                    .next();
                            found = consumesDescriptor.getEndpointName().equals(
                                    serviceEndpoint.getEndpointName())
                                    && consumesDescriptor.getServiceName().equals(
                                            serviceEndpoint.getServiceName());
                        }
                    }
                }

                if (found) {
                    this.exposedEndpoints.put(this.getKey(serviceEndpoint), serviceEndpoint);
                    result = false;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    private String getKey(ServiceEndpoint ep) {
        StringBuffer sb = new StringBuffer("EP:");
        sb.append(ep.toString());
        sb.append(ep.getLocation().toString());
        if (this.log.isDebugEnabled()) {
            this.log.debug("Service endpoint key is : " + sb.toString());
        }
        return sb.toString();
    }

    /**
     * TODO : Maybe we van delegate this to a new thread pool...
     * 
     * @param serviceEndpoint
     */
    public void bind(ServiceEndpoint serviceEndpoint) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Let's bind the service endpoint " + serviceEndpoint);
        }

        if (serviceEndpoint == null) {
            this.log.warning("Service endpoint is null and will not be exposed!");
            return;
        }

        // TODO : look at the endpoint values, we will just bind it if the
        // endpoint is
        // a platform service

        // TODO : get the list of protocols the service needs to be exposed in!
        // Map<String, String> map = serviceEndpoint.getProperties();

        String protocolName = Constants.SOAP_SERVICE_EXPOSER;
        if ((serviceEndpoint.getEndpointName() != null)
                && serviceEndpoint.getEndpointName().startsWith(
                        Constants.REST_PLATFORM_ENDPOINT_PREFIX)) {
            protocolName = Constants.REST_SERVICE_EXPOSER;
        }
        // } else if ((serviceEndpoint.getEndpointName() != null)
        // && serviceEndpoint.getEndpointName().startsWith(
        // Constants.SOAP_PLATFORM_ENDPOINT_PREFIX)) {
        // protocolName = Constants.SOAP_SERVICE_EXPOSER;
        // }

        if (protocolName == null) {
            if (this.log.isDebugEnabled()) {
                this.log
                        .debug("This endpoint '"
                                + serviceEndpoint.getEndpointName()
                                + "' can not be exposed since it has not be recognized as platform service");
            }
            return;
        }

        ServiceExposer exposer = this.serviceExposerRegistry.getServiceExposer(protocolName);
        if (exposer != null) {
            // let's expose !
            try {
                org.petalslink.dsb.ws.api.ServiceEndpoint ep = new org.petalslink.dsb.ws.api.ServiceEndpoint();
                ep.setEndpoint(serviceEndpoint.getEndpointName());
                if (serviceEndpoint.getInterfacesName() != null && serviceEndpoint.getInterfacesName().size() > 0) {
                    ep.setItf(serviceEndpoint.getInterfacesName().get(0));
                }
                ep.setService(serviceEndpoint.getServiceName());
                exposer.expose(ep);
                this.exposedEndpoints.put(this.getKey(serviceEndpoint), serviceEndpoint);

            } catch (BinderException e) {
                this.log.warning("Problem while binding : " + e.getMessage());
            }
        } else {
            this.log.warning("No service exposer has been found for protocol : " + protocolName);
        }
    }

}
