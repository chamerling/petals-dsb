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
package org.petalslink.dsb.kernel.management.binder;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.api.service.ServiceEndpoint;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.management.binder.BinderManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = BinderManager.class) })
public class BinderManagerImpl implements BinderManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, Set<ServiceEndpoint>> bindings;

    /**
     * 
     */
    public BinderManagerImpl() {
    }

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.bindings = new Hashtable<String, Set<ServiceEndpoint>>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void addBind(String protocol, ServiceEndpoint endpoint) {
        synchronized (this.bindings) {
            if (this.bindings.get(protocol) == null) {
                Set<ServiceEndpoint> set = new HashSet<ServiceEndpoint>();
                this.bindings.put(protocol, set);
            }
            this.bindings.get(protocol).add(endpoint);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<ServiceEndpoint> getBoundServices(String protocol) {
        synchronized (this.bindings) {
            return this.bindings.get(protocol);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(String protocol, ServiceEndpoint endpoint) {
        synchronized (this.bindings) {
            Set<ServiceEndpoint> set = this.bindings.get(protocol);
            if (set != null) {
                set.remove(endpoint);
            }
        }
    }

}
