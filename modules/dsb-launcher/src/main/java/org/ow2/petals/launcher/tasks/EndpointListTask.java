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

import javax.xml.namespace.QName;

import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.api.server.PetalsServer;
import org.ow2.petals.kernel.api.service.ServiceEndpoint;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointListTask extends Task {

    protected PetalsServer petalsServer;

    /**
     * 
     *
     */
    public EndpointListTask(PetalsServer petalsServer) {
        this.petalsServer = petalsServer;

        this.setName("eplist");
        this.setShortcut("l");
        this.setDescription("Display the endpoint list");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doProcess(List<String> args) {
        try {
            List<ServiceEndpoint> list = this.petalsServer.getServiceEndpoints(true);
            System.out.println("Global Endpoints list");
            System.out.println("=====================");
            System.out.println();

            if ((list != null) && (list.size() > 0)) {
                int i = 1;
                for (ServiceEndpoint serviceEndpoint : list) {
                    System.out.println(" + Endpoint #" + i++);
                    System.out.println("  - Name       : " + serviceEndpoint.getEndpointName());
                    System.out.println("  - Service    : " + serviceEndpoint.getServiceName());
                    System.out.print("  - Interfaces : ");
                    if (serviceEndpoint.getInterfacesName() != null) {
                        for (int j = 0; j < serviceEndpoint.getInterfacesName().size(); j++) {
                            QName itf = serviceEndpoint.getInterfacesName().get(j);
                            System.out.print(itf);
                            if (j < serviceEndpoint.getInterfacesName().size()) {
                                System.out.print(", ");
                            }
                        }
                        System.out.println();
                    } else {
                        System.out.println("Undefined");
                    }
                    System.out
                            .print("  - Location   : " + serviceEndpoint.getLocation().toString());
                    System.out.println();
                }
            } else {
                System.out.println("No endpoints");
            }
        } catch (PetalsException e) {
            e.printStackTrace(System.err);
        }
        return OK_CODE;
    }

}
