/**
 * 
 */
package org.petalslink.dsb.service.poller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author chamerling
 * 
 */
public class ServiceCallJob implements Job {

    public static final String POLLINGCONTEXT = "polling-context";

    public static final String COREJOB = "corejob";

    final Log logger = LogFactory.getLog(ServiceCallJob.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("It is time to call the service...");
        Object o = context.getJobDetail().getJobDataMap().get(POLLINGCONTEXT);
        if (o == null) {
            logger.warn("Can not find the polling context in the job context...");
            throw new JobExecutionException(
                    "Can not find the polling context in the job context...");
        }

        PollingContext pollingContext = null;
        if (o != null && o instanceof PollingContext) {
            pollingContext = (PollingContext) o;
        } else {
            throw new JobExecutionException("Bad polling context");
        }

        org.petalslink.dsb.service.poller.api.Job coreJob = pollingContext.getJob();
        if (coreJob == null) {
            throw new JobExecutionException("Can not find any core job to call in the context");
        }

        // let's call the real job
        try {
            coreJob.invoke(pollingContext);
        } catch (PollerException e) {
            throw new JobExecutionException("Some error occured during job invocation", e);
        }
    }
}
