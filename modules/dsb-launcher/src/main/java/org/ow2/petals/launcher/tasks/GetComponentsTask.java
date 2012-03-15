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

import javax.management.ObjectName;

import org.ow2.petals.jmx.JMXClient;

/**
 * 
 * Created on 14 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class GetComponentsTask extends Task {

    private JMXClient client;

    /**
     * 
     */
    public GetComponentsTask(JMXClient client) {
        this.client = client;

        this.setShortcut("c");
        this.setName("components");
        this.setDescription("Display the installed JBI components");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.tasks.Task#doProcess(java.util.List)
     */
    @Override
    protected int doProcess(List<String> args) {
        ObjectName[] objectNames = null;
        System.out.println("JBI components on local container");
        System.out.println("Binding Components List : ");
        try {
            objectNames = client.getAdminServiceClient().getBindingComponents();
            if (objectNames != null && objectNames.length > 0) {
                for (ObjectName name : objectNames) {
                    System.out.println(" - " + name);
                }
            } else {
                System.out.println(" - NO BINDING COMPONENT");
            }
        } catch (Exception e) {
            System.out.println("Error while retrieving Binding Components");
        }

        System.out.println("");
        System.out.println("Service Engines List : ");
        try {
            objectNames = client.getAdminServiceClient().getEngineComponents();
            if (objectNames != null && objectNames.length > 0) {
                for (ObjectName name : objectNames) {
                    System.out.println(" - " + name);
                }
            } else {
                System.out.println(" - NO SERVICE ENGINE");
            }

        } catch (Exception e) {
            System.out.println("Error while retrieving Service Engines");
        }

        return OK_CODE;
    }

}
