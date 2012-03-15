/**
 * 
 */
package org.petalslink.dsb.kernel.tools.cron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.kernel.api.tools.cron.CronRegistry;
import org.petalslink.dsb.kernel.api.tools.cron.Job;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = CronRegistry.class) })
public class CronRegistryImpl implements CronRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, Job> jobs;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.jobs = new HashMap<String, Job>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.tools.cron.CronRegistry#store(org.petalslink
     * .dsb.kernel.api.tools.cron.Job)
     */
    public void store(Job job) {
        if (log.isDebugEnabled()) {
            log.debug("Storing job " + job);
        }
        if (job != null && job.getId() != null) {
            this.jobs.put(job.getId(), job);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.tools.cron.CronRegistry#get(java.lang.String
     * )
     */
    public Job get(String id) {
        if (log.isDebugEnabled()) {
            log.debug("Getting job " + id);
        }
        if (id != null) {
            return this.jobs.get(id);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.tools.cron.CronRegistry#get()
     */
    public List<Job> get() {
        if (log.isDebugEnabled()) {
            this.log.debug("Get all jobs");
        }
        return new ArrayList<Job>(this.jobs.values());
    }

}
