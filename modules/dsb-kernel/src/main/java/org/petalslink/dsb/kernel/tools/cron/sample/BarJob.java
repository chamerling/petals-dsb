/**
 * 
 */
package org.petalslink.dsb.kernel.tools.cron.sample;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.cron.Job;

/**
 * A sample job. We do not use Quartz scheduler to not introduce libraries
 * conflicts. For now simple period jobs are enough...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
public class BarJob {

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

    @Job(delay = 15L, period = 10L, timeUnit = TimeUnit.SECONDS)
    public void iAmACronJob() {
        Date d = new Date(System.currentTimeMillis());
        System.out.println("I am a BAR job, invoked at " + d.toString());
    }

}
