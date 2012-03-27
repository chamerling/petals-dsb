/**
 * 
 */
package org.petalslink.dsb.kernel.rest.impl;

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
import org.ow2.petals.jbi.management.deployment.AtomicDeploymentService;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.service.RESTService;
import org.petalslink.dsb.kernel.rest.api.ServiceAssembly;
import org.petalslink.dsb.kernel.rest.api.beans.SA;
import org.petalslink.dsb.kernel.rest.api.beans.Status;

/**
 * @author chamerling
 * 
 */
@RESTService
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceAssembly.class) })
public class ServiceAssemblyServiceImpl implements ServiceAssembly {

    private static final Status OK = new Status("OK");

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "atomic-deployment", signature = AtomicDeploymentService.class)
    protected AtomicDeploymentService atomicDeploymentService;

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
     * @see
     * org.petalslink.dsb.kernel.rest.api.ServiceUnitService#start(java.lang
     * .String)
     */
    public Status start(String saName) {
        if (saName == null) {
            return new Status("Null SA name");
        }
        try {
            this.atomicDeploymentService.start(saName);
        } catch (PetalsException e) {
            return new Status(e.getMessage());
        }
        return OK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.rest.api.ServiceUnitService#stop(java.lang.
     * String)
     */
    public Status stop(String saName) {
        if (saName == null) {
            return new Status("Null SA name");
        }
        try {
            this.atomicDeploymentService.stop(saName);
        } catch (PetalsException e) {
            return new Status(e.getMessage());
        }
        return OK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.rest.api.ServiceUnitService#shutdown(java.lang
     * .String)
     */
    public Status shutdown(String saName) {
        if (saName == null) {
            return new Status("Null SA name");
        }
        try {
            this.atomicDeploymentService.shutdown(saName);
        } catch (PetalsException e) {
            return new Status(e.getMessage());
        }
        return OK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.rest.api.ServiceUnitService#status(java.lang
     * .String)
     */
    public Status status(String saName) {
        return new Status("TODO");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.rest.api.ServiceUnitService#undeploy(java.lang.String)
     */
    public Status undeploy(String saName) {
        return new Status("TODO");
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.rest.api.ServiceAssembly#all()
     */
    public List<SA> all() {
        List<SA> result = new ArrayList<SA>();
        SA a = new SA();
        a.setName("SA-1");
        a.setState("started");
        
        SA b = new SA();
        b.setName("SA-2");
        b.setState("stopped");
        
        result.add(a);
        result.add(b);
        
        return result;
    }

}
