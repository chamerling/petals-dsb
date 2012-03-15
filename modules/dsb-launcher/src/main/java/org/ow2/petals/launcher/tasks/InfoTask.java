/**
 * PETALS - PETALS Services Platform.
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
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.launcher.tasks;

import java.util.List;

import org.ow2.petals.jmx.JMXClient;
import org.ow2.petals.kernel.api.server.PetalsServer;

/**
 * 
 * Created on 13 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class InfoTask extends Task {

    private JMXClient client;

    private PetalsServer petalsServer;

    /**
     * 
     * @param petalsServer
     */
    public InfoTask(JMXClient client, PetalsServer petalsServer) {
        this.client = client;
        this.petalsServer = petalsServer;

        this.setShortcut("i");
        this.setName("info");
        this.setDescription("Display the local container information");

    }

    @Override
    public int doProcess(List<String> args) {
        try {
            System.out.println(client.getAdminServiceClient().getSystemInfo());
            System.out.println(petalsServer.getContainerConfiguration());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return OK_CODE;
    }
}
