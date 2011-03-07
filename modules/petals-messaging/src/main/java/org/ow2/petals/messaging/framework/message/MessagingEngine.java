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
package org.ow2.petals.messaging.framework.message;

import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycle;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface MessagingEngine extends LifeCycle {

	/**
	 * Add a client for the given protocol. The number of clients per protocol
	 * is defined at the implementation level.
	 * 
	 * @param protocol
	 * @param client
	 */
	void addClient(String protocol, Client client) throws EngineException;

    /**
     * Get the client for the given protocol name
     * 
     * @return
     */
    Client getClient(String protocol);

	/**
	 * Send synchronously the message and gets the response.
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	Message send(Message message) throws MessagingException;

	/**
	 * Send asynchronously the message. The callback will be caled on message
	 * reception.
	 * 
	 * @param message
	 * @param callback
	 * @throws MessagingException
	 */
	void send(Message message, Callback callback) throws MessagingException;

	/**
	 * Receive a message
	 * 
	 * @param message
	 * @throws MessagingException
	 */
	void receive(Message message) throws MessagingException;
}
