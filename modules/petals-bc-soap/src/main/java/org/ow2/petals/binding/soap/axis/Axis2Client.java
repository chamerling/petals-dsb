
package org.ow2.petals.binding.soap.axis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.ow2.petals.binding.soap.listener.outgoing.PetalsServiceClient;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

public class Axis2Client extends AbstractAxis2RepositoryBasedObject {

    private final URL wsdlURL;

    private final QName wsdlServiceName;

    private final String portName;

    private final Logger logger;

    // FIXME : eradicate petalsService and replace it by Axis2Client everywhere,
    // instantiation directly an Axis2 ServiceClient. Otherly said, use the
    // simple way!
    private PetalsServiceClient petalsServiceClient;

    public Axis2Client(File baseDir, Axis2Config config, URL wsdlURL, QName wsdlServiceName,
            String portName, Logger logger) {
        super(baseDir, config);
        this.wsdlURL = wsdlURL;
        this.wsdlServiceName = wsdlServiceName;
        this.portName = portName;
        this.logger = logger;
        this.petalsServiceClient = null;
    }

    @Override
    protected void specificSetUp() {
        super.specificSetUp();

        try {
            petalsServiceClient = new PetalsServiceClient(getConfigurationContext(), wsdlURL,
                    wsdlServiceName, portName);
            petalsServiceClient.setLogger(logger);
        } catch (AxisFault af) {
            throw new UncheckedException(af);
        }
    }

    public void submitRequestInOutMep(String operationName, String soapBody) {
        QName operationQName = new QName(operationName);
        OMElement xmlPayload = createXmlPayloadInOMElement(soapBody);

        try {
            petalsServiceClient.sendReceive(operationQName, xmlPayload, null);

        } catch (AxisFault af) {
            throw new UncheckedException(af);
        }
    }

    public void submitRequestInOnlyMep(String operationName, String soapBody) {
        QName operationQName = new QName(operationName);
        OMElement xmlPayload = createXmlPayloadInOMElement(soapBody);

        try {
            petalsServiceClient.fireAndForget(operationQName, xmlPayload, null);
        } catch (AxisFault af) {
            throw new UncheckedException(af);
        }
    }

    public void submitRequestRobustInOnlyMep(String operationName, String soapBody) {
        QName operationQName = new QName(operationName);
        OMElement xmlPayload = createXmlPayloadInOMElement(soapBody);

        try {
            petalsServiceClient.sendRobust(operationQName, xmlPayload, null);
        } catch (AxisFault af) {
            throw new UncheckedException(af);
        }
    }

    public void submitRequestWithAttachments(String operationName, String soapBody,
            Map<String, DataHandler> attachments) {
        QName operationQName = new QName(operationName);
        OMElement xmlPayload = createXmlPayloadInOMElement(soapBody);

        try {
            petalsServiceClient.getOptions().setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);

            petalsServiceClient.sendReceive(operationQName, xmlPayload, null);
        } catch (AxisFault af) {
            throw new UncheckedException(af);
        }
    }

    private OMElement createXmlPayloadInOMElement(String soapBody) {
        InputStream inputStream = new ByteArrayInputStream(soapBody.getBytes());
        OMElement xmlPayload = null;

        try {
            StAXOMBuilder builder = new StAXOMBuilder(inputStream);
            xmlPayload = builder.getDocumentElement();
        } catch (XMLStreamException xse) {
            throw new UncheckedException(xse);
        }
        return xmlPayload;
    }

}
