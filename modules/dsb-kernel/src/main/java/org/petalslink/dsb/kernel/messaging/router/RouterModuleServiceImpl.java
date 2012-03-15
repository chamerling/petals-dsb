/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.ws.api.RouterModule;
import org.petalslink.dsb.ws.api.RouterModuleService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RouterModuleService.class) })
public class RouterModuleServiceImpl implements RouterModuleService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "router-module-manager", signature = RouterModuleManager.class)
    protected RouterModuleManager routerModuleManager;

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
     * @see org.petalslink.dsb.ws.api.RouterModuleService#getSenders()
     */
    public List<RouterModule> getSenders() {
        List<SenderModule> modules = this.routerModuleManager.getSenders();
        List<RouterModule> result = new ArrayList<RouterModule>();
        if (modules != null) {
            for (SenderModule module : modules) {
                RouterModule rm = new RouterModule();
                rm.setDescription(module.getDescription());
                rm.setName(module.getName());
                rm.setState(this.routerModuleManager.getSenderState(module.getName()));
                result.add(rm);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.ws.api.RouterModuleService#getReceivers()
     */
    public List<RouterModule> getReceivers() {
        List<ReceiverModule> modules = this.routerModuleManager.getReceivers();
        List<RouterModule> result = new ArrayList<RouterModule>();
        if (modules != null) {
            for (ReceiverModule module : modules) {
                RouterModule rm = new RouterModule();
                rm.setDescription(module.getDescription());
                rm.setName(module.getName());
                rm.setState(this.routerModuleManager.getReceiverState(module.getName()));
                result.add(rm);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.RouterModuleService#setSenderState(java.lang
     * .String, boolean)
     */
    public void setSenderState(String name, boolean onoff) {
        this.routerModuleManager.setSenderState(name, onoff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.RouterModuleService#setReceiverState(java.lang
     * .String, boolean)
     */
    public void setReceiverState(String name, boolean onoff) {
        this.routerModuleManager.setReceiverState(name, onoff);
    }
}
