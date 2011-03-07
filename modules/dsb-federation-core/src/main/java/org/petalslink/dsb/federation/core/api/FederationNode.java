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
package org.petalslink.dsb.federation.core.api;

import java.util.Set;

import org.petalslink.dsb.federation.api.FederationException;

/**
 * A federation node is the representation of a federation server. The
 * federation server can also be the client of an upper federation server...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface FederationNode extends FederationClient {

	/**
	 * Get the list of connected clients.
	 * 
	 * @return
	 */
	Set<FederationClient> getClients();

	/**
	 * Add a client to the federation
	 * 
	 * @param client
	 * @throws FederationException
	 */
	void addClient(String name, String callbackURL) throws FederationException;

	/**
	 * Remove a client
	 * 
	 * @param name
	 * @throws FederationException
	 */
	void removeClient(String name) throws FederationException;

	FederationClient getClient(String name);

}
