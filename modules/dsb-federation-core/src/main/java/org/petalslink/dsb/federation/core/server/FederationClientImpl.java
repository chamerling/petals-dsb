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

import org.petalslink.dsb.federation.core.api.FederationClient;

/**
 * Bean implementation of FederationClient
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationClientImpl implements FederationClient {

    private final String name;

    private final String callbackURL;

    private final Date joinDate;

    private boolean reachable = false;

    /**
	 * 
	 */
    public FederationClientImpl(String name, String callbackURL, Date joinDate) {
        this.name = name;
        this.callbackURL = callbackURL;
        this.joinDate = joinDate;
        this.reachable = true;
    }

    /**
     * {@inheritDoc}
     */
    public String getCallbackURL() {
        return this.callbackURL;
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
    public Date getJoinDate() {
        return this.joinDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setReachable() {
        this.reachable = true;
    }

    /**
     * {@inheritDoc}
     */
    public void setUnreachable() {
        this.reachable = false;
    }

    /**
     * @return the reachable
     */
    public boolean isReachable() {
        return this.reachable;
    }

}
