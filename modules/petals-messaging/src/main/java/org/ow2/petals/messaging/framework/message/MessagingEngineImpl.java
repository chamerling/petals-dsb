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

import java.util.concurrent.ConcurrentHashMap;

import org.ow2.petals.messaging.framework.lifecycle.NullLifeCycle;

/**
 * Default implementation with a single client per protocol.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class MessagingEngineImpl extends NullLifeCycle implements MessagingEngine {

	private final ConcurrentHashMap<String, Client> clients;

	/**
     * 
     */
	public MessagingEngineImpl() {
		this.clients = new ConcurrentHashMap<String, Client>();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addClient(String protocol, Client client) {
		if ((protocol == null) || (client == null)) {
			throw new NullPointerException(
					"Protocol and client can not be null");
		}
		this.clients.put(protocol, client);
	}

    /**
     * {@inheritDoc}
     */
    public Client getClient(String protocol) {
        return this.clients.get(protocol);
    }

	/**
	 * {@inheritDoc}
	 */
	public Message send(Message message) throws MessagingException {
		if (message == null) {
			throw new MessagingException("Null message");
		}

		// get the client from the message property
		String protocol = (String) message.get(Constants.PROTOCOL);
		if (protocol == null) {
			throw new MessagingException("Null protocol, please set the "
					+ Constants.PROTOCOL + " String property in the message");
		}

		final Client client = this.clients.get(protocol);
		if (client == null) {
			throw new MessagingException("Client is null for protocol '"
					+ protocol + "'");
		}

        return client.send(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(final Message message, final Callback callback)
			throws MessagingException {
		if (message == null) {
			throw new MessagingException("Message can not be null");
		}

		if (callback == null) {
			throw new MessagingException("Callback handler can not be null");
		}

		// TODO : Use a worker pool!

		Runnable r = new Runnable() {
			public void run() {
				String protocol = (String) message.get(Constants.PROTOCOL);

				final Client client = MessagingEngineImpl.this.clients
						.get(protocol);
				if (client == null) {
					callback.onError(new MessagingException(
							"No client found in messaging engine for protocol "
									+ protocol));
				}

				try {
					Message response = client.send(message);
					callback.onMessage(response);
				} catch (MessagingException e) {
					callback.onError(e);
				}
			}
		};

		Thread t = new Thread(r);
		t.start();

		return;
	}

	/**
	 * {@inheritDoc}
	 */
	public void receive(Message message) throws MessagingException {
		// TODO
	}
}
