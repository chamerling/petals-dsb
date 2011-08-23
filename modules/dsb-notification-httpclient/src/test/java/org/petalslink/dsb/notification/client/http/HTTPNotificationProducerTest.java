/**
 * 
 */
package org.petalslink.dsb.notification.client.http;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.notification.commons.SOAUtil;
import org.petalslink.dsb.notification.service.NotificationProducerService;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.petalslink.dsb.soap.api.ServiceException;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.element.Address;
import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType.Message;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * Just test if the calls are received in some fake services
 * 
 * @author chamerling
 * 
 */
public class HTTPNotificationProducerTest extends TestCase {

    static String address = "http://localhost:8878/petals/services/NotificationProducerPortService";

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    public void testSubscribe() throws Exception {

        final AtomicInteger i = new AtomicInteger(0);

        HTTPNotificationProducerClient client = new HTTPNotificationProducerClient(address);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(
                HTTPNotificationConsumerTest.class.getResourceAsStream("/subscribe-body.xml"));

        Service server = null;
        try {
            server = getServer(i);
            server.start();
            Subscribe subscribe = Wsnb4ServUtils.getWsnbReader().readSubscribe(document);
            System.out.println("Let's subscribe");
            client.subscribe(subscribe);
            // wait some time, it seems that response is not send at the right
            // time...
            System.out.println("Waiting...");
            TimeUnit.SECONDS.sleep(2);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.stop();
            }
        }
        assertEquals(1, i.get());
    }

    // public void testGetCurrentMessage() throws Exception {
    //
    // final AtomicInteger i = new AtomicInteger(0);
    //
    // HTTPNotificationProducerClient client = new
    // HTTPNotificationProducerClient(address);
    // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // factory.setNamespaceAware(true);
    // Document document = factory.newDocumentBuilder().parse(
    // HTTPNotificationConsumerTest.class.getResourceAsStream("/getcurrent-body.xml"));
    //
    // Service server = null;
    // try {
    // server = getServer(i);
    // server.start();
    // GetCurrentMessage getCurrentMessage = Wsnb4ServUtils.getWsnbReader()
    // .readGetCurrentMessage(document);
    // System.out.println("Let's get current message...");
    // client.getCurrentMessage(getCurrentMessage);
    // System.out.println("Waiting...");
    // TimeUnit.SECONDS.sleep(5);
    // } catch (RuntimeException e) {
    // e.printStackTrace();
    // } finally {
    // if (server != null) {
    // server.stop();
    // }
    // }
    // assertEquals(1, i.get());
    // }

    /**
     * @param i
     * @return
     * @throws ServiceException
     */
    private Service getServer(final AtomicInteger i) throws ServiceException {
        // start an http server with a notificaiton producer engine...
        // TODO
        QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationProducer");
        QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationProducerService");
        QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationProducerPort");
        // expose the service
        INotificationProducer producer = new INotificationProducer() {

            public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage getCurrentMessage)
                    throws WsnbException, AbsWSStarFault {
                System.out.println("Got a getCurrentMessage");
                i.incrementAndGet();
                Message message = RefinedWsnbFactory.getInstance()
                        .createNotificationMessageHolderTypeMessage(null);
                return RefinedWsnbFactory.getInstance().createGetCurrentMessageResponse(message);
            }

            public SubscribeResponse subscribe(Subscribe subscribe) throws WsnbException,
                    AbsWSStarFault {
                System.out.println("Got a subscribe");
                i.incrementAndGet();
                final EndpointReferenceType registrationRef = SOAUtil.getInstance()
                        .getXmlObjectFactory().create(EndpointReferenceType.class);
                Address address = SOAUtil.getInstance().getXmlObjectFactory().create(Address.class);
                address.setValue(URI.create("http://localhost:99998/foo/bar"));
                registrationRef.setAddress(address);
                return RefinedWsnbFactory.getInstance().createSubscribeResponse(registrationRef);
            }
        };
        NotificationProducerService service = new NotificationProducerService(interfaceName,
                serviceName, endpointName, "NotificationProducerService.wsdl", address, producer);
        Exposer exposer = new CXFExposer();
        return exposer.expose(service);
    }
}
