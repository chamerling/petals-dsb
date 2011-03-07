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
package org.petalslink.dsb.transport.extension;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Cardinality;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.transport.TransportListener;
import org.petalslink.dsb.transport.api.Constants;
import org.ow2.petals.util.LoggingUtil;

/**
 * This transport listener dispatches the incoming message to a set of core
 * listeners in a parallel way using thread workers
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = TransportListener.class) })
public class ParallelTransportListenerImpl implements TransportListener {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * The core listeners which will be invoked
     */
    @Requires(name = Constants.TRANSPORTLISTENERS_PREFIX, signature = TransportListener.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private Hashtable<String, Object> coreListeners = new Hashtable<String, Object>();

    private ExecutorService executors;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.executors = Executors.newFixedThreadPool(coreListeners.size());
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
        this.executors.shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    public void onExchange(final MessageExchange messageExchange) {
        // let's dispatch the message
        if (log.isDebugEnabled()) {
            log.debug("Got a message in parallel transport listener, let's dispatch");
        }

        Iterator<Object> iter = this.coreListeners.values().iterator();
        while (iter.hasNext()) {
            final TransportListener listener = (TransportListener) iter.next();
            if (log.isDebugEnabled()) {
                log.debug("Submitting to listener " + listener.getClass().getCanonicalName());
            }
            executors.submit(new Runnable() {
                public void run() {
                    listener.onExchange(messageExchange);
                }
            });
        }
    }
}
