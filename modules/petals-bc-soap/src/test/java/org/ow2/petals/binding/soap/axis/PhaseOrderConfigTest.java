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

public class PhaseOrderConfigTest {

    private static final String PHASEORDER_CONFIG_TEST0 = "PhaseOrderConfigTest0.xml";

    @Test
    public void testDumpXml() throws Exception {
        InputStream expected = getClass().getClassLoader().getResourceAsStream(
                PHASEORDER_CONFIG_TEST0);

        HandlerConfig requestURIBasedDispatcherConfig = new HandlerConfig(
                "RequestURIBasedDispatcher",
                org.apache.axis2.dispatchers.RequestURIBasedDispatcher.class);
        HandlerConfig soapActionBasedDispatcherConfig = new HandlerConfig(
                "SOAPActionBasedDispatcher",
                org.apache.axis2.dispatchers.SOAPActionBasedDispatcher.class);

        PhaseConfig transportPhaseConfig = new PhaseConfig("Transport");
        transportPhaseConfig.addHandler(requestURIBasedDispatcherConfig);
        transportPhaseConfig.addHandler(soapActionBasedDispatcherConfig);

        PhaseConfig securityPhaseConfig = new PhaseConfig("Security");
        PhaseConfig preDispatchPhaseConfig = new PhaseConfig("PreDispatch");

        HandlerConfig requestURIOperationDispatcherConfig = new HandlerConfig(
                "RequestURIOperationDispatcher",
                org.apache.axis2.dispatchers.RequestURIOperationDispatcher.class);

        HandlerConfig soapMessageBodyBasedDispatcherConfig = new HandlerConfig(
                "SOAPMessageBodyBasedDispatcher",
                org.apache.axis2.dispatchers.SOAPMessageBodyBasedDispatcher.class);

        PhaseConfig dispatchPhaseConfig = new PhaseConfig("Dispatch");
        dispatchPhaseConfig.addHandler(requestURIOperationDispatcherConfig);
        dispatchPhaseConfig.addHandler(soapMessageBodyBasedDispatcherConfig);

        PhaseOrderConfig config = new PhaseOrderConfig("InFlow");
        config.addPhase(transportPhaseConfig);
        config.addPhase(securityPhaseConfig);
        config.addPhase(preDispatchPhaseConfig);
        config.addPhase(dispatchPhaseConfig);

        StringWriter sw = new StringWriter();
        config.dump(sw);
        assertTrue(XMLComparator.isEquivalent(expected, new ByteArrayInputStream(sw.getBuffer()
                .toString().getBytes())));

    }

}
