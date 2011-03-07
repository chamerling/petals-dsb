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
package org.ow2.petals.messaging.framework.plugins;

import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;

/**
 * @author chamerling - eBM WebSourcing
 * @param <T>
 * 
 */
public abstract class AbstractManager<T extends LifeCycle> implements Manager<T> {

    protected final Map<String, T> managedObjects;

    protected STATE state;

    /**
     * 
     */
    public AbstractManager() {
        this.managedObjects = new HashMap<String, T>();
        this.state = STATE.STOPPED;
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, T managed) {
        this.managedObjects.put(name, managed);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String name) {
        this.managedObjects.remove(name);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return this.managedObjects.size();
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        for (T managed : this.managedObjects.values()) {
            if (managed != null) {
                try {
                    managed.init();
                } catch (LifeCycleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        this.state = STATE.INITIALIZED;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        // TODO : start all the jobs in separated threads
        for (T managed : this.managedObjects.values()) {
            if (managed != null) {
                try {
                    managed.start();
                } catch (LifeCycleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        this.state = STATE.STARTED;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        for (T managed : this.managedObjects.values()) {
            if (managed != null) {
                try {
                    managed.stop();
                } catch (LifeCycleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        this.state = STATE.STOPPED;
    }

    /**
     * {@inheritDoc}
     */
    public STATE getState() {
        return this.state;
    }

    /**
     * @return the managedObjects
     */
    public Map<String, T> getManagedObjects() {
        return this.managedObjects;
    }

}
