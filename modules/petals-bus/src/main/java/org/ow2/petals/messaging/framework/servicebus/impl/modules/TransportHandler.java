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

import java.net.URI;

import javax.annotation.Resource;

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.servicebus.Endpoint;
import org.ow2.petals.messaging.framework.servicebus.MessageHandler;
import org.ow2.petals.messaging.framework.servicebus.TransportManager;
import org.ow2.petals.messaging.framework.servicebus.Transporter;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TransportHandler implements MessageHandler<Message> {

    @Resource
    private Engine engine;

    /**
     * 
     */
    public TransportHandler() {
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessage(Message t) {
        Endpoint endpoint = t.getContent(Endpoint.class);
        if (endpoint == null) {
            t.setContent(Exception.class, new Exception(
                    "Can not get a vaild endpoint to send the message to..."));
        } else {
            TransportManager manager = this.getTransportManager();
            Transporter transporter = manager.getTransporter(this.getTransportId(endpoint.getURL()));
            if (transporter == null) {
                t.setContent(Exception.class, new Exception(
                        "Can not find a valid transporter for endpoint " + endpoint.getName()));
            }
            // TODO : asynchronous push...
            transporter.push(t);
        }
    }

    /**
     * @param url
     * @return
     */
    private String getTransportId(String url) {
        return url != null ? URI.create(url).getScheme() : "";
    }

    TransportManager getTransportManager() {
        return this.engine.getComponent(TransportManager.class);
    }
}
