/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
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
package org.ow2.petals.tools.generator.jbi.ws2jbi;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class Main {
    public static void main(String[] args) throws Exception {

        if ((args != null) && (args.length >= 2)) {
            String wsdl = args[0];
            String version = args[1];

            Map<String, String> map = new HashMap<String, String>();
            map.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION, version);
            if (args.length >= 3) {
                String outputPath = args[2];
                map.put(Constants.OUTPUT_DIR, outputPath);
            }
            URI wsdlURI = new URI(wsdl);
            File f = new WS2Jbi(wsdlURI, map).generate();
            System.out.println("Service Assembly is available at " + f.getAbsolutePath());
        } else {
            printUsage();
            throw new Exception("Bad number of arguments");
        }
    }

    private static void printUsage() {
        System.out
                .println(Main.class.getCanonicalName() + "wsdl-uri component-version output-path");
    }
}
