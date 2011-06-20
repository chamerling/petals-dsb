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
package org.petalslink.dsb.kernel.ws;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.tools.ws.KernelWebService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.management.component.ComponentInformationService;
import org.petalslink.dsb.ws.api.ServiceInformation;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = KernelWebService.class),
        @Interface(name = "webservice", signature = ServiceInformation.class) })
public class ServiceInformationImpl implements ServiceInformation, KernelWebService {

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "component-information", signature = ComponentInformationService.class)
    private ComponentInformationService componentInformationService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExposedWebServices() {
        Set<String> services = this.componentInformationService
                .getExposedServiceURLs("petals-bc-soap");
        Set<String> result = new HashSet<String>(services.size());
        for (String string : services) {
            result.add(string.replaceAll("\\$HOST", this.configurationService
                    .getContainerConfiguration().getHost()));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public org.objectweb.fractal.api.Component getComponent() {
        return this.component;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getConsumedRESTServices() {
        Set<String> restServices = this.componentInformationService
                .getConsumedServiceURLs("petals-bc-rest");
        Set<String> result = new HashSet<String>(restServices.size());
        for (String string : restServices) {
            result.add(string.replaceAll("\\$HOST", this.configurationService
                    .getContainerConfiguration().getHost()));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getConsumedWebServices() {
        Set<String> restServices = this.componentInformationService
                .getConsumedServiceURLs("petals-bc-soap");
        Set<String> result = new HashSet<String>(restServices.size());
        for (String string : restServices) {
            result.add(string.replaceAll("\\$HOST", this.configurationService
                    .getContainerConfiguration().getHost()));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExposedRESTServices() {
        Set<String> restServices = this.componentInformationService
                .getExposedServiceURLs("petals-bc-rest");
        Set<String> result = new HashSet<String>(restServices.size());
        for (String string : restServices) {
            result.add(string.replaceAll("\\$HOST", this.configurationService
                    .getContainerConfiguration().getHost()));
        }
        return result;
    }
}
