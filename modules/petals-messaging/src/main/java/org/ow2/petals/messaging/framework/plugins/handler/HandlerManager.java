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
package org.ow2.petals.messaging.framework.plugins.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;
import org.ow2.petals.messaging.framework.lifecycle.NullLifeCycle;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.plugins.Handler;
import org.ow2.petals.messaging.framework.plugins.HandlerException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class HandlerManager extends NullLifeCycle implements Handler, LifeCycle {

    private final Map<Class<?>, List<Handler>> handlers;

    private final Log logger = LogFactory.getLog(HandlerManager.class);

    /**
     * 
     */
    public HandlerManager() {
        super();
        this.handlers = new HashMap<Class<?>, List<Handler>>();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> void addHandler(Class<T> type, Handler handler) throws EngineException {
        if (this.isStarted()) {
            throw new EngineException("Can not add handler while started");
        }

        if (this.handlers.get(type) == null) {
            this.handlers.put(type, new ArrayList<Handler>());
        }

        this.handlers.get(type).add(handler);
    }

    public void handle(Message message) throws HandlerException {
        if (!this.isStarted()) {
            throw new HandlerException("Can not handle while not started");
        }

        if (message == null) {
            return;
        }

        Set<Class<?>> set = this.handlers.keySet();
        for (Class<?> class1 : set) {
            if (message.getContent(class1) != null) {
                List<Handler> handlers = this.handlers.get(class1);
                for (Handler handler : handlers) {
                    this.logger.info("Handling message for handler "
                            + handler.getClass().getCanonicalName());
                    try {
                        handler.handle(message);
                    } catch (HandlerException e) {
                        String s = "Exception on handling message : " + e.getMessage();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.warn(s, e);
                        } else {
                            this.logger.warn(s);
                        }
                    }
                }
            }
        }
    }
}
