
package org.ow2.petals.binding.soap.listener.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.ow2.petals.binding.soap.SoapProvideExtFlowStepBeginLogData;
import org.ow2.petals.binding.soap.axis.Axis2Client;
import org.ow2.petals.binding.soap.axis.Axis2TestHelper;
import org.ow2.petals.commons.Constants;
import org.ow2.petals.commons.PetalsExecutionContext;
import org.ow2.petals.commons.logger.AbstractFlowLogData;

import com.ebmwebsourcing.easycommons.io.FileSystemHelper;
import com.ebmwebsourcing.easycommons.io.IOHelper;
import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.logger.Level;
import com.ebmwebsourcing.easycommons.logger.TestHandler;
import com.ebmwebsourcing.easycommons.thread.ExecutionContext;
import com.ebmwebsourcing.easycommons.uuid.SimpleUUIDGenerator;

public class Axis2ClientTest {

    private static final String TEST_FLOW_INSTANCE_ID = "testFlowInstanceId";

    private static MockWebService mockWebService;

    private static final URL SAMPLE_WSDL_1_URL = Axis2ClientTest.class.getClassLoader()
            .getResource("sampleWsdl-1.wsdl");

    private static final QName SAMPLE_WSDL_1_SERVICE_NAME = new QName("http://act.org/",
            "EchoService");

    private static final String SAMPLE_WSDL_1_PORT_NAME = "Echo";

    private static final String SAMPLE_WSDL_1_SERVICE_URL = "http://localhost:7856/ACTSoap/Echo";

    private static final String SOAP_BODY = "<param0>nimportequoi</param0>";

    private static final String OPERATION_NAME_IN_OUT = "echoString";

    private static final String OPERATION_NAME_IN_ONLY = "echoVoid";

    private static String TEST_PETALS_FILEHANDLER_BASEDIR;

    private static Axis2Client createAxis2Client(URL wsdlUrl, QName wsdlServiceName,
            String portName, Logger logger) {
        Axis2Client a2c = Axis2TestHelper.createMinimalAxis2Client(wsdlUrl, wsdlServiceName,
                portName, logger);
        a2c.setUp();
        return a2c;
    }

    private static MockWebService createMockWebService(URL serviceUrl) {
        return new MockSoapWebService(serviceUrl);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        TEST_PETALS_FILEHANDLER_BASEDIR = FileSystemHelper.createTempDir(
                "testPetalsFileHandlerBaseDir").getAbsolutePath();
        try {
            mockWebService = createMockWebService(new URL(SAMPLE_WSDL_1_SERVICE_URL));
            mockWebService.start();
        } catch (MalformedURLException mue) {
            throw new UncheckedException(mue);
        }
        PetalsExecutionContext.putFlowInstanceId(TEST_FLOW_INSTANCE_ID);
        PetalsExecutionContext.putFlowStepId(new SimpleUUIDGenerator().getNewID());
        PetalsExecutionContext.putPetalsFileHandlerBaseDir(TEST_PETALS_FILEHANDLER_BASEDIR);
    }

    @AfterClass
    public static void afterClass() {
        mockWebService.stop();
        ExecutionContext.getProperties().clear();

        System.clearProperty(Constants.PETALS_HOME_PROPERTY_NAME);
    }

    @Test
    public void testLogOnOutgoingRequestMonitDisabledInOutMode() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestInOutMep(OPERATION_NAME_IN_OUT, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);
        assertEquals(0, monitRecords.size());
    }

    @Test
    @Ignore
    public void testLogOnOutgoingRequestWithoutAttachmentMonitEnabledInOutMode() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);
        testLogger.setLevel(Level.MONIT);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestInOutMep(OPERATION_NAME_IN_OUT, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);

        makeAssertForTestingMonitTraceLevel(monitRecords);
    }

    @Test
    @Ignore
    public void testLogOnOutgoingRequestWithAttachmentsMonitEnabled() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);
        testLogger.setLevel(Level.MONIT);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);
        String operationName = "echoString";
        String soapBody = "<act:echoStringRequest xmlns:act='http://act.org/'><act:param0 xmlns='http://act.org/'>nimportequoi</act:param0> "
                + "<act:binaryData></act:binaryData></act:echoStringRequest>";

        a2c.submitRequestWithAttachments(operationName, soapBody, null);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);
        assertEquals(1, monitRecords.size());

        SoapProvideExtFlowStepBeginLogData soapRequestLogData = extractOutgoingSoapRequestLogData(monitRecords
                .get(0));

        SoapProvideExtFlowStepBeginLogData expectedLogData = new SoapProvideExtFlowStepBeginLogData(
                ExecutionContext.getProperties().getProperty(
                        AbstractFlowLogData.FLOW_INSTANCE_ID_PROPERTY_NAME), ExecutionContext
                        .getProperties()
                        .getProperty(AbstractFlowLogData.FLOW_STEP_ID_PROPERTY_NAME),
                SAMPLE_WSDL_1_SERVICE_URL);
        assertEquals(expectedLogData, soapRequestLogData);
    }

    @Test
    public void testLogOnOutgoingRequestMonitDisabledInOnlyMode() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestInOnlyMep(OPERATION_NAME_IN_ONLY, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);
        assertEquals(0, monitRecords.size());
    }

    @Test
    @Ignore
    public void testLogOnOutgoingRequestWithoutAttachmentMonitEnabledInOnlyMode() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);
        testLogger.setLevel(Level.MONIT);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestInOnlyMep(OPERATION_NAME_IN_ONLY, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);

        makeAssertForTestingMonitTraceLevel(monitRecords);
    }

    @Test
    public void testLogOnOutgoingRequestMonitDisabledRobustInOnlyMode() throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestRobustInOnlyMep(OPERATION_NAME_IN_ONLY, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);
        assertEquals(0, monitRecords.size());
    }

    @Test
    @Ignore
    public void testLogOnOutgoingRequestWithoutAttachmentMonitEnabledRobustInOnlyMode()
            throws Exception {
        TestHandler testHandler = new TestHandler();
        Logger testLogger = createLoggerWithTestHandler(testHandler);
        testLogger.setLevel(Level.MONIT);

        Axis2Client a2c = createAxis2Client(SAMPLE_WSDL_1_URL, SAMPLE_WSDL_1_SERVICE_NAME,
                SAMPLE_WSDL_1_PORT_NAME, testLogger);

        a2c.submitRequestRobustInOnlyMep(OPERATION_NAME_IN_ONLY, SOAP_BODY);

        final List<LogRecord> monitRecords = testHandler.getAllRecords(Level.MONIT);

        makeAssertForTestingMonitTraceLevel(monitRecords);
    }

    private static final void makeAssertForTestingMonitTraceLevel(final List<LogRecord> monitRecords) {
        assertEquals(1, monitRecords.size());

        SoapProvideExtFlowStepBeginLogData soapRequestLogData = extractOutgoingSoapRequestLogData(monitRecords
                .get(0));

        SoapProvideExtFlowStepBeginLogData expectedLogData = new SoapProvideExtFlowStepBeginLogData(
                ExecutionContext.getProperties().getProperty(
                        AbstractFlowLogData.FLOW_INSTANCE_ID_PROPERTY_NAME), ExecutionContext
                        .getProperties()
                        .getProperty(AbstractFlowLogData.FLOW_STEP_ID_PROPERTY_NAME),
                SAMPLE_WSDL_1_SERVICE_URL);
        assertEquals(expectedLogData, soapRequestLogData);
    }

    private static final Logger createLoggerWithTestHandler(TestHandler testHandler) {
        final Logger logger = Logger.getAnonymousLogger();

        logger.addHandler(testHandler);
        logger.setLevel(Level.INFO);
        return logger;
    }

    private static final SoapProvideExtFlowStepBeginLogData extractOutgoingSoapRequestLogData(
            LogRecord logRecord) {
        assertEquals(1, logRecord.getParameters().length);
        assertTrue(logRecord.getParameters()[0] instanceof SoapProvideExtFlowStepBeginLogData);
        return (SoapProvideExtFlowStepBeginLogData) logRecord.getParameters()[0];
    }

    private static class MockSoapWebService extends MockWebService {

        private static final String SOAP_RESPONSE = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<SOAP-ENV:Body>  </SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";

        public MockSoapWebService(URL serviceUrl) {
            super(serviceUrl);

        }

        @Override
        public void onPost(HttpServletRequest req, HttpServletResponse resp) {
            OutputStream os = null;
            try {
                os = resp.getOutputStream();
                os.write(SOAP_RESPONSE.getBytes());
            } catch (IOException e) {
                throw new UncheckedException(e);
            } finally {
                IOHelper.close(os);
            }
        }
    }
}
