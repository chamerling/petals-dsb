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
import java.util.Map;

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
import org.petalslink.dsb.transport.api.Constants;
import org.petalslink.dsb.transport.api.ReceiveInterceptor;
import org.petalslink.dsb.transport.api.Constants.STATUS;
import org.ow2.petals.util.LoggingUtil;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ReceiveInterceptor.class) })
public class SequentialReceiveInterceptorImpl implements ReceiveInterceptor {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * The send interceptors are optional
     */
    @Requires(name = Constants.RECEIVE_MODULE_PREFIX, signature = ReceiveInterceptor.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private Map<String, Object> interceptors = new Hashtable<String, Object>();

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public STATUS receive(MessageExchange message) {
        if (interceptors == null || interceptors.size() == 0) {
            return STATUS.CONTINUE;
        }

        Constants.STATUS status = STATUS.CONTINUE;
        Iterator<Object> iter = this.interceptors.values().iterator();
        while (iter.hasNext() && status != Constants.STATUS.ABORT) {
            ReceiveInterceptor receiveInterceptor = (ReceiveInterceptor) iter.next();
            if (log.isDebugEnabled()) {
                log.debug("Calling the receive interceptor '"
                        + receiveInterceptor.getClass().getName() + "'");
            }
            status = receiveInterceptor.receive(message);
            if (log.isDebugEnabled()) {
                log.debug("Receive interceptor '" + receiveInterceptor.getClass().getName()
                        + "' called, result is " + status);
            }
        }

        return status;
    }

}
