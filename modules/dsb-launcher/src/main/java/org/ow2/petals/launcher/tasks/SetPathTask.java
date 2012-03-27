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

import java.io.File;
import java.util.List;

/**
 * 
 * Created on 13 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class SetPathTask extends Task {

    protected File currentPath;

    /**
     * 
     */
    public SetPathTask(File currentPath) {
        this.currentPath = currentPath;

        this.setShortcut("sp");
        this.setName("setpath");
        this.setDescription("Change current file system path (possible args [Path])");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.util.Task#process(java.util.List)
     */
    @Override
    public int doProcess(List<String> args) {
        int result = OK_CODE;
        this.currentPath = new File(args.get(0));
        System.out.println("New current path is : " + currentPath.getAbsolutePath());
        return result;
    }

    @Override
    public boolean validateArgs(List<String> args) {
        boolean result = false;
        if (args != null && args.size() > 0) {
            File f = new File(args.get(0));
            result = f.exists();
            if (!result) {
                System.out.println("Path not found : " + f.getAbsolutePath());
            }
        }
        return result;
    }
}
