/**
 * 
 */
package org.petalslink.dsb.service.poller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author chamerling
 * 
 */
public class QuartzPollingManagerImpl implements PollingManager {

    final Log logger = LogFactory.getLog(QuartzPollingManagerImpl.class);

    private SchedulerFactory sf;

    private Scheduler scheduler;

    private boolean started = false;

    public QuartzPollingManagerImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.poller.api.PollingManager#getPollerService
     * (org.petalslink.dsb.service.poller.api.PollingContext)
     */
    public PollerService getPollerService(PollingContext context) throws PollerException {
        if (!started) {
            return null;
        }
        return new QuartzPollerServiceImpl(context, scheduler);
    }

    public synchronized void init() {
        if (started) {
            return;
        }

        sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        if (started) {
            return;
        }
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        started = true;
    }

    public synchronized void stop() {
        if (!started) {
            return;
        }
        try {
            this.scheduler.shutdown();
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        started = false;
    }

}
