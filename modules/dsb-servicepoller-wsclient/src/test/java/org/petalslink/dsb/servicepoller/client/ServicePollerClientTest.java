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
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.servicepoller.api.ServicePollerService;
import org.petalslink.dsb.servicepoller.api.ServicePollerServiceAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author chamerling
 * 
 */
public class ServicePollerClientTest extends TestCase {

    /**
     * 
     */
    public void testCallWithInputDocument() {
        final AtomicLong received = new AtomicLong(0);
        final AtomicInteger fail = new AtomicInteger(0);
        final AtomicInteger ok = new AtomicInteger(0);

        ServicePoller beanServer = new ServicePoller() {
            public void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo) {

            }

            public void start(ServicePollerInformation toPoll, Document inputMessage,
                    String cronExpression, ServicePollerInformation replyTo) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    TransformerFactory.newInstance().newTransformer()
                            .transform(new DOMSource(inputMessage), new StreamResult(outputStream));
                    System.out.println("Receive : " + outputStream.toString());
                    received.incrementAndGet();

                    if (payloadIsOk(inputMessage)) {
                        ok.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    fail.incrementAndGet();
                }
            }

            private boolean payloadIsOk(Document inputMessage) {
                int check = 0;
                if (inputMessage != null) {
                    if (inputMessage.getFirstChild() != null
                            && inputMessage.getFirstChild().getNodeName().equals("in")) {
                        check++;
                    }
                    NodeList list = inputMessage.getFirstChild().getChildNodes();
                    int length = list.getLength();
                    for (int i = 0; i < length; i++) {
                        if (list.item(i).getNodeType() == Node.ELEMENT_NODE
                                && list.item(i).getNodeName().equals("arg")
                                && list.item(i).getTextContent().equals("DSB")) {
                            check++;
                        }
                    }
                }
                return check == 2;
            }
        };

        String url = "http://localhost:9787/services/";
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
            ServicePollerInformation info = new ServicePollerInformation();
            info.setEndpointName("endpoint");
            info.setInterfaceName(QName.valueOf("itf"));
            info.setOperation(QName.valueOf("operation"));
            info.setServiceName(QName.valueOf("service"));
            client.start(info, document, null, null);
        } catch (ServicePollerException e) {
            fail();
        }
        server.stop();
        assertEquals(1L, received.get());
        assertEquals(0, fail.get());
        assertEquals(1, ok.get());
    }

    /**
     * 
     */
    private ServicePoller createClient(String url) {
        return new ServicePollerClient(url);
    }

    private Server createServer(String url, ServicePoller bean) {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setAddress(url + ServicePollerService.class.getSimpleName());
        factory.setServiceClass(ServicePollerService.class);
        factory.setServiceBean(new ServicePollerServiceAdapter(bean));
        return factory.create();
    }

}
