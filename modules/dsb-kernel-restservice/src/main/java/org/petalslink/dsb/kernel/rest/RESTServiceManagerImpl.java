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
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.WebServiceException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RESTServiceManager.class) })
public class RESTServiceManagerImpl implements RESTServiceManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "restregistry", signature = RESTServiceRegistry.class)
    protected RESTServiceRegistry restServiceRegistry;

    @Requires(name = "restexposer", signature = RESTServiceExposer.class)
    protected RESTServiceExposer restExposer;

    private boolean exposed;

    private HashSet<RESTServiceInformationBean> services;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        log.start();
        this.services = new HashSet<RESTServiceInformationBean>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        log.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.rest.RESTManager#exposeAll()
     */
    @LifeCycleListener(phase = Phase.START)
    public void exposeAll() throws WebServiceException {
        if (!this.exposed) {
            // get the services from the registry
            Set<RESTServiceInformationBean> set = this.restServiceRegistry.getRESTServices();
            // TODO filter things that we do not want to expose from
            // configuration
            // file
            Set<RESTServiceInformationBean> subset = set;
            Set<RESTServiceInformationBean> exposed = this.restExposer.expose(subset);
            if (exposed != null) {
                this.services.addAll(exposed);
            }
            this.exposed = true;
        } else {
            throw new WebServiceException("REST Services already exposed");
        }
    }
}
