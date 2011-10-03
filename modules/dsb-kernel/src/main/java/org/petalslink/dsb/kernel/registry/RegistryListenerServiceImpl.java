/**
 * 
 */
package org.petalslink.dsb.kernel.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;
import org.petalslink.dsb.ws.api.RegistryListenerService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListenerService.class) })
public class RegistryListenerServiceImpl implements RegistryListenerService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "registry-listener-manager", signature = RegistryListenerManager.class)
    private RegistryListenerManager registryListenerManager;

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
     * @see org.petalslink.dsb.ws.api.RegistryListenerService#getListeners()
     */
    public Set<String> getListeners() {
        Set<String> result = new HashSet<String>();
        List<RegistryListener> listeners = this.registryListenerManager.getList();
        if (listeners != null) {
            for (RegistryListener registryListener : listeners) {
                result.add(registryListener.getName());
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.RegistryListenerService#getState(java.lang.
     * String)
     */
    public boolean getState(String name) {
        return this.registryListenerManager.getState(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.RegistryListenerService#setState(java.lang.
     * String, boolean)
     */
    public void setState(String name, boolean state) {
        this.registryListenerManager.setState(name, state);
    }

}
