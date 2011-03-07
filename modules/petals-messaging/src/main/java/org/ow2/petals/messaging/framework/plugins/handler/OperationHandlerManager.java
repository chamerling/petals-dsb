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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;
import org.ow2.petals.messaging.framework.lifecycle.NullLifeCycle;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.plugins.Handler;
import org.ow2.petals.messaging.framework.plugins.HandlerException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class OperationHandlerManager extends NullLifeCycle implements Handler, LifeCycle {

    public final static String OPERTATION = "operation";

    Map<String, Set<Handler>> handlers;

    private final Log logger = LogFactory.getLog(OperationHandlerManager.class);

    /**
     * 
     */
    public OperationHandlerManager() {
        this.handlers = new HashMap<String, Set<Handler>>();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        // TODO Auto-generated method stub
        return "OperationHandlerManager";
    }

    /**
     * {@inheritDoc}
     */
    public void handle(Message message) throws HandlerException {
        if ((message != null) && (message.get(OPERTATION) != null)) {
            Set<Handler> set = this.handlers.get(message.get(OPERTATION));
            for (Handler handler : set) {
                // TODO : Do it in parallel!!!
                this.logger.debug("Handling message in handler " + handler.getName());
                handler.handle(message);
            }
        } else {
            // exception
        }
    }

    public final void addHandler(final String operationName, final Handler handler) {
        if ((operationName == null) || (handler == null)) {
            // exception
            return;
        }

        if (this.handlers.get(operationName) == null) {
            this.handlers.put(operationName, new HashSet<Handler>());
        }
        this.handlers.get(operationName).add(handler);
    }
}
