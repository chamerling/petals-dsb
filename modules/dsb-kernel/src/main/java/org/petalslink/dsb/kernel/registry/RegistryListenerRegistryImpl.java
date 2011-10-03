/**
 * 
 */
package org.petalslink.dsb.kernel.registry;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
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
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.fractal.utils.FractalHelper;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerRegistry;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListenerRegistry.class) })
public class RegistryListenerRegistryImpl implements RegistryListenerRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    /**
     * NOTE : We do it this way and not the other one since there is an issue
     * when binding the registry listener registry in the endpoint registry...
     */
    @Requires(name = "registry-listener-manager", signature = RegistryListenerManager.class)
    protected RegistryListenerManager registryListenerManager;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        System.out.println("INIT " + this.getClass().getCanonicalName());
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /**
     * 
     */
    @LifeCycleListener(priority = 10000)
    public void initialize() {
        List<org.petalslink.dsb.kernel.api.messaging.RegistryListener> listeners = getListeners();

        if (listeners != null) {
            for (org.petalslink.dsb.kernel.api.messaging.RegistryListener registryListener : listeners) {
                try {
                    registryListenerManager.add(registryListener);
                } catch (DSBException e) {
                    final String message = "Error while adding registry listener";
                    if (this.log.isDebugEnabled()) {
                        this.log.error(message, e);
                    } else {
                        this.log.debug(message);
                    }
                }
            }
        }

    }

    public List<RegistryListener> getListeners() {
        // get all the components which are annotated with the right
        // annotation and which are implementing the Service interface
        List<RegistryListener> result = new ArrayList<RegistryListener>();
        List<Component> components = FractalHelper.getAllComponentsWithAnnotation(
                FractalHelper.getContentController(FractalHelper.getRootComponent(component)),
                org.petalslink.dsb.annotations.registry.RegistryListener.class);
        for (Component component : components) {
            Object o = FractalHelper.getContent(component);
            if (o instanceof RegistryListener) {
                RegistryListener listener = (RegistryListener) o;
                System.out.println(String.format(
                        "++++ Got a registry listener detected from the framework : %s",
                        listener.getName()));
                if (this.log.isDebugEnabled()) {
                    this.log.debug(String.format(
                            "Got a registry listener detected from the framework : %s",
                            listener.getName()));
                }
                result.add(listener);
            }
        }
        return result;
    }
}
