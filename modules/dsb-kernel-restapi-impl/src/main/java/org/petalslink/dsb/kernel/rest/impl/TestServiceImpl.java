/**
 * 
 */
package org.petalslink.dsb.kernel.rest.impl;

import javax.ws.rs.Path;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.service.RESTService;
import org.petalslink.dsb.kernel.rest.api.TestService;
import org.petalslink.dsb.kernel.rest.api.beans.Status;

/**
 * @author chamerling
 * 
 */
@RESTService
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = TestService.class) })
public class TestServiceImpl implements TestService {

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
     * @see org.petalslink.dsb.kernel.rest.api.TestService#foo()
     */
    public Status foo() {
        System.out.println("REST FOO CALL");
        return new Status("foo");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.rest.api.TestService#bar()
     */
    public Status bar() {
        System.out.println("REST BAR CALL");
        return new Status("bar");
    }

}
