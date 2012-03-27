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
package org.ow2.petals.launcher.tasks;

import java.util.List;

import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.api.server.PetalsServer;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class SynchronizeRegistryTask extends Task {

    private final PetalsServer petalsServer;

    /**
     * 
     */
    public SynchronizeRegistryTask(final PetalsServer petalsServer) {
        super();
        this.petalsServer = petalsServer;
        this.setName("sync");
        this.setShortcut("s");
        this.setDescription("Synchronize the endpoint registry");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doProcess(List<String> args) {
        try {
            System.out.print("Synchronizing the registry...");
            this.petalsServer.synchronizeRegistry();
            System.out.println(" Done!");
        } catch (PetalsException e) {
            e.printStackTrace(System.err);
        }
        return OK_CODE;
    }

}
