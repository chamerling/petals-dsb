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
package org.petalslink.dsb.kernel.listener;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import org.ow2.petals.kernel.api.server.PetalsStateListener;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.listener.LifeCycleManager;

/**
 * @author chamerling - eBM WebSourcing
 * @deprecated use {@link LifeCycleManager} instead. Will be removed on 1.0
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = LifeCycleListenerManager.class) })
public class LifeCycleListenerManagerImpl implements LifeCycleListenerManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = PREFIX, signature = PetalsStateListener.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> listeners = new Hashtable<String, Object>();

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
    public Set<PetalsStateListener> getListeners() {
        HashSet<PetalsStateListener> result = new HashSet<PetalsStateListener>();
        if (this.listeners != null) {
            Iterator<String> iter = this.listeners.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                Object o = this.listeners.get(key);
                if (o instanceof PetalsStateListener) {
                    result.add((PetalsStateListener) o);
                } else {
                    // warning
                    this.log.warning(o + " is not an instance of "
                            + PetalsStateListener.class.getCanonicalName());
                }
            }
        }
        return result;
    }
}
