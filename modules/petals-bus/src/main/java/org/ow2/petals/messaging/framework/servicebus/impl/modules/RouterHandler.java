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
package org.ow2.petals.messaging.framework.servicebus.impl.modules;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.servicebus.Endpoint;
import org.ow2.petals.messaging.framework.servicebus.MessageHandler;
import org.ow2.petals.messaging.framework.servicebus.service.Router;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RouterHandler implements MessageHandler<Message> {

    private static Log logger = LogFactory.getLog(RouterHandler.class);

    /**
     * 
     */
    public RouterHandler() {
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessage(Message message) {
        if (logger.isInfoEnabled()) {
            logger.info("In module " + RouterHandler.class.getCanonicalName());
        }

        Endpoint endpoint = message.getContent(Endpoint.class);
        if (endpoint == null) {
            Set<Endpoint> ep = this.getRouter(message).route(message);
            // put it in the message, next module can potentially choose the
            // right endpoint...
            if (logger.isDebugEnabled()) {
                logger.debug("Found " + ep.size() + " endpoints");
            }
            message.put("endpoints", ep);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Endpoints are already defined in the message");
            }
        }
    }

    /**
     * @return
     */
    private Router getRouter(Message message) {
        return this.getEngine(message).getComponent(Router.class);
    }

    /**
     * @param message
     * @return
     */
    private Engine getEngine(Message message) {
        return message.getContent(Engine.class);
    }

}
