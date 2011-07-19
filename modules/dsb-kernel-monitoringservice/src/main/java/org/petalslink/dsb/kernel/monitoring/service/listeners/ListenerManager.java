/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service.listeners;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;

/**
 * Just a simple ack to be removed when the DSB will be able to detect listeners
 * in the framework. FIXME : Must be included in the framework to discover new
 * listener automatically based on some annotations like for JAXWS.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
public class ListenerManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "listener", signature = RegistryListener.class)
    private RegistryListener listener;

    @Requires(name = "registrylistenermanager", signature = RegistryListenerManager.class)
    private RegistryListenerManager registryListenerManager;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /**
     * Will be called by the framework
     */
    @LifeCycleListener(phase = Phase.START)
    public void registerListeners() {
        System.out.println("REGISTER LISTENER FOR MONITORING!!!");
        try {
            this.registryListenerManager.add(listener);
        } catch (DSBException e) {
            e.printStackTrace();
        }
    }
}
