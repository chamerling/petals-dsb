/**
 * 
 */
package org.petalslink.dsb.kernel.tools.cron;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.fractal.utils.FractalHelper;
import org.petalslink.dsb.kernel.api.tools.cron.CronScanner;
import org.petalslink.dsb.kernel.api.tools.cron.Job;

/**
 * Scan the framework to find all annotated method
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = CronScanner.class) })
public class CronScannerImpl implements CronScanner {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

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
     * @see org.petalslink.dsb.kernel.api.tools.cron.CronScanner#scan()
     */
    public List<Job> scan() {
        List<Job> result = new ArrayList<Job>();
        Component root = FractalHelper.getRootComponent(component);
        if (root != null) {
            List<Component> components = FractalHelper.getAllComponentsWithMethodAnnotation(
                    FractalHelper.getContentController(root),
                    org.petalslink.dsb.annotations.cron.Job.class);
            if (components != null) {
                for (Component component : components) {
                    String componentName = FractalHelper.getName(component);

                    // get the topic names
                    Object o = FractalHelper.getContent(component);
                    // get all the methods which are annotated with Notify
                    if (o != null) {
                        for (Method m : o.getClass().getMethods()) {
                            if (m.isAnnotationPresent(org.petalslink.dsb.annotations.cron.Job.class)) {
                                log.debug(String.format(
                                        "Found cron annotation on method %s for component %s",
                                        m.getName(), componentName));

                                // just accept one argument method which is a
                                // dom document...
                                org.petalslink.dsb.annotations.cron.Job j = m
                                        .getAnnotation(org.petalslink.dsb.annotations.cron.Job.class);
                                Job target = new Job();
                                target.setMethod(m);
                                target.setTarget(o);
                                target.setDelay(j.delay());
                                target.setPeriod(j.period());
                                target.setUnit(j.timeUnit());
                                target.setId(j.id());
                                result.add(target);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
