/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.petalslink.dsb.jbi.se.wsn.listeners;

import org.ow2.petals.component.framework.api.message.Exchange;

/**
 * @author
 * 
 */
public class JBIListener extends NotificationV2JBIListener {

    /**
     * Creates a new instance of {@link JBIListener}
     * 
     */
    public JBIListener() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.listener.AbstractJBIListener
     */
    public boolean onJBIMessage(final Exchange exchange) {
        System.out
                .println("Got a message, should not happen since we only process WSN notification...");
        // set a fault...
        return false;
    }
}
