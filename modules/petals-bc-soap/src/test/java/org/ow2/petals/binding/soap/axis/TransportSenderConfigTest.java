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

package org.ow2.petals.binding.soap.axis;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.axis2.transport.http.CommonsHTTPTransportSender;
import org.junit.Test;

import com.ebmwebsourcing.easycommons.xml.XMLComparator;

public class TransportSenderConfigTest {

    private static final String TRANSPORTSENDER_CONFIG_TEST0 = "TransportSenderConfigTest0.xml";

    @Test
    public void testDumpXml() throws Exception {
        InputStream expected = getClass().getClassLoader().getResourceAsStream(
                TRANSPORTSENDER_CONFIG_TEST0);

        TransportSenderConfig config = new TransportSenderConfig("http",
                CommonsHTTPTransportSender.class);
        config.addParameter("Protocol", "HTTP/1.1");
        config.addParameter("Transfer-Encoding", "chunked");

        StringWriter sw = new StringWriter();
        config.dump(sw);
        assertTrue(XMLComparator.isEquivalent(expected, new ByteArrayInputStream(sw.getBuffer()
                .toString().getBytes())));

    }

}
