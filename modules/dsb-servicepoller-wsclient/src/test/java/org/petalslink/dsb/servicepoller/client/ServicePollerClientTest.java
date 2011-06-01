/**
 * 
 */
package org.petalslink.dsb.servicepoller.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerService;
import org.petalslink.dsb.servicepoller.api.ServicePollerServiceImpl;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ServicePollerClientTest extends TestCase {

    /**
     * 
     */
    public void testCallWithInputDocument() {
        final AtomicLong l = new AtomicLong(0);
        final AtomicInteger fail = new AtomicInteger(0);
        ServicePoller beanServer = new ServicePoller() {
            public void stop(String endpointName, QName service, QName itf, QName operation) {

            }

            public void start(String endpointName, QName service, QName itf, QName operation,
                    Document inputMessage) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    TransformerFactory.newInstance().newTransformer()
                            .transform(new DOMSource(inputMessage), new StreamResult(outputStream));
                    System.out.println("Receive : " + outputStream.toString());
                    l.incrementAndGet();
                    // TODO : check received message
                } catch (Exception e) {
                    e.printStackTrace();
                    fail.incrementAndGet();
                }
            }
        };

        String url = "http://localhost:9787/services/Poller";
        Server server = createServer(url, beanServer);
        ServicePoller client = createClient(url);
        Document document = null;
        try {
            File xml = new File(ServicePollerClientTest.class.getResource("/input.xml").toURI());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(xml);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            client.start("endpoint", QName.valueOf("service"), QName.valueOf("itf"),
                    QName.valueOf("operation"), document);
        } catch (ServicePollerException e) {
            fail();
        }
        server.stop();
        assertEquals(1L, l.get());
        assertEquals(0, fail.get());
    }

    /**
     * 
     */
    private ServicePoller createClient(String url) {
        return new ServicePollerClient(url);
    }

    private Server createServer(String url, ServicePoller bean) {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setAddress(url);
        factory.setServiceClass(ServicePollerService.class);
        factory.setServiceBean(new ServicePollerServiceImpl(bean));
        return factory.create();
    }

}
