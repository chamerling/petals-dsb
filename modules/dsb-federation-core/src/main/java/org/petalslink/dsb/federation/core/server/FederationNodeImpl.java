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
package org.petalslink.dsb.federation.core.server;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.api.FederationClient;
import org.petalslink.dsb.federation.core.api.FederationNode;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationNodeImpl extends FederationClientImpl implements
		FederationNode {

	private final Map<String, FederationClient> clients;

	/**
	 * 
	 */
	public FederationNodeImpl(String name, String callbackURL) {
		super(name, callbackURL, new Date());
		this.clients = new HashMap<String, FederationClient>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws FederationException
	 */
	public void addClient(String name, String callbackURL)
			throws FederationException {
		if ((name == null) || (callbackURL == null)) {
			throw new FederationException(
					"Name or/and callback is/are null : name='" + name
							+ "', callbackURL='" + callbackURL + "'");
		}

		if (this.clients.get(name) != null) {
			throw new FederationException("The client '" + name
					+ "' is already registered in the federation");
		}

		this.clients.put(name, new FederationClientImpl(name, callbackURL,
				new Date()));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<FederationClient> getClients() {
		return new HashSet<FederationClient>(this.clients.values());
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeClient(String name) throws FederationException {
		if (name == null) {
			throw new FederationException("Client name can not be null");
		}
		this.clients.remove(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public FederationClient getClient(String name) {
		return this.clients.get(name);
	}

}
