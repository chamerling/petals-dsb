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
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.DSBConfigurationService;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = EmbeddedServiceBinder.class) })
public class EmbeddedServiceBinderImpl implements EmbeddedServiceBinder {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "dsb-configuration", signature = DSBConfigurationService.class)
    private DSBConfigurationService configurationService;

    @Requires(name = "binder-registry", signature = ServiceBinderRegistry.class)
    private ServiceBinderRegistry serviceBinderRegistry;

    /**
     * The unbound services ie the service which are not bound for some reasons
     * (component is not ready, etc etc). These services will be bound later by
     * a worker...
     */
    private Map<String, List<String>> unbound;

    private boolean firstCall;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.firstCall = true;
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.START, priority = 900)
    public synchronized void bindAll() {
        Map<String, List<String>> services = this.getServicesToBind();
        Map<String, List<String>> futureBind = new HashMap<String, List<String>>();
        // get all the services to bind from configuration file

        if ((services == null) || (services.size() == 0)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("No embedded service to bind");
            }
            return;
        }

        this.log.info("Let's bind services which have been defined from configuration file");

        // bind services
        for (String key : services.keySet()) {
            List<String> list = services.get(key);

            if (list == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("No service to bind for protocol '" + key + "'");
                }
            } else {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Getting service binder for '" + key + "'");
                }
                org.petalslink.dsb.kernel.management.binder.ServiceBinder binder = this.serviceBinderRegistry
                        .getServiceBinder(key);

                if (binder != null) {
                    for (String service : list) {
                        if ((service != null) && (service.trim().length() > 0)) {

                            // FIXME = for now we put the value in all the
                            // fields
                            // since the data comes from a map...

                            Map<String, Object> props = new HashMap<String, Object>();
                            props.put("wsdl", service.trim());
                            props.put("url", service.trim());
                            props.put("restURL", service.trim());
                            try {
                                binder.bind(props);
                            } catch (BinderException e) {
                                this.log.warning(e.getMessage());
                                // put for future bind...
                                if (futureBind.get(key) == null) {
                                    List<String> l = new ArrayList<String>();
                                    l.add(service);
                                    futureBind.put(key, l);
                                } else {
                                    futureBind.get(key).add(service);
                                }
                            }
                        }
                    }
                } else {
                    this.log.warning("No service binder has been found for protocol '" + key + "'");
                }
            }
        }
        this.unbound = futureBind;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Embedded services which have not be bound are : " + this.unbound);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getServicesToBind() {
        Map<String, List<String>> result = null;
        if (this.firstCall) {
            result = this.configurationService.getServices2BindAtStartup();
        } else {
            result = this.unbound;
        }
        // dummy synchro...
        this.firstCall = false;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Services to bind = " + result);
        }
        return result;
    }
}
