/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2007 EBM Websourcing, http://www.ebmwebsourcing.com/
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

package org.ow2.petals.launcher.util;

import java.io.File;
import java.io.IOException;

/**
 * A container locker; When the container is started, no other container can be
 * started on the same context.
 * <p>
 * Note that the locker cannot be based on the PID of the running program as
 * JAVA doesn't provide any method to get it.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Locker {

    private File file;

    private static final String FILE_NAME = "locked";

    /**
     * Creates a new {@link Locker}
     * 
     * @param rootPath
     */
    public Locker(File rootPath) {
        this.file = new File(rootPath, FILE_NAME);
    }

    /**
     * Test if the lock file is present
     * 
     * @return
     */
    public boolean isLocked() {
        return (file != null && file.exists());
    }

    /**
     * Create the lock file
     * 
     */
    public void lock() {
        try {
            if (file != null) {
                file.createNewFile();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Remove the lock file
     * 
     */
    public void unlock() {
        if (isLocked()) {
            file.delete();
        }
    }
}
