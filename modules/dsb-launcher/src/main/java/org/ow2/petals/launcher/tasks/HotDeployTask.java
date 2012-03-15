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
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.jmx.JMXClient;

/**
 * 
 * Created on 13 févr. 08
 * 
 * FIXME : Use {@link JMXClient} instead of file copy!
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class HotDeployTask extends Task {

    private final File currentPath;

    private final File installPath;

    /**
     * 
     * @param currentPath
     * @param installPath
     */
    public HotDeployTask(File currentPath, File installPath) {
        this.currentPath = currentPath;
        this.installPath = installPath;

        this.setShortcut("hd");
        this.setName("hotdeploy");
        this
                .setDescription("Install and start a Component, a Service Assembly or a Share Library (possible args [ZIP filePath])");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.util.Task#process(java.util.List)
     */
    @Override
    public int doProcess(List<String> args) {
        String zip = args.get(0);
        File source = new File(zip);
        if (!source.exists()) {
            source = new File(this.currentPath, zip);
        }

        try {
            FileUtils.copyFileToDirectory(source, this.installPath);
            Thread.sleep(1000);
        } catch (IOException e) {
            System.out.println("File found, but can not copy and deploy file");
            e.printStackTrace(System.err);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return OK_CODE;
    }

    @Override
    public boolean validateArgs(List<String> args) {
        boolean result = false;
        if ((args != null) && (args.size() > 0)) {
            File f = new File(args.get(0));
            result = f.exists();
            if (!result) {
                System.out.println("File not found : " + f.getAbsolutePath());
            }
        }
        return result;
    }
}
