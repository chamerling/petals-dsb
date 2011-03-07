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
package org.petalslink.dsb.transport.xmpp;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.ow2.petals.util.LoggingUtil;

/**
 * Does nothing but log activity...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPRosterListener implements RosterListener {

    private final LoggingUtil log;

    /**
     * 
     */
    public XMPPRosterListener(final LoggingUtil log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void entriesAdded(Collection<String> arg0) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entries added " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entriesDeleted(Collection<String> arg0) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entries deleted " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entriesUpdated(Collection<String> arg0) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entries updated " + arg0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void presenceChanged(Presence arg0) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Presence changed from : '" + arg0.getFrom() + "', status is : '"
                    + arg0.getStatus() + "', available ? '" + arg0.isAvailable() + "', away ? '"
                    + arg0.isAway() + "'");
        }
    }
}
