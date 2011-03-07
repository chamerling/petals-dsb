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
package org.ow2.petals.messaging.framework.servicebus.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.lifecycle.NullLifeCycle;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.servicebus.Endpoint;
import org.ow2.petals.messaging.framework.servicebus.service.Registry;
import org.ow2.petals.messaging.framework.servicebus.service.Router;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RouterImpl extends NullLifeCycle implements Router {

    /**
     * 
     */
    public RouterImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public Set<Endpoint> route(Message message) {
        // get the registry
        Registry r = this.getEngine(message).getComponent(Registry.class);
        // create the query from the message information
        Map<String, String> query = new HashMap<String, String>();
        return r.lookup(query);
    }

    Engine getEngine(Message message) {
        return message.getContent(Engine.class);
    }

}
