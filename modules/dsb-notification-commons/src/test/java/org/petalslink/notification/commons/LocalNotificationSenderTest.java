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

import junit.framework.TestCase;

import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbReaderImpl;

/**
 * @author chamerling
 * 
 */
public class LocalNotificationSenderTest extends TestCase {

    public void testNotifyLocal() throws Exception {
        final AtomicInteger calls = new AtomicInteger(0);
        QName topic = QName.valueOf("{http://www.petalslink.org/dsb/topicsns/}DSBTopic");

        String dialect = "http://www.w3.org/TR/1999/REC-xpath-19991116";

        URL topicNamespaces = LocalNotificationSenderTest.class.getResource("/topicNS.xml");
        List<String> supportedTopics = new ArrayList<String>();
        supportedTopics.add("DSBTopic");
        QName serviceName = QName.valueOf("Service");
        QName interfaceName = QName.valueOf("Interface");
        String endpointName = "Enpoint";
        NotificationManager notificationManager = new NotificationManagerImpl(topicNamespaces,
                supportedTopics, serviceName, interfaceName, endpointName);
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
                calls.incrementAndGet();
            }
        };

        // assert that the sender is not called...
        Document payload = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(LocalNotificationSenderTest.class.getResourceAsStream("/notify.xml"));
        sender.notify(payload, topic, dialect);
        assertEquals(0, calls.get());

        // now subscribe to be notified...
        WsnbModelFactoryImpl factory = new WsnbModelFactoryImpl();
        Subscribe subscribe = factory.getWsnbModelReader().readSubscribe(
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(LocalNotificationSenderTest.class
                                .getResourceAsStream("/subscribe.xml")));
        System.out.println("subscribe read...");
        
        notificationManager.getNotificationProducerEngine().subscribe(subscribe);
        
        System.out.println("Subscribed...");
        
        sender.notify(payload, topic, dialect);


        // QName topic = QName.valueOf("{http://petalslink.org}TopicA");
        //
        // SubscriptionManager manager = new
        // SubscriptionManager(topicNamespaces, supportedTopics, serviceName,
        // interfaceName, endpointName);
        // NotificationProducer producer = new NotificationProducer(manager);
        // NotificationSender sender = new
        // AbstractNotificationSender(manager.get) {
        //
        // @Override
        // protected String getProducerAddress() {
        // return "local://foo/bar";
        // }
        //
        // @Override
        // protected void doNotify(Notify notify) throws NotificationException {
        // // TODO Auto-generated method stub
        // System.out.println("Let's notify");
        //
        // }
        // };
        //
        // Document payload = null;
        // String dialect = null;
        // sender.notify(payload, topic, dialect);
    }
}
