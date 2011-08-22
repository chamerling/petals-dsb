/**
 * 
 */
package org.petalslink.dsb.notification.service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.TestCase;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.saaj.utils.SOAPMessageUtils;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * Test by exposing with Web service...
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerWSTest extends TestCase {

    public void testNotify() throws Exception {
        // instanciate the WSN server stuff...
        final AtomicInteger integer = new AtomicInteger(0);
        Service server = null;
        String serviceURL = "http://localhost:9998/foo/bar/NotificationConsumerService";

        try {
            INotificationConsumer consumer = new INotificationConsumer() {

                public void notify(Notify notify) throws WsnbException {
                    System.out.println("Got a notify...");
                    integer.incrementAndGet();
                }
            };
            QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                    "NotificationConsumer");
            QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                    "NotificationConsumerService");
            QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                    "NotificationConsumerPort");
            // expose the service
            NotificationConsumerService service = new NotificationConsumerService(interfaceName,
                    serviceName, endpointName, "NotificationConsumerService.wsdl", serviceURL,
                    consumer);
            Exposer exposer = new CXFExposer();
            server = exposer.expose(service);
            server.start();

            // create a client and call
            // org.petalslink.dsb.notification.jaxws.api.NotificationConsumer
            // client = CXFHelper
            // .getClientFromFinalURL(serviceURL,
            // org.petalslink.dsb.notification.jaxws.api.NotificationConsumer.class);

            System.out.println("Started and client created");
            Document document = loadNotify("/messages/notifypayload.xml");
            if (document == null) {
                fail();
            }
            
            // Send with SAAJ...
            SOAPMessage soapMessage = SOAPMessageUtils.createSOAPMessageFromBodyContent(document);
            if (soapMessage == null) {
                fail();
            }
            
            System.out.println("Input message = ");
            soapMessage.writeTo(System.out);
            System.out.println();
            
            QName operation = new QName("http://docs.oasis-open.org/wsn/b-2", "Notify");
            SOAPMessage reply = send(soapMessage, serviceURL, operation);
            if (reply != null) {
                System.out.println("Output message");
                reply.writeTo(System.out);
            } else {
                System.out.println("Ouput message is null, not a problem...");
            }
            
            assertEquals(1, integer.get());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    /**
     * @param soapMessage
     */
    private SOAPMessage send(SOAPMessage soapMessage, String destination, QName operation) {
        try {
            //soapMessage.
            MimeHeaders hd = soapMessage.getMimeHeaders();
            hd.addHeader("SOAPAction", operation.getLocalPart());
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();
            return connection.call(soapMessage, destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param string
     * @return
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    private Document loadNotify(String name) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(NotificationConsumerWSTest.class.getResourceAsStream(name));
    }
}
