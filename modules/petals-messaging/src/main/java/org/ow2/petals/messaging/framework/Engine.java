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

import java.util.List;

import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Engine extends LifeCycle {

    /**
     * Add a component. This is only possible on some lifecycle.
     * 
     * @param <T>
     * @param type
     * @param plugin
     * @throws EngineException
     */
    <T> void addComponent(Class<T> type, T component) throws EngineException;

    /**
     * Get a component, can return null if not found
     * 
     * @param <T>
     * @param type
     * @return
     */
    <T> T getComponent(Class<T> type);

    /**
     * Get all the plugins for the given type ie if type is an interface and
     * many plugins are implementing this interface, the method must return all
     * the interface implementation available in the plugins.
     * 
     * @param <T>
     * @param type
     * @return
     */
    <T> List<T> getComponents(Class<T> type);

}
