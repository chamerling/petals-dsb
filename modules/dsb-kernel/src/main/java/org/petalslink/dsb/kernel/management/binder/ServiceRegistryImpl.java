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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.registry.api.Endpoint;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.management.binder.ServiceRegistry;

/**
 * TODO : Should register entries somewhere in file...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceRegistry.class) })
public class ServiceRegistryImpl implements ServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, Map<String, Endpoint>> cache;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");

        this.cache = new HashMap<String, Map<String, Endpoint>>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addService(String protocol, String url, Endpoint endpoint) {
        Map<String, Endpoint> map = this.cache.get(protocol);
        if (map == null) {
            map = new HashMap<String, Endpoint>();
            this.cache.put(protocol, map);
        }
        map.put(url, endpoint);
    }

    /**
     * {@inheritDoc}
     */
    public void removeService(String protocol, String url) {
        Map<String, Endpoint> map = this.cache.get(protocol);
        if (map != null) {
            map.remove(url);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getURLs(String protocol) {
        Set<String> result = null;
        Map<String, Endpoint> map = this.cache.get(protocol);
        if (map != null) {
            result = map.keySet();
        } else {
            result = new HashSet<String>(0);
        }
        return result;
    }
}
