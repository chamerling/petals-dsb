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

import java.util.List;

/**
 * NOTE : The command line interface SHOULD be a separate process and not hosted
 * by petals. PEtALS MUST run on its side and the tasks connects to the JMX
 * interface to process admin tasks from command line!
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public abstract class Task {

    public final static int STOP_CODE = 0;

    public final static int ERROR_CODE = -1;

    public final static int OK_CODE = 1;

    public final static int INVALID_ARGS = -2;

    private String name;

    private String shortcut;

    private String description;

    /**
     * 
     * @param args
     */
    public int process(List<String> args) {
        int result = OK_CODE;
        if (!this.validateArgs(args)) {
            result = INVALID_ARGS;
        } else {
            result = this.doProcess(args);
        }
        return result;
    }

    protected abstract int doProcess(List<String> args);

    /**
     * 
     * @param args
     * @return
     */
    public boolean validateArgs(List<String> args) {
        return true;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String prefix = this.shortcut + ", " + this.name;
        String blank = "";
        int length = 25 - prefix.length();
        for (int i = 0; i < length; i++) {
            blank += " ";
        }
        return prefix + blank + this.description;
    }
}
