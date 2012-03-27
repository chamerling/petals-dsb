/**
 * 
 */
package org.petalslink.dsb.kernel.tools.cron;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.tools.cron.CronManager;
import org.petalslink.dsb.kernel.api.tools.cron.CronRegistry;
import org.petalslink.dsb.kernel.api.tools.cron.CronScanner;
import org.petalslink.dsb.kernel.api.tools.cron.Job;
import org.petalslink.dsb.ws.api.cron.CronJobBean;
import org.petalslink.dsb.ws.api.cron.CronJobService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = CronManager.class),
        @Interface(name = "webservice", signature = CronJobService.class) })
public class CronManagerImpl implements CronManager, CronJobService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private ScheduledExecutorService scheduler;

    boolean initialized = false;

    private Map<String, ScheduledFuture<?>> jobs;

    /**
     * This is because we do not handle multiple interfaces in components so the
     * lifecycle listener find two components and then call lifecycle listener
     * stuff two times...
     */
    private boolean done = false;

    @Requires(name = "cron-registry", signature = CronRegistry.class)
    protected CronRegistry registry;

    @Requires(name = "cron-scanner", signature = CronScanner.class)
    protected CronScanner scanner;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.jobs = new HashMap<String, ScheduledFuture<?>>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void startup() {
        if (done) {
            if (log.isDebugEnabled()) {
                log.debug("Already done");
            }
            return;
        }
        
        done = true;
        
        // get all jobs and publish all...
        List<Job> jobs = this.scanner.scan();
        this.initialize(jobs.size());

        for (Job job : jobs) {
            try {
                this.startJob(job);
            } catch (DSBException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    @LifeCycleListener(phase = Phase.STOP, priority = 0)
    public void shutdown() {
        // get all the registered jobs, and kill them!
        List<Job> jobs = this.registry.get();
        for (Job job : jobs) {
            try {
                stopJob(job);
            } catch (DSBException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.tools.cron.CronManager#initialize(int)
     */
    public void initialize(int size) {
        this.scheduler = Executors.newScheduledThreadPool(size);
        this.initialized = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.tools.cron.CronManager#startJob(org.petalslink
     * .dsb.kernel.api.tools.cron.Job)
     */
    public String startJob(final Job job) throws DSBException {
        if (log.isDebugEnabled()) {
            log.debug("Starting job " + job);
        }

        if (!initialized) {
            throw new DSBException("Not initialized");
        }

        // let's create a runnable from the job...
        Runnable runnable = new Runnable() {
            public void run() {
                Method m = job.getMethod();
                Object o = job.getTarget();

                if (o != null && m != null) {
                    try {
                        log.debug("Invoking cron method " + o.getClass() + " - " + m.getName());
                        m.invoke(o, (Object[]) null);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        final ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(runnable, job.getDelay(),
                job.getPeriod(), job.getUnit());

        String jobId = "job-" + UUID.randomUUID().toString();
        job.setId(jobId);
        this.jobs.put(jobId, handle);
        this.registry.store(job);

        return jobId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.tools.cron.CronManager#stopJob(org.petalslink
     * .dsb.kernel.api.tools.cron.Job)
     */
    public void stopJob(Job job) throws DSBException {
        if (log.isDebugEnabled()) {
            log.debug("Stopping job " + job);
        }

        if (!initialized) {
            throw new DSBException("Not initialized");
        }

        if (this.jobs.get(job.getId()) == null) {
            throw new DSBException("No such job '%s'", job.getId());
        }

        ScheduledFuture<?> handle = this.jobs.remove(job.getId());
        if (handle != null) {
            handle.cancel(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.ws.api.cron.CronJobService#get()
     */
    public List<CronJobBean> get() {
        List<Job> jobs = this.registry.get();
        List<CronJobBean> beans = new ArrayList<CronJobBean>(jobs.size());
        for (Job job : jobs) {
            CronJobBean bean = new CronJobBean();
            bean.setDelay(job.getDelay());
            bean.setId(job.getId());
            bean.setMethod(job.getMethod().getName());
            bean.setPeriod(job.getPeriod());
            bean.setTarget(job.getTarget().getClass().getName());
            bean.setUnit(job.getUnit().toString());
            beans.add(bean);
        }
        return beans;
    }
}
