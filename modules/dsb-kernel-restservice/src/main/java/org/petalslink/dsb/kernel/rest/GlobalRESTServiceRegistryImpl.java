/**
 * 
 */
package org.petalslink.dsb.kernel.rest;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RESTServiceRegistry.class) })
public class GlobalRESTServiceRegistryImpl implements RESTServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    Set<RESTServiceInformationBean> webservices;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        log.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.rest.RESTServiceRegistry#load()
     */
    public void load() {
        Set<RESTServiceInformationBean> beans = FractalHelper.getAllRESTServices(this.component);
        if (webservices == null) {
            webservices = new HashSet<RESTServiceInformationBean>();
        }
        webservices.addAll(beans);
        if (log.isDebugEnabled()) {
            int i = 0;
            log.debug("Found the following REST services in the framework :");
            for (RESTServiceInformationBean bean : webservices) {
                log.debug((i++) + " : " + bean);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.rest.RESTServiceRegistry#getRESTServices()
     */
    public Set<RESTServiceInformationBean> getRESTServices() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getRESTServices");
        }
        if (this.webservices == null) {
            this.webservices = new HashSet<RESTServiceInformationBean>();
            this.load();
        }
        return new HashSet<RESTServiceInformationBean>(this.webservices);
    }

}
