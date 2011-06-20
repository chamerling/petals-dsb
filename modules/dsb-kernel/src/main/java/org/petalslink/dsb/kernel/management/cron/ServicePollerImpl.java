/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.management.cron;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.api.DSBConfigurationService;
import org.petalslink.dsb.kernel.api.management.binder.NewServiceExposer;
import org.petalslink.dsb.kernel.api.management.cron.ServicePoller;


/**
 * This is a cron task wich poll for new endpoints in order to expose them.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServicePoller.class) })
public class ServicePollerImpl implements ServicePoller {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "dsb-configuration", signature = DSBConfigurationService.class)
    protected DSBConfigurationService configurationService;

    @Requires(name = "new-service-exposer", signature = NewServiceExposer.class)
    protected NewServiceExposer newServiceExposer;

    private ScheduledExecutorService executorService;

    private boolean paused;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);

        if (this.log.isDebugEnabled()) {
            this.log.debug("Starting...");
            this.log.debug("Endoints polling will start in "
                    + this.configurationService.getEndpointsPollingDelay() + " seconds ");
            this.log.debug("Endoints will be polled each "
                    + this.configurationService.getEndpointsPollingPeriod() + " seconds ");
        }

        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.paused = false;
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");

        this.stopPolling();
    }

    /**
     * {@inheritDoc}
     */
    public void setPeriod(long time, TimeUnit timeUnit) {
        // TODO Auto-generated method stub
        this.log.info("#setPeriod is not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void startPolling() {
        this.log.info("Start polling for new services");
        if ((this.executorService != null) && !this.executorService.isTerminated()) {
            this.executorService.scheduleAtFixedRate(new NewServiceChecker(), 5,
                    this.configurationService.getEndpointsPollingPeriod(), TimeUnit.SECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopPolling() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * {@inheritDoc}
     */
    public void resume() {
        this.paused = false;
    }

    class NewServiceChecker implements Runnable {

        /**
         * 
         */
        public NewServiceChecker() {
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            if (!ServicePollerImpl.this.paused) {
                ServicePollerImpl.this.log.info("Time to look for new services to expose");
                ServicePollerImpl.this.newServiceExposer.expose();
            } else {
                ServicePollerImpl.this.log.debug("The cron is paused...");
            }
        }

    }

    class NewServiceCheckerExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {
            ServicePollerImpl.this.logger.log(BasicLevel.ERROR, e.getMessage() + " : "
                    + e.getCause());
        }
    }
}
