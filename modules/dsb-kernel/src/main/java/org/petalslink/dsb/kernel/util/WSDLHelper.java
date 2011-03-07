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
package org.petalslink.dsb.kernel.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class WSDLHelper {

    static WSDLReader reader;

    static {
        try {
            reader = WSDLFactory.newInstance().newWSDLReader();
        } catch (WSDLException e) {
            e.printStackTrace();
        }
    }

    public static Description readWSDL(URI wsdlURI) {
        Description description = null;
        try {
            description = reader.read(wsdlURI.toURL());
        } catch (WSDLException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (URISyntaxException e) {
        }
        return description;
    }

}
