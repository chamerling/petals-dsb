/**
 * 
 */
package org.petalslink.notification.commons;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class LocalNotificationSenderTest extends TestCase {

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

    public void testNotifyLocal() throws Exception {
        final AtomicInteger calls = new AtomicInteger(0);
        QName topic = new QName("http://www.petalslink.org/dsb/topicsns/", "DSBTopic", "dsb");
        String dialect = "http://www.w3.org/TR/1999/REC-xpath-19991116";

        URL topicNamespaces = LocalNotificationSenderTest.class.getResource("/topicNS.xml");
        List<String> supportedTopics = new ArrayList<String>();
        supportedTopics.add("DSBTopic");
        String NS = "http://dsb.petalslink.com/notification/";
        QName serviceName = new QName(NS, "Service");
        QName interfaceName = new QName(NS, "Interface");
        String endpointName = "Enpoint";

        NotificationManager notificationManager = new NotificationManagerImpl(topicNamespaces,
                supportedTopics, serviceName, interfaceName, endpointName);
        
        // does nothing but just check that the subscription is received...
        NotificationSender sender = new AbstractNotificationSender(
                notificationManager.getNotificationProducerEngine()) {

            @Override
            protected String getProducerAddress() {
                return "http://localhost:9998/foo/bar/Producer";
            }

            @Override
            protected void doNotify(Notify notify, String producerAddress,
                    EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
                    String dialect) throws NotificationException {
                System.out.println("Got a notify...");
                System.out.println("Topic : " + topic);
                System.out.println("Dialect : " + dialect);
                System.out.println("SubscriptionID " + subscriptionId);
                try {
                    System.out.println("--- NOTIFICATION RECEIVED ---");
                    Document n = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                    System.out.println(XMLHelper.createStringFromDOMDocument(n));
                    System.out.println("--- /NOTIFICATION RECEIVED ---");
                } catch (WsnbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (TransformerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                calls.incrementAndGet();
            }
        };

        // assert that the sender is not called...
        System.out.println("Notify but no subscribers...");
        Document payload = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(LocalNotificationSenderTest.class.getResourceAsStream("/notify.xml"));
        sender.notify(payload, topic, dialect);
        assertEquals(0, calls.get());

        // now subscribe to be notified...
        System.out.println("Subscribe...");
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        // Important!
        f.setNamespaceAware(true);
        Subscribe subscribe = Wsnb4ServUtils.getWsnbReader().readSubscribe(
                f.newDocumentBuilder().parse(
                        LocalNotificationSenderTest.class.getResourceAsStream("/subscribe.xml")));

        SubscribeResponse response = notificationManager.getNotificationProducerEngine().subscribe(
                subscribe);
        System.out.println("Subscribed!");
        System.out.println("Subscribe Response : ");
        Document n = Wsnb4ServUtils.getWsnbWriter().writeSubscribeResponseAsDOM(response);
        System.out.println(XMLHelper.createStringFromDOMDocument(n));

        System.out.println("Let's Notify and see if the subscriber receives it...");
        sender.notify(payload, topic, dialect);
        
        assertEquals(1, calls.get());
    }
}
