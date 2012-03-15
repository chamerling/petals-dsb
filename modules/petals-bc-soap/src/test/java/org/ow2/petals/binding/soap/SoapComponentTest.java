
package org.ow2.petals.binding.soap;

import static org.junit.Assert.assertEquals;

import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.FlowAttributes;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis2.util.Utils;
import org.junit.Test;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfOperation;
import org.ow2.petals.binding.soap.axis.Axis2TestHelper;
import org.ow2.petals.commons.logger.ConsumeFlowStepBeginLogData;
import org.ow2.petals.component.api.Component;
import org.ow2.petals.component.api.ComponentConfiguration;
import org.ow2.petals.component.api.Message;
import org.ow2.petals.component.api.ServiceConfiguration;
import org.ow2.petals.component.api.ServiceConfiguration.ServiceType;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.logger.Level;
import com.ebmwebsourcing.jbi.adaptor.impl.AbstractJBIComponentTest;
import com.ebmwebsourcing.jbi.adaptor.impl.ComponentType;
import com.ebmwebsourcing.jbi.adaptor.impl.JbiConstants;
import com.ebmwebsourcing.jbi.adaptor.impl.WrappedComponent;
import com.ebmwebsourcing.jbi.adaptor.impl.WrappedRequestToProviderMessage;
import com.ebmwebsourcing.jbi.adaptor.impl.WrappedStatusFromConsumerMessage;

public class SoapComponentTest extends AbstractJBIComponentTest {

    private static final QName TEST_OPERATION = new QName("http://act.org/", "echoString");

    private static final String SERVICE_NAMESPACE = "http://act.org/";

    private static final QName INTERFACE_QNAME = new QName(SERVICE_NAMESPACE, "Echo");

    private static final QName SERVICE_QNAME = new QName(SERVICE_NAMESPACE, "EchoService");

    private static final String OPERATION = "echoString";

    private static final QName EXPECTED_OPERATION_QNAME = new QName(SERVICE_NAMESPACE, OPERATION);

    private static final String EXPOSED_SERVICE_NAME = "testService";

    private static final String ENDPOINT_NAME = "Echo";

    private static final String HTTP_HOST;

    static {
        try {
            HTTP_HOST = Utils.getIpAddress();
        } catch (SocketException se) {
            throw new UncheckedException(se);
        }
    }

    @Override
    protected ComponentConfiguration createComponentConfiguration(Logger logger) {
        URL addressingMarUrl = Thread.currentThread().getContextClassLoader()
                .getResource("addressing-1.5.6.mar");
        assert addressingMarUrl != null;

        ComponentConfiguration componentConfiguration = super.createComponentConfiguration(logger);
        componentConfiguration.setParameter("{" + SoapConstants.Component.NS_URI + "}"
                + SoapConstants.HttpServer.HTTP_HOSTNAME, HTTP_HOST);
        componentConfiguration.setParameter("{" + SoapConstants.Component.NS_URI + "}"
                + SoapConstants.HttpServer.HTTP_PORT,
                String.valueOf(SoapConstants.HttpServer.DEFAULT_HTTP_PORT));
        componentConfiguration.setParameter("{" + SoapConstants.Component.NS_URI + "}"
                + SoapConstants.HttpServer.HTTP_SERVICES_CONTEXT,
                SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_CONTEXT);
        componentConfiguration.setParameter("{" + SoapConstants.Component.NS_URI + "}"
                + SoapConstants.HttpServer.HTTP_SERVICES_MAPPING,
                SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_MAPPING);
        componentConfiguration.addResource(addressingMarUrl);
        return componentConfiguration;
    }

    @Override
    public Component createComponentUnderTest(ComponentConfiguration componentConfiguration) {
        return new WrappedComponent(ComponentType.BC_SOAP, componentConfiguration);
    }

    @Override
    public Message createMessageToProcessAsProvider(
            ServiceConfiguration testProvideServiceConfiguration) throws Exception {
        String payload = "<get />";

        return new WrappedRequestToProviderMessage(testProvideServiceConfiguration, TEST_OPERATION,
                AbsItfOperation.MEPPatternConstants.IN_OUT.value(),
                createPayloadInputStream(payload), new FlowAttributes("testFlowInstanceId",
                        "testFlowStepId"));
    }

    @Override
    public Message createTestStatusMessage(ServiceConfiguration testProvideServiceConfiguration,
            ExchangeStatus status) throws Exception {
        String payload = "";

        return new WrappedStatusFromConsumerMessage(testProvideServiceConfiguration,
                TEST_OPERATION, AbsItfOperation.MEPPatternConstants.IN_OUT.value(),
                createPayloadInputStream(payload), new FlowAttributes("testFlowInstanceId",
                        "testFlowStepId"), status);
    }

    @Override
    protected Map<String, String> generateExternalEvent() throws Exception {
        SOAPMessage soap11Message = createTestSoap11Message(OPERATION, new String[] {},
                new String[] {});

        final String requestUrl = generateRequestedUrl(EXPOSED_SERVICE_NAME, HTTP_HOST);

        Axis2TestHelper.sendSoapMessageOverHttp(requestUrl, soap11Message);

        Map<String, String> specificParams = new HashMap<String, String>();
        specificParams.put(ConsumeFlowStepBeginLogData.FLOW_OPERATION_NAME_PROPERTY_NAME,
                EXPECTED_OPERATION_QNAME.toString());
        specificParams.put(SoapConsumeFlowStepBeginLogData.REQUESTED_URL_KEY, requestUrl);

        return specificParams;
    }

    private static final SOAPMessage createTestSoap11Message(String soapBodyElementName,
            String[] attachmentContentIds, String[] textAttachmentsContent) {
        assert attachmentContentIds.length == textAttachmentsContent.length;
        try {
            MessageFactory mf11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage message = mf11.createMessage();

            SOAPBody body = message.getSOAPBody();
            QName bodyName = new QName(soapBodyElementName);
            body.addBodyElement(bodyName);

            for (int i = 0; i < attachmentContentIds.length; ++i) {
                AttachmentPart attachment = message.createAttachmentPart();
                attachment.setContentId(attachmentContentIds[i]);
                attachment.setContent(textAttachmentsContent[i], "text/plain");
                message.addAttachmentPart(attachment);
            }

            return message;
        } catch (SOAPException e) {
            throw new UncheckedException(e);
        }
    }

    @Override
    @Test(timeout = 300000)
    public void testProcessMessageAsConsumerLogsNothingBecauseMonitDisabled() throws Exception {
        this.componentUnderTest.start();

        ServiceConfiguration consumeServiceConfiguration = createTestConsumeServiceConfiguration();
        this.componentUnderTest.installService(consumeServiceConfiguration);
        ServiceConfiguration provideServiceConfiguration = createTestProvideServiceConfiguration();
        this.componentUnderTest.installService(provideServiceConfiguration);

        this.logger.setLevel(Level.INFO);

        generateExternalEvent();

        this.componentUnderTest.pollRequestFromConsumer();

        List<LogRecord> records = this.testHandler.getAllRecords(Level.MONIT);
        assertEquals(0, records.size());
    }

    @Override
    protected ServiceConfiguration createTestConsumeServiceConfiguration() throws Exception {
        ServiceConfiguration testProvideServiceConguration = new ServiceConfiguration(
                "testServiceConsume", INTERFACE_QNAME, SERVICE_QNAME, ENDPOINT_NAME,
                ServiceType.CONSUME);
        testProvideServiceConguration.setParameter("{" + SoapConstants.Component.NS_URI + "}"
                + SoapConstants.ServiceUnit.SERVICE_NAME, EXPOSED_SERVICE_NAME);
        testProvideServiceConguration.setParameter("{" + JbiConstants.CDK_NAMESPACE_URI + "}"
                + "timeout", "1000");

        return testProvideServiceConguration;
    }

    @Override
    protected ServiceConfiguration createTestProvideServiceConfiguration() throws Exception {
        URL wsdlUrl = Thread.currentThread().getContextClassLoader()
                .getResource("sampleWsdl-1.wsdl");
        ServiceConfiguration testProvideServiceConguration = new ServiceConfiguration(
                "testServiceProvide", INTERFACE_QNAME, SERVICE_QNAME, ENDPOINT_NAME,
                ServiceType.PROVIDE, wsdlUrl);
        testProvideServiceConguration.addResource(wsdlUrl);
        return testProvideServiceConguration;
    }

    private String generateRequestedUrl(String serviceName, String ipAddress)
            throws SocketException {
        return String.format("http://%s:%s/%s/%s/%s", ipAddress,
                SoapConstants.HttpServer.DEFAULT_HTTP_PORT,
                SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_CONTEXT,
                SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_MAPPING, serviceName);
    }

}
