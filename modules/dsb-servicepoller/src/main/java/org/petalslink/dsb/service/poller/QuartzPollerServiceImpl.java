/**
 * 
 */
package org.petalslink.dsb.service.poller;

import java.text.ParseException;
import java.util.UUID;

import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A poller service based on Quartz
 * 
 * @author chamerling
 * 
 */
public class QuartzPollerServiceImpl extends AbstractPollerServiceImpl {

    private Scheduler sched;

    private String jobId;

    private JobDetail job;

    CronTrigger trigger;

    /**
     * 
     * @param context
     * @param scheduler
     * @throws PollerException is the context is not well set
     */
    public QuartzPollerServiceImpl(PollingContext context, Scheduler scheduler)
            throws PollerException {
        super(context);
        this.sched = scheduler;

        jobId = UUID.randomUUID().toString();

        // create the quartz job
        try {
            job = newJob(ServiceCallJob.class).withIdentity(jobId, "group1").build();
            JobDataMap map = job.getJobDataMap();
            map.put(ServiceCallJob.POLLINGCONTEXT, context);
            trigger = newTrigger().withIdentity("trigger1", "group1")
                    .withSchedule(cronSchedule(context.getCron())).build();
        } catch (ParseException e) {
            throw new PollerException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new PollerException(e.getMessage());
        }
    }

    public void start() throws PollerException {
        try {
            java.util.Date ft = sched.scheduleJob(job, trigger);
            logger.info("JOB " + job.getKey() + " has been scheduled to run at: " + ft
                    + " and repeat based on expression: " + trigger.getCronExpression());
        } catch (SchedulerException e) {
            throw new PollerException(e.getMessage());
        }
    }

    public void stop() throws PollerException {
        try {
            boolean deleted = sched.deleteJob(this.job.getKey());
        } catch (SchedulerException e) {
            throw new PollerException(e.getMessage());
        }
    }

    public void pause() throws PollerException {
        if (job != null) {
            try {
                sched.pauseJob(job.getKey());
                logger.info("Job has been paused");
            } catch (SchedulerException e) {
                throw new PollerException(e.getMessage());
            }
        }
    }

    public void resume() throws PollerException {
        if (job != null) {
            try {
                sched.resumeJob(job.getKey());
                logger.info("Job has been resumed");
            } catch (SchedulerException e) {
                throw new PollerException(e.getMessage());
            }
        }
    }

    public String getId() {
        return jobId;
    }

}
