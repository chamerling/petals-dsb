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

import java.util.ArrayList;
import java.util.List;

import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.service.Service;

/**
 * 
 * Created on 13 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class InfoTask extends Task {


	private Node node = null;
	

	/**
     * 
     * @param node 
	 * @param petalsServer
     */
    public InfoTask(Node node) {
    	this.node = node;
        this.setShortcut("i");
        this.setName("info");
        this.setDescription("Display the local container information");

    }

    @Override
    public int doProcess(List<String> args) {
        try {
            System.out.println("Infos");
            if(node != null) {
            	List<String> components = new ArrayList<String>();
            	List<String> services = new ArrayList<String>();
            	List<String> providerEndpoints = new ArrayList<String>();
            	List<String> clientProxyEndpoints = new ArrayList<String>();
            	for(Endpoint ep: this.node.getRegistry().getLocalEndpoints()) {
            		Object e = ep.getComponent().getFcInterface("service");
            		if((e instanceof Provider)||(e instanceof ClientAndProvider)) {
            			components.add(ep.getQName().toString());
            		} else if(e instanceof Service) {
            			services.add(ep.getQName().toString());
            		} else if((e instanceof ProviderEndpoint)||(e instanceof ClientAndProviderEndpoint)) {
            			providerEndpoints.add(ep.getQName().toString());
            		} else if(e instanceof ClientEndpoint) {
            			clientProxyEndpoints.add(ep.getQName().toString());
            		} 
            	}
            	
            	System.out.println("\n\n\nLocal composition of node: " + this.node.getQName());
            	System.out.println("--------------------------\n");
            	
            	System.out.println("\n\nComponents:\n");
            	for(String comp: components) {
            		System.out.println("\t- " + comp);
            	}
            	
            	System.out.println("\n\nServices:\n");
            	for(String s: services) {
            		System.out.println("\t- " + s);
            	}
            	
            	System.out.println("\n\nProvider Endpoints:\n");
            	for(String pep: providerEndpoints) {
            		System.out.println("\t- " + pep);
            	}
            	System.out.println("\n\nClient Proxy Endpoints:\n");
            	for(String cep: clientProxyEndpoints) {
            		System.out.println("\t- " + cep);
            	}
            } else {
            	throw new ESBException("node cannot be null");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return OK_CODE;
    }
    
}
