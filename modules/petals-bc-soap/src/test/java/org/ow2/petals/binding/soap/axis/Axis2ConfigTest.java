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

import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOutMessageReceiver;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.junit.Test;

import com.ebmwebsourcing.easycommons.xml.XMLComparator;

public class Axis2ConfigTest {

    private static final String AXIS2_CONFIG_TEST0 = "Axis2ConfigTest0.xml";

    @Test
    public void testDumpXml() throws Exception {
        InputStream expected = getClass().getClassLoader().getResourceAsStream(AXIS2_CONFIG_TEST0);
        Axis2Config axis2Config = new Axis2Config("axis2Config");

        axis2Config.addParameter("hotdeployment", "true");
        axis2Config.addParameter("hotupdate", "false");

        MessageReceiverConfig inOnlyReceiverConfig = new MessageReceiverConfig(
                "http://www.w3.org/2004/08/wsdl/in-only", RawXMLINOnlyMessageReceiver.class);
        MessageReceiverConfig inOutReceiverConfig = new MessageReceiverConfig(
                "http://www.w3.org/2004/08/wsdl/in-out", RawXMLINOutMessageReceiver.class);

        axis2Config.addMessageReceiver(inOnlyReceiverConfig);
        axis2Config.addMessageReceiver(inOutReceiverConfig);

        TransportReceiverConfig httpServerReceiverConfig = new TransportReceiverConfig("http",
                SimpleHTTPServer.class);
        httpServerReceiverConfig.addParameter("port", "8080");

        axis2Config.addTransportReceiver(httpServerReceiverConfig);

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

        PhaseOrderConfig inFlowConfig = new PhaseOrderConfig("InFlow");
        inFlowConfig.addPhase(transportPhaseConfig);
        inFlowConfig.addPhase(securityPhaseConfig);
        inFlowConfig.addPhase(preDispatchPhaseConfig);
        inFlowConfig.addPhase(dispatchPhaseConfig);

        axis2Config.addPhaseOrder(inFlowConfig);

        StringWriter sw = new StringWriter();
        axis2Config.dump(sw);
        assertTrue(XMLComparator.isEquivalent(expected, new ByteArrayInputStream(sw.getBuffer()
                .toString().getBytes())));

    }

}
