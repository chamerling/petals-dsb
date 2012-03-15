/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.service.WebService;
import org.petalslink.dsb.fractal.utils.FractalHelper;
import org.petalslink.dsb.soap.api.Service;

/**
 * Gets all the services from the framework...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceRegistry.class) })
public class GlobalServiceRegistryImpl implements ServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * Used to retrieve the current component. It is automatically injected by
     * Fractal.
     */
    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

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
        // get all the components which are annotated with the right
        // annotation and which are implementing the Service interface
        List<Service> result = new ArrayList<Service>();
        List<Component> components = FractalHelper.getAllComponentsWithAnnotation(
                FractalHelper.getContentController(FractalHelper.getRootComponent(component)),
                WebService.class);
        for (Component component : components) {
            Object o = FractalHelper.getContent(component);
            if (o instanceof Service) {
                result.add((Service) o);
            }
        }
        return result;
    }
}
