/**
 * PETALS: PETALS Services Platform
 * Copyright (C) 2005 EBM WebSourcing
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA.
 *
 * Initial developer(s): EBM WebSourcing
 * --------------------------------------------------------------------------
 * $Id: Transporter.java,v 0.3 2005/07/22 10:24:27 alouis Exp $
 * --------------------------------------------------------------------------  
 */

package org.ow2.petals.transporter.api.transport;

import java.util.UUID;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.base.fractal.api.FractalComponent;
import org.ow2.petals.exchange.api.Exchange;



@Interface(name="transporter")
public interface Transporter extends FractalComponent {
	
	TransportContext getContext();
	
	void setContext(TransportContext context);
	
	void push(Exchange exchange, QName destinationName) throws TransportException;
		
	Exchange pull(QName providerEndpointName, QName nodeEndpointName) throws TransportException;

	Exchange pull(UUID id, QName providerEndpointName, QName nodeEndpointName) throws TransportException;

	void stop();
}
