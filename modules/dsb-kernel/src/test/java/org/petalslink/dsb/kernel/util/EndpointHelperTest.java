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

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.petalslink.dsb.kernel.util.EndpointHelper;
import org.petalslink.dsb.ws.api.ServiceEndpoint;

import junit.framework.TestCase;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointHelperTest extends TestCase {

    public void testGetEndpoints() throws Exception {
        URL url = EndpointHelperTest.class.getResource("/TestService.wsdl");
        assertNotNull(url);
        List<ServiceEndpoint> result = EndpointHelper.getEndpoints(url.toURI());
        assertNotNull(result);
        assertEquals(1, result.size());
        for (ServiceEndpoint serviceEndpoint : result) {
            System.out.println("Endpoint : " + serviceEndpoint.getEndpoint());
            System.out.println("Interface : " + serviceEndpoint.getItf());
            System.out.println("Service : " + serviceEndpoint.getService());
        }
    }

    public void testGetRESTEndpoint() throws Exception {
        String s = "http://weather.yahooapis.com/";
        URI uri = new URI(s);
        ServiceEndpoint serviceEndpoint = EndpointHelper.getRESTEndpoint(uri);
        assertNotNull(serviceEndpoint);
        assertEquals("RestPlatformweatheryahooapiscomEndpoint", serviceEndpoint.getEndpoint());

        assertEquals("weatheryahooapiscomService", serviceEndpoint.getService().getLocalPart());
        assertEquals(s, serviceEndpoint.getService().getNamespaceURI());

        assertEquals(s, serviceEndpoint.getItf().getNamespaceURI());
        assertEquals("weatheryahooapiscomInterface", serviceEndpoint.getItf().getLocalPart());

    }

}
