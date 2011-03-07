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
package org.petalslink.dsb.kernel.ws;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.KernelWebService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.management.binder.NewServiceExposer;
import org.petalslink.dsb.kernel.management.binder.ServiceExposer;
import org.petalslink.dsb.ws.api.ExposerService;


/**
 * Expose all new services
 * 
 * @author chamerling - eBM WebSourcing
 *
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = KernelWebService.class),
        @Interface(name = "webservice", signature = ExposerService.class) })
public class ExposerServiceImpl implements KernelWebService, ExposerService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Requires(name = "new-service-exposer", signature = ServiceExposer.class)
    private NewServiceExposer exposer;

    private ExecutorService executorService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.executorService = Executors.newSingleThreadExecutor();

    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return this.component;
    }

    /**
     * {@inheritDoc}
     */
    public void expose() {
        // launch it in a new thread since it can takes time and the web service
        // does not have to be blocked!!!
        this.log.debug("Got an #expose WS call, submitting it to executor...");
        this.executorService.submit(new Runnable() {
            public void run() {
                ExposerServiceImpl.this.exposer.expose();
            }
        });
    }
}
