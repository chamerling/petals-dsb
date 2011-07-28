/**
 * 
 */
package org.petalslink.dsb.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.cxf.Server;
import org.petalslink.dsb.soap.api.Exposer;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author chamerling
 * 
 */
public class CXFExposerTest extends TestCase {

    private static final String url = "http://localhost:8889/foo/bar/TestService";

    public void testSend() throws Exception {

        final AtomicBoolean messageReceived = new AtomicBoolean(false);
        Exposer exposer = new CXFExposer();
        Service service = new Service() {

            public void invoke(SimpleExchange exchange) throws ServiceException {
                Document in = exchange.getIn();
                System.out.println("Got an invoke, message is " + get(in));
                messageReceived.set(true);
            }

            public String getWSDLURL() {
                return "test.wsdl";
            }

            public String getURL() {
                return url;
            }

            public QName getService() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService");
            }

            public QName getInterface() {
                return null;
            }

            public QName getEndpoint() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServicePort");
            }
        };

        Server server = exposer.expose(service);
        try {
            server.start();
            HelloService client = CXFHelper.getClientFromFinalURL(url, HelloService.class);
            client.sayHello();

            // launch a client...
            assertTrue(messageReceived.get());
        } finally {
            server.stop();
        }
    }

    public void testReturn() throws Exception {
        final AtomicBoolean messageReceived = new AtomicBoolean(false);
        Exposer exposer = new CXFExposer();
        Service service = new Service() {

            public void invoke(SimpleExchange exchange) throws ServiceException {
                Document in = exchange.getIn();
                System.out.println("Got an invoke, message is " + get(in));
                if (in == null || in.getFirstChild() == null) {
                    // error
                    System.out.println("Can not get input message...");
                    return;
                }

                boolean ok = (in.getFirstChild().getLocalName() != null && in.getFirstChild()
                        .getLocalName().equals("sayHello"));
                messageReceived.set(ok);
            }

            public String getWSDLURL() {
                return "test.wsdl";
            }

            public String getURL() {
                return url;
            }

            public QName getService() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService");
            }

            public QName getInterface() {
                return null;
            }

            public QName getEndpoint() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServicePort");
            }
        };

        Server server = null;
        try {
            server = exposer.expose(service);
            server.start();

            HelloService client = CXFHelper.getClientFromFinalURL(url, HelloService.class);
            client.sayHello();

            // launch a client...
            assertTrue(messageReceived.get());
        } finally {
            if (server != null)
                server.stop();
        }
    }

    public void testSendResponse() throws Exception {
        final AtomicBoolean messageReceived = new AtomicBoolean(false);
        Exposer exposer = new CXFExposer();
        Service service = new Service() {

            public void invoke(SimpleExchange exchange) throws ServiceException {
                Document in = exchange.getIn();
                System.out.println("Got an invoke, message is " + get(in));
                if (in == null || in.getFirstChild() == null) {
                    // error
                    System.out.println("Can not get input message...");
                    return;
                }

                // set the output...
                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder();
                    String xml = "<ns2:sayHelloResponse xmlns:ns2=\"http://api.ws.dsb.petalslink.org/\"><return>hello</return></ns2:sayHelloResponse>";
                    Document document = builder.parse(new InputSource(new StringReader(xml)));
                    exchange.setOut(document);
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            public String getWSDLURL() {
                return "test.wsdl";
            }

            public String getURL() {
                return url;
            }

            public QName getService() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService");
            }

            public QName getInterface() {
                return null;
            }

            public QName getEndpoint() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServicePort");
            }
        };

        Server server = null;
        try {
            server = exposer.expose(service);
            server.start();

            HelloService client = CXFHelper.getClientFromFinalURL(url, HelloService.class);
            String response = client.sayHello();
            System.out.println("Response from service is  " + response);
            assertEquals("hello", response);
        } finally {
            if (server != null)
                server.stop();
        }
    }

    public void testGetSOAPAction() throws Exception {
        final AtomicBoolean messageReceived = new AtomicBoolean(false);
        Exposer exposer = new CXFExposer();
        Service service = new Service() {

            public void invoke(SimpleExchange exchange) throws ServiceException {
                Document in = exchange.getIn();
                System.out.println("Got an invoke, message is " + get(in));
                if (in == null || in.getFirstChild() == null) {
                    // error
                    System.out.println("Can not get input message...");
                    return;
                }

                System.out.println("Incoming Action is " + exchange.getOperation());
                messageReceived.set(exchange.getOperation() != null
                        && exchange.getOperation().getLocalPart().equals("sayHello"));

            }

            public String getWSDLURL() {
                return "test.wsdl";
            }

            public String getURL() {
                return url;
            }

            public QName getService() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService");
            }

            public QName getInterface() {
                return null;
            }

            public QName getEndpoint() {
                return new QName("http://api.ws.dsb.petalslink.org/", "HelloServicePort");
            }
        };

        Server server = null;
        try {
            server = exposer.expose(service);
            server.start();

            HelloService client = CXFHelper.getClientFromFinalURL(url, HelloService.class);
            client.sayHello();
            assertTrue(messageReceived.get());
        } finally {
            if (server != null)
                server.stop();
        }
    }

    private static final String get(Document doc) {
        Source source = new DOMSource(doc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Result result = new StreamResult(baos);
        try {
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            return baos.toString();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
