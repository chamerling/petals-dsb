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
package org.ow2.petals.messaging.framework.plugins.service;

import java.util.Iterator;

import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.plugins.AbstractManager;
import org.ow2.petals.messaging.framework.plugins.Service;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ServiceManager extends AbstractManager<Service> implements Service {

    /**
     * 
     */
    public ServiceManager() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(Message message) throws EngineException {
        if (!this.state.equals(STATE.STARTED)) {
            throw new EngineException("Can not service while not started");
        }

        if (message == null) {
            return;
        }

        // get the path from the message
        Object o = message.get("path");
        String path = (o != null) ? o.toString() : "";

        Service s = null;
        s = this.managedObjects.get(path);

        if (s == null) {
            Iterator<String> iter = this.managedObjects.keySet().iterator();
            while (iter.hasNext() && (s == null)) {
                String key = iter.next();
                if (key.endsWith("/*")) {
                    // this is a wildcard, let see if the path and the key match
                    String start = key.substring(0, key.length() - 2);
                    if ((start.length() > 0) && path.startsWith(start)) {
                        s = this.managedObjects.get(key);
                    }
                }
            }
        }

        if (s != null) {
            // TODO : Async
            s.invoke(message);
        } else {
            // no service found...
        }
    }
}
