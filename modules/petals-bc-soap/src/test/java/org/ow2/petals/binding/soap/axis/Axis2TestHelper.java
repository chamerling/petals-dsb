
package org.ow2.petals.binding.soap.axis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.transport.http.CommonsHTTPTransportSender;
import org.apache.axis2.transport.http.SimpleHTTPServer;

import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_DISPATCH_PHASE_NAME;
import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_INFAULTFLOW_PHASEORDER_NAME;
import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_INFLOW_PHASEORDER_NAME;
import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_PREDISPATCH_PHASE_NAME;
import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_SECURITY_PHASE_NAME;
import static org.ow2.petals.binding.soap.axis.Constants.AXIS2_TRANSPORT_PHASE_NAME;

import com.ebmwebsourcing.easycommons.io.FileSystemHelper;
import com.ebmwebsourcing.easycommons.lang.UncheckedException;

public class Axis2TestHelper {

    private static final Axis2Config createEmptyAxis2ConfigWithParameters() {
        Axis2Config axis2Config = new Axis2Config("axis2Config");
        axis2Config.addParameter("hotdeployment", "true");
        axis2Config.addParameter("hotupdate", "false");
        axis2Config.addParameter("contextRoot", "/");
        axis2Config.addParameter("servicePath", "services");
        return axis2Config;
    }

    private static final PhaseOrderConfig createMinimalInFlowPhaseOrder() {
        HandlerConfig requestURIBasedDispatcherConfig = new HandlerConfig(
                "RequestURIBasedDispatcher",
                org.apache.axis2.dispatchers.RequestURIBasedDispatcher.class);

        PhaseConfig transportPhaseConfig = new PhaseConfig(AXIS2_TRANSPORT_PHASE_NAME);
        transportPhaseConfig.addHandler(requestURIBasedDispatcherConfig);

        PhaseConfig securityPhaseConfig = new PhaseConfig(AXIS2_SECURITY_PHASE_NAME);
        PhaseConfig preDispatchPhaseConfig = new PhaseConfig(AXIS2_PREDISPATCH_PHASE_NAME);

        HandlerConfig soapMessageBodyBasedDispatcherConfig = new HandlerConfig(
                "SOAPMessageBodyBasedDispatcher",
                org.apache.axis2.dispatchers.SOAPMessageBodyBasedDispatcher.class);

        PhaseConfig dispatchPhaseConfig = new PhaseConfig(AXIS2_DISPATCH_PHASE_NAME,
                org.apache.axis2.engine.DispatchPhase.class);
        dispatchPhaseConfig.addHandler(soapMessageBodyBasedDispatcherConfig);

        PhaseOrderConfig inFlowConfig = new PhaseOrderConfig(AXIS2_INFLOW_PHASEORDER_NAME);
        inFlowConfig.addPhase(transportPhaseConfig);
        inFlowConfig.addPhase(securityPhaseConfig);
        inFlowConfig.addPhase(preDispatchPhaseConfig);
        inFlowConfig.addPhase(dispatchPhaseConfig);
        return inFlowConfig;
    }

    private static final PhaseOrderConfig createMinimalInFaultFlowPhaseOrder() {
        PhaseConfig preDispatchPhaseConfig = new PhaseConfig(AXIS2_PREDISPATCH_PHASE_NAME);

        HandlerConfig requestURIBasedDispatcherConfig = new HandlerConfig(
                "RequestURIBasedDispatcher",
                org.apache.axis2.dispatchers.RequestURIBasedDispatcher.class);
        PhaseConfig dispatchPhaseConfig = new PhaseConfig(AXIS2_DISPATCH_PHASE_NAME,
                org.apache.axis2.engine.DispatchPhase.class);
        dispatchPhaseConfig.addHandler(requestURIBasedDispatcherConfig);

        PhaseOrderConfig inFaultFlowConfig = new PhaseOrderConfig(AXIS2_INFAULTFLOW_PHASEORDER_NAME);
        inFaultFlowConfig.addPhase(preDispatchPhaseConfig);
        inFaultFlowConfig.addPhase(dispatchPhaseConfig);
        return inFaultFlowConfig;
    }

    public static final Axis2Server createMinimalAxis2Server(
            Class<? extends MessageReceiver> messageReceiverClass) {

        Axis2Config axis2Config = createEmptyAxis2ConfigWithParameters();
        for (String mepUri : new String[] { WSDL2Constants.MEP_URI_IN_ONLY,
                WSDL2Constants.MEP_URI_IN_OUT }) {
            axis2Config.addMessageReceiver(new MessageReceiverConfig(mepUri, messageReceiverClass));
        }

        TransportReceiverConfig httpServerReceiverConfig = new TransportReceiverConfig("http",
                SimpleHTTPServer.class);
        httpServerReceiverConfig.addParameter("port", "7899");

        axis2Config.addTransportReceiver(httpServerReceiverConfig);

        TransportSenderConfig httpSenderConfig = new TransportSenderConfig("http",
                CommonsHTTPTransportSender.class);
        httpSenderConfig.addParameter("Protocol", "HTTP/1.1");
        httpSenderConfig.addParameter("Transfer-Encoding", "chunked");

        axis2Config.addTransportSender(httpSenderConfig);

        axis2Config.addPhaseOrder(createMinimalInFlowPhaseOrder());
        axis2Config.addPhaseOrder(createMinimalInFaultFlowPhaseOrder());

        File tempDir;
        try {
            tempDir = FileSystemHelper.createTempDir();
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        Axis2Server axis2Server = new Axis2Server(tempDir, axis2Config);
        return axis2Server;

    }

    public static final Axis2Client createMinimalAxis2Client(URL wsdlURL, QName wsdlServiceName,
            String portName, Logger logger) {

        Axis2Config axis2Config = createEmptyAxis2ConfigWithParameters();

        TransportSenderConfig httpSenderConfig = new TransportSenderConfig("http",
                CommonsHTTPTransportSender.class);
        httpSenderConfig.addParameter("Protocol", "HTTP/1.1");
        httpSenderConfig.addParameter("Transfer-Encoding", "chunked");

        axis2Config.addTransportSender(httpSenderConfig);
        
        axis2Config.addPhaseOrder(createMinimalInFlowPhaseOrder());
        axis2Config.addPhaseOrder(createMinimalInFaultFlowPhaseOrder());
        
        File tempDir;
        try {
            tempDir = FileSystemHelper.createTempDir();
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        Axis2Client axis2Client = new Axis2Client(tempDir, axis2Config, wsdlURL, wsdlServiceName, portName, logger);
        return axis2Client;
    }

    public static final SOAPMessage sendSoapMessageOverHttp(String urlStr, SOAPMessage soapMessage)
            throws Exception {
        final URL url = new URL(urlStr);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage response = soapConnection.call(soapMessage, url);
        return response;
    }

}
