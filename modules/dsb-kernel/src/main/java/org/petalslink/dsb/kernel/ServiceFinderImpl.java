/**
 * 
 */
package org.petalslink.dsb.kernel;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.server.FractalHelper;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.ServiceFinder;

/**
 * A service used to find other services...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceFinder.class) })
public class ServiceFinderImpl implements ServiceFinder {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }
    
    public <T> T get(Class<T> t) throws DSBException {
        // TODO get component name from class name
        return null;
    }

    public <T> T get(Class<T> t, String componentName) throws DSBException {
        return this.get(t, componentName, null);
    }

    public <T> T get(Class<T> t, String componentName, String serviceName) throws DSBException {
        T result = null;
        try {
            SuperController sc = Fractal.getSuperController(this.component);
            Component parentcontainer = sc.getFcSuperComponents()[0];
            ContentController cc = Fractal.getContentController(parentcontainer);
            Component c = FractalHelper.getRecursiveComponentByName(cc, componentName);
            if (c != null) {
                String name = (serviceName != null) ? serviceName : DEFAULT_SERVICE_NAME;
                Object o = c.getFcInterface(name);
                if (o != null) {
                    try {
                        result = t.cast(o);
                    } catch (ClassCastException e) {
                        throw new DSBException(e.getMessage());
                    }
                }
            } else {
                throw new DSBException("No such component : " + componentName);
            }
        } catch (NoSuchInterfaceException e) {
            throw new DSBException(e.getMessage());
        }
        return result;
    }
}
