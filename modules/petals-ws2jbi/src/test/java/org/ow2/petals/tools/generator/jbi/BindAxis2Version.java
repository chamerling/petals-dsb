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
package org.ow2.petals.tools.generator.jbi;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.ow2.petals.tools.generator.jbi.ws2jbi.WS2Jbi;

public class BindAxis2Version {

    public static void main(String[] args) throws JBIGenerationException, URISyntaxException {
        String wsdl = "http://localhost:8080/axis2/services/Version?wsdl";
        Map<String, String> map = new HashMap<String, String>();
        // let's say that we want to generate SA for SOAP component 4.0
        map.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION, "4.0");
        URI wsdlURI = new URI(wsdl);
        File f = new WS2Jbi(wsdlURI, map).generate();
        System.out.println("Service Assembly is available at " + f.getAbsolutePath());
    }

}
