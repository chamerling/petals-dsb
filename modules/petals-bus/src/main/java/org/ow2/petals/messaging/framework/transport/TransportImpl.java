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
package org.ow2.petals.messaging.framework.transport;

import java.util.HashSet;

import org.ow2.petals.messaging.framework.message.Callback;
import org.ow2.petals.messaging.framework.message.Client;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessagingException;
import org.ow2.petals.messaging.framework.plugins.Handler;
import org.ow2.petals.messaging.framework.plugins.HandlerException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TransportImpl implements Transport {

    private final HashSet<Handler> inHandlers;

    private final HashSet<Handler> outHandlers;

    private Client client;

    /**
     * 
     */
    public TransportImpl() {
        this.inHandlers = new HashSet<Handler>();
        this.outHandlers = new HashSet<Handler>();
    }

    /**
     * {@inheritDoc}
     */
    public void addRequestHandler(Handler handler) {
        if (handler == null) {
            return;
        }
        this.inHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void addResponseHandler(Handler handler) {
        if (handler == null) {
            return;
        }
        this.outHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public Message send(Message in) throws MessagingException {
        for (Handler handler : this.inHandlers) {
            try {
                handler.handle(in);
            } catch (HandlerException e) {
                e.printStackTrace();
            }
        }

        Message response = null;
        if (this.client != null) {
            response = this.client.send(in);
        }

        for (Handler handler : this.outHandlers) {
            try {
                handler.handle(response);
            } catch (HandlerException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * {@inheritDoc}
     */
    public void send(Message in, Callback callback) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setCoreClient(Client client) {
        this.client = client;
    }

}
