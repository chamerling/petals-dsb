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
package org.ow2.petals.binding.restproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ow2.petals.binding.restproxy.in.RESTEngineContext;
import org.ow2.petals.binding.restproxy.in.RESTEngineContext.Consume;
import org.ow2.petals.component.framework.ComponentInformation;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.su.BindingComponentServiceUnitManager;
import org.ow2.petals.component.framework.su.ServiceUnitDataHandler;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SUManager extends BindingComponentServiceUnitManager {

    private final Map<String, Jbi> cache;

    /**
     * @param bindingComponent
     */
    public SUManager(Component bindingComponent) {
        super(bindingComponent);
        this.cache = new HashMap<String, Jbi>();
    }

    /**
     * @return the componentInformation
     */
    public ComponentInformation getComponentInformation() {
        return ((PetalsBindingComponent) this.component).getPlugin(ComponentInformation.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDeploy(String serviceUnitName, String suRootPath, Jbi jbiDescriptor)
            throws PEtALSCDKException {
        List<Consumes> consumes = jbiDescriptor.getServices().getConsumes();
        for (Consumes consumes2 : consumes) {
            this.deployConsume(consumes2);
        }

        List<Provides> provides = jbiDescriptor.getServices().getProvides();
        for (Provides provides2 : provides) {
            this.deployProvide(provides2);
        }

        this.cache.put(serviceUnitName, jbiDescriptor);
    }

    /**
     * @param provides2
     */
    private void deployProvide(Provides provides2) {
        ServiceUnitDataHandler dh = this.getSUDataHandlerForProvides(provides2);
        ConfigurationExtensions extensions = dh.getConfigurationExtensions(provides2);
        String restService = extensions.get("address");
        if (restService != null) {
            this.addToConsume(restService);
        }
    }

    /**
     * @param consumes2
     */
    private void deployConsume(Consumes consumes2) {
        Engine engine = EngineFactory.getEngine();

        RESTEngineContext context = engine.getComponent(RESTEngineContext.class);
        if (context == null) {
            return;
        }

        Consume consume = context.newConsume();
        consume.setEndpointName(consumes2.getEndpointName());
        consume.setInterfaceName(consumes2.getInterfaceName());
        consume.setServiceName(consumes2.getServiceName());
        ServiceUnitDataHandler dh = this.getSUDataHandlerForConsumes(consumes2);
        if (dh != null) {
            ConfigurationExtensions extensions = dh.getConfigurationExtensions(consumes2);
            String restService = extensions.get("address");
            if (restService != null) {
                Engine e = EngineFactory.getEngine();
                RESTEngineContext restEngineContext = e.getComponent(RESTEngineContext.class);
                String serviceURL = "http://$HOST:" + restEngineContext.getPort()
                        + restEngineContext.getServicePath() + "/" + restService;
                this.addToExpose(serviceURL);
                context.getRestConsumers().put(restService, consume);
            }
        }
    }

    /**
     * @param restService
     */
    private void addToExpose(String restService) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> exposed = this.getComponentInformation().getExposedServices();
        if (exposed != null) {
            exposed.add(restService);
        }
    }

    private void removeFromExpose(String restService) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> exposed = this.getComponentInformation().getExposedServices();
        if (exposed != null) {
            exposed.remove(restService);
        }
    }

    private void addToConsume(String restService) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> services = this.getComponentInformation().getConsumedServices();
        if (services != null) {
            services.add(restService);
        }
    }

    private void removeFromConsume(String restService) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> services = this.getComponentInformation().getConsumedServices();
        if (services != null) {
            services.remove(restService);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInit(String serviceUnitName, String suRootPath) throws PEtALSCDKException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doShutdown(String serviceUnitName) throws PEtALSCDKException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart(String serviceUnitName) throws PEtALSCDKException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(String serviceUnitName) throws PEtALSCDKException {
        Jbi jbiDescriptor = this.cache.get(serviceUnitName);
        if (jbiDescriptor == null) {
            return;
        }

        List<Consumes> consumes = jbiDescriptor.getServices().getConsumes();
        for (Consumes consumes2 : consumes) {
            this.undeployConsume(consumes2);
        }
    }

    /**
     * @param consumes2
     */
    private void undeployConsume(Consumes consumes2) {
        this.logger.fine("Undeploying consume = " + consumes2);
        Engine engine = EngineFactory.getEngine();

        RESTEngineContext context = engine.getComponent(RESTEngineContext.class);
        if (context == null) {
            return;
        }
        ServiceUnitDataHandler dh = this.getSUDataHandlerForConsumes(consumes2);
        if (dh != null) {
            ConfigurationExtensions extensions = dh.getConfigurationExtensions(consumes2);
            String restService = extensions.get("address");
            if (restService != null) {
                context.getRestConsumers().remove(restService);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndeploy(String serviceUnitName) throws PEtALSCDKException {
    }

}
