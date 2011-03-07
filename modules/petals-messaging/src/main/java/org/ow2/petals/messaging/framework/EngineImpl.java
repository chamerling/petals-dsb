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
package org.ow2.petals.messaging.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleManager;

/**
 * The default engine implementation
 * 
 * TODO : Synchronize all!!!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EngineImpl implements Engine {

    private static Log logger = LogFactory.getLog(EngineImpl.class);

    private final Map<Class<?>, Object> plugins;

    boolean initCalled;

    private STATE state;

    /**
     * 
     */
    protected EngineImpl() {
        this.plugins = new ConcurrentHashMap<Class<?>, Object>();
        this.initCalled = false;
        this.state = STATE.STOPPED;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws LifeCycleException {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing engine");
        }
        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preInit();
        }

        // get all the plugins which are implementing the LifeCyle interface in
        // order to start them
        List<LifeCycle> pluginsToInit = this.getComponents(LifeCycle.class);
        for (LifeCycle lifeCycle : pluginsToInit) {
            try {
                lifeCycle.init();
            } catch (LifeCycleException e) {
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postInit();
        }

        this.state = STATE.INITIALIZED;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void start() throws LifeCycleException {

        if (!this.initCalled) {
            // call init before...
            this.init();
        }

        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preStart();
        }

        // get all the plugins which are implementing the LifeCyle interface in
        // order to start them
        List<LifeCycle> pluginsToStart = this.getComponents(LifeCycle.class);
        for (LifeCycle lifeCycle : pluginsToStart) {
            try {
                lifeCycle.start();
            } catch (LifeCycleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postStart();
        }
        this.state = STATE.STARTED;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void stop() {

        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preStop();
        }

        // get all the plugins which are implementing the lifecycle in order to
        // stop them automatically
        List<LifeCycle> pluginsToStop = this.getComponents(LifeCycle.class);
        for (LifeCycle lifeCycle : pluginsToStop) {
            try {
                lifeCycle.stop();
            } catch (LifeCycleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postStop();
        }
        this.state = STATE.STOPPED;
    }

    private boolean isStarted() {
        return STATE.STARTED.equals(this.getState());
    }

    public <T> void addComponent(Class<T> type, T plugin) throws EngineException {
        if (this.isStarted()) {
            throw new EngineException("Can not add plugin if engine is started");
        }
        if ((type == null) || (plugin == null)) {
            throw new EngineException("Can not add null plugin");
        }
        this.plugins.put(type, plugin);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getComponent(Class<T> type) {
        Object o = this.plugins.get(type);
        if (o != null) {
            return type.cast(o);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getComponents(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Object o : this.plugins.values()) {
            if (o != null) {
                try {
                    T object = type.cast(o);
                    result.add(object);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public STATE getState() {
        return this.state;
    }
}
