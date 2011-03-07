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
package org.petalslink.dsb.federation.xmpp.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.xmpp.commons.XMPPConnectionManager;

/**
 * This service is in charge of connecting to a XMPP server...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPConnectionService implements Service {

    private static Log logger = LogFactory.getLog(XMPPConnectionService.class);

    private final String login;

    private final String password;

    /**
     * 
     */
    public XMPPConnectionService(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "XMPPConnectionService";
    }

    /**
     * {@inheritDoc}
     */
    public TYPE getType() {
        return TYPE.INTERNAL;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        xmppConnectionManager.connect();

        if (logger.isInfoEnabled()) {
            logger.info("Logging to XMPP server with user='" + this.login
                    + "' and password='************'");
        }
        xmppConnectionManager.login(this.login, this.password);
        xmppConnectionManager.setStatus(XMPPConnectionManager.AVAILABLE_STATUS, "Federation Ready");
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        XMPPConnectionManager.getInstance().setStatus(XMPPConnectionManager.UNAVAILABLE_STATUS,
                "Shutting down Federation...");

        if (logger.isInfoEnabled()) {
            logger.info("Disconnecting from server");
        }
        XMPPConnectionManager.getInstance().disconnect();
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return 100;
    }

}
