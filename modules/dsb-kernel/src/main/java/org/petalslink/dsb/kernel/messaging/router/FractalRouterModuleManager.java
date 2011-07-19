/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RouterModuleManager.class) })
public class FractalRouterModuleManager implements RouterModuleManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private RouterModuleManager delagate;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.delagate = new RouterModuleManagerImpl();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.SenderModule)
     */
    public void add(SenderModule module) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding sender module '%s'", module.getName()));
        }
        this.delagate.add(module);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.ReceiverModule)
     */
    public void add(ReceiverModule module) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding receiver module '%s'", module.getName()));
        }
        this.delagate.add(module);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#setState
     * (java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Set module state '%s' to %b", name, onoff));
        }
        this.delagate.setState(name, onoff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getState
     * (java.lang.String)
     */
    public boolean getState(String name) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get module state '%s'", name));
        }
        return this.delagate.getState(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenders
     * ()
     */
    public List<SenderModule> getSenders() {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get senders modules"));
        }
        return this.delagate.getSenders();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getReceivers
     * ()
     */
    public List<ReceiverModule> getReceivers() {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get receivers modules"));
        }
        return this.delagate.getReceivers();
    }

}
