/**
 * 
 */
package org.petalslink.dsb.kernel.registry.sample;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;

/**
 * A registry listener sample, just add the registry listener annotation and it
 * will be detected by the DSB at startup.
 * 
 * @author chamerling
 * 
 */
@org.petalslink.dsb.annotations.registry.RegistryListener
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListener.class) })
public class RegistryListenerSample implements RegistryListener {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
     * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onRegister(org
     * .petalslink.dsb.api.ServiceEndpoint)
     */
    public void onRegister(ServiceEndpoint endpoint) throws DSBException {
        if (log.isDebugEnabled()) {
            log.debug("A new endpoint has been registered : " + endpoint);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.messaging.RegistryListener#onUnregister
     * (org.petalslink.dsb.api.ServiceEndpoint)
     */
    public void onUnregister(ServiceEndpoint endpoint) throws DSBException {
        if (log.isDebugEnabled()) {
            log.debug("An endpoint has been unregistered : " + endpoint);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListener#getName()
     */
    public String getName() {
        return "RegistryListenerSample";
    }

}
