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
package org.ow2.petals.binding.soap.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import junit.framework.TestCase;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlReader;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.petals.commons.threadlocal.DocumentBuilders;
import org.w3c.dom.Document;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class WsdlHelperTest extends TestCase {

    public void testChangeAddressInWSDL() {
        final String address = "http://petals.ow2.org/chamerling/Service";
        WSDL4ComplexWsdlReader reader = null;
        try {
            final WSDL4ComplexWsdlFactory wsdlFactory = WSDL4ComplexWsdlFactory.newInstance();
            reader = wsdlFactory.newWSDLReader();
            assertNotNull(reader);
        } catch (final WSDL4ComplexWsdlException e) {
            fail(e.getMessage());
        }

        final URL serviceURL = this.getClass().getResource("/service01.wsdl");
        assertNotNull(serviceURL);

        final DocumentBuilder builder = DocumentBuilders.getDefaultDocumentBuilder();
        Document doc = null;
        try {
            doc = builder.parse(serviceURL.toURI().toString());
            assertNotNull(doc);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

        Document result = null;
        try {
            result = WsdlHelper.replaceServiceAddressInWSDL(doc, address);
            assertNotNull(result);
        } catch (final WSDL4ComplexWsdlException e) {
            fail(e.getMessage());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        // check that the address has been changed...
        Description desc = null;
        if (reader != null) {
            try {
                desc = reader.read(result);
            } catch (final WSDL4ComplexWsdlException e) {
                fail(e.getMessage());
            } catch (final URISyntaxException e) {
                fail(e.getMessage());
            }
            final java.util.List<Service> services = desc.getServices();

            for (final Service service : services) {
                final List<Endpoint> endpoints = service.getEndpoints();
                for (final Endpoint endpoint : endpoints) {
                    System.out.println("Check endpoint " + endpoint.getAddress());
                    assertEquals(endpoint.getAddress(), address);
                    System.out.println("OK");
                }
            }
        }
    }
}
