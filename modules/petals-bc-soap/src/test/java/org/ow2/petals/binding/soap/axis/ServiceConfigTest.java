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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Test;

import com.ebmwebsourcing.easycommons.xml.XMLComparator;

public class ServiceConfigTest {

    private static final String SERVICE_CONFIG_TEST0 = "ServiceConfigTest0.xml";

    @Test
    public void testDumpXml() throws Exception {
        InputStream expected = getClass().getClassLoader()
                .getResourceAsStream(SERVICE_CONFIG_TEST0);
        OperationConfig operationConfig1 = new OperationConfig("myOperation1");
        operationConfig1.addActionMapping("canBeASoapAction");
        operationConfig1.addActionMapping("canBeAWsaAction");
        OperationConfig operationConfig2 = new OperationConfig("myOperation2");

        ServiceConfig config = new ServiceConfig("MyService");
        config.addOperation(operationConfig1);
        config.addOperation(operationConfig2);

        config.addTransport("http");

        StringWriter sw = new StringWriter();
        config.dump(sw);

        assertTrue(XMLComparator.isEquivalent(expected, new ByteArrayInputStream(sw.getBuffer()
                .toString().getBytes())));

    }

}
