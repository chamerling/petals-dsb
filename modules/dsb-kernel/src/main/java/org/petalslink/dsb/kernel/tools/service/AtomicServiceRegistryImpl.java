/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Cardinality;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.soap.api.Service;

/**
 * This service registry is used with services which are directly bound to it by
 * configuration. There is no introspection like in
 * {@link GlobalServiceRegistryImpl}
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceRegistry.class) })
public class AtomicServiceRegistryImpl implements ServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "service-provider-", signature = Service.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    protected Hashtable<String, Object> providers = new Hashtable<String, Object>();

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.tools.service.ServiceRegistry#getServices()
     */
    public List<Service> getServices() {
        List<Service> result = new ArrayList<Service>();
        if (providers != null) {
            for (Object o : providers.values()) {
                if (o != null && o instanceof Service) {
                    result.add((Service) o);
                }
            }
        }
        return result;
    }

}
