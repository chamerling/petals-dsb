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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.servicebus.MessageHandler;
import org.ow2.petals.messaging.framework.servicebus.module.Phase;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class PhaseImpl implements Phase {

    HashSet<MessageHandler<Message>> handlers;

    private final String name;

    private static Log logger = LogFactory.getLog(PhaseImpl.class);

    public PhaseImpl(String name, Set<MessageHandler<Message>> handlers) {
        this.handlers = new HashSet<MessageHandler<Message>>();
        if (handlers != null) {
            this.handlers.addAll(handlers);
        }
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public Set<MessageHandler<Message>> getHandlers() {
        return this.handlers;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void addHandler(MessageHandler<Message> handler) {
        this.handlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessage(Message t) {
        Iterator<MessageHandler<Message>> iter = this.handlers.iterator();
        while (iter.hasNext()) {
            MessageHandler<Message> handler = iter.next();
            if (logger.isInfoEnabled()) {
                logger.info("Handling " + handler);
            }
            handler.handleMessage(t);
        }
    }

}
