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
package org.ow2.petals.messaging.framework.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class LIfeCycleManagerImpl implements LifeCycleManager {

    private final List<LifeCycleListener> listeners;

    private static Log logger = LogFactory.getLog(LIfeCycleManagerImpl.class);

    /**
     * 
     */
    public LIfeCycleManagerImpl() {
        this.listeners = new ArrayList<LifeCycleListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void register(LifeCycleListener listener) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : register with params listener = " + listener);
        }
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unregister(LifeCycleListener listener) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : unregister with params listener = " + listener);
        }
        if (listener != null) {
            this.listeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void postInit() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : postInit");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.postInit();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void postStart() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : postStart");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.postStart();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void postStop() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : postStop");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.postStop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void preInit() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : preInit");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.preInit();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void preStart() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : preStart");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.preStart();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void preStop() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method : preStop");
        }
        for (LifeCycleListener listener : this.listeners) {
            listener.preStop();
        }
    }

}
