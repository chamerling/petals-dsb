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
package org.ow2.petals.tools.generator.commons;

import java.util.Map;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Creator {

	public static final String INTERFACE = "interface";
	public static final String INTERFACE_NS = "interfacens";

	public static final String SERVICE = "service";
	public static final String SERVICE_NS = "servicens";

	public static final String ENDPOINT = "endpoint";
	public static final String ENDPOINT_NS = "endpointns";

    public static final String LINK_TYPE = "linktype";

    public static final String SU_TYPE = "sutype";

    public static final String WSDLFILE = "cdk.wsdlfile";

    public static final String TIMEOUT = "cdk.timeout";

    public static final String OPERATION = "cdk.operation";

    // Component related things

    String getComponentName();

    String getComponentVersion();

    SuBean createSUConsume(Map<String, String> elements);

	SuBean createSUProvide(Map<String, String> elements);
}
