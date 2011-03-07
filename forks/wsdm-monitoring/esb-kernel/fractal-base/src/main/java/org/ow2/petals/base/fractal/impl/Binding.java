/**
 * Maestro-Core - SOA Tools Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $id.java
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.base.fractal.impl;

import org.objectweb.fractal.api.Interface;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class Binding {

    /**
     * The client interface name.
     */
    private String clientInterfaceName = "";

    /**
     * The server interface.
     */
    private Interface serverInterface = null;

    /**
     * Default Constructor.
     *
     * @param cin
     *            the name of the client interface
     * @param si
     *            the interface of the server
     */
    public Binding(final String cin, final Interface si) {
        this.clientInterfaceName = cin;
        this.serverInterface = si;
    }

    /**
     * Get the client interface.
     *
     * @return The interface of the client
     */
    public String getClientInterfaceName() {
        return this.clientInterfaceName;
    }

    /**
     * Get the server interfaces.
     *
     * @return The interface of the server
     */
    public Interface getServerInterface() {
        return this.serverInterface;
    }

}
