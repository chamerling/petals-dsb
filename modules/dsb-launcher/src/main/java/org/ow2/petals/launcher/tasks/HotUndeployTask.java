/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.launcher.tasks;

import java.io.File;
import java.util.List;

import org.ow2.petals.jmx.JMXClient;

/**
 * 
 * FIXME : Use {@link JMXClient} instead of file copy!
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class HotUndeployTask extends Task {

    private final File installedPath;

    /**
     * 
     * @param installPath
     * @param uninstalledPath
     */
    public HotUndeployTask(File installedPath) {
        this.installedPath = installedPath;

        this.setShortcut("hu");
        this.setName("hotundeploy");
        this
                .setDescription("Shutdown and uninstall a Component, a Service Assembly or a Share Library (possible args [ZIP fileName])");
    }

    @Override
    public int doProcess(List<String> args) {
        String zip = args.get(0);
        File installedFile = new File(this.installedPath, zip);
        installedFile.delete();

        return OK_CODE;
    }

    @Override
    public boolean validateArgs(List<String> args) {
        boolean result = false;
        if ((args != null) && (args.size() > 0)) {
            File f = new File(this.installedPath, args.get(0));
            result = f.exists();
            if (!result) {
                System.out.println("File to undeploy not found : " + f.getAbsolutePath());
            }
        }
        return result;
    }
}
