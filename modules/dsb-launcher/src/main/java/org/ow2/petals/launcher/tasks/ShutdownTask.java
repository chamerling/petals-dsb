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

import org.ow2.petals.jmx.PetalsAdminServiceClient;

/**
 * 
 * Created on 13 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class ShutdownTask extends Task {
    
    protected PetalsAdminServiceClient petalsService;

    public ShutdownTask(PetalsAdminServiceClient petalsService) {
        this.petalsService = petalsService;
        
        this.setName("shutdown");
        this.setShortcut("x");
        this.setDescription("Shut down PEtALS");
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.launcher.util.Command#process(java.util.List)
     */
    @Override
    public int doProcess(List<String> args) {
        int result = STOP_CODE;
        try {
            petalsService.shutdownContainer();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            result = ERROR_CODE;
        }
        return result;
    }
}
