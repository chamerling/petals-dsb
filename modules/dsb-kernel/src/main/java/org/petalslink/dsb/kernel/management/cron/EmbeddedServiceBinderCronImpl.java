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

import java.util.List;
import java.util.Map;
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
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.api.management.binder.EmbeddedServiceBinder;
import org.petalslink.dsb.kernel.api.management.cron.EmbeddedServiceBinderCron;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = EmbeddedServiceBinderCron.class) })
public class EmbeddedServiceBinderCronImpl implements EmbeddedServiceBinderCron {

    @Requires(name = "embeddedservicebinder", signature = EmbeddedServiceBinder.class)
    private EmbeddedServiceBinder embeddedServiceBinder;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private ScheduledExecutorService executorService;

    protected boolean oneShot;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.oneShot = false;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.START, priority = 700)
    public synchronized void execute() {
        if (!this.oneShot) {
            this.startPolling();
            this.oneShot = true;
        } else {
            this.log.warning("Can not start the job more than once!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startPolling() {
        if ((this.executorService != null) && !this.executorService.isTerminated()) {
            this.executorService.scheduleAtFixedRate(new EmbeddedServiceChecker(), 30, 120,
                    TimeUnit.SECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopPolling() {
        this.log.debug("Stopping to watch for embedded services to bind");
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    class EmbeddedServiceChecker implements Runnable {
        public EmbeddedServiceChecker() {
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            EmbeddedServiceBinderCronImpl.this.log.info("Looking if there are remaining services");
            Map<String, List<String>> remaining = EmbeddedServiceBinderCronImpl.this.embeddedServiceBinder
                    .getServicesToBind();
            if ((remaining != null) && (remaining.size() > 0)) {
                // try to bind all...
                EmbeddedServiceBinderCronImpl.this.embeddedServiceBinder.bindAll();
            } else {
                // stop myself...
                EmbeddedServiceBinderCronImpl.this.stopPolling();
            }
        }
    }
}
