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
package org.ow2.petals.tools.generator.rest2jbi;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class Main {

    /**
     * @param args
     * @throws JBIGenerationException
     */
    public static void main(String[] args) throws JBIGenerationException {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.COMPONENT_VERSION, "1.0");
        REST2Jbi rest2Jbi = new REST2Jbi(URI.create("http://localhost:8080/foo/bar"), null, map);
        rest2Jbi.generate();

    }

}
