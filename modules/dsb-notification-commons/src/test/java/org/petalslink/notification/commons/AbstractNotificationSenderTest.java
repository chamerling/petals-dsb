/**
 * 
 */
package org.petalslink.notification.commons;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class AbstractNotificationSenderTest extends TestCase {

    List<String> problems;

    final String producer = "http://localhost:9998/foo/bar/LocalProducer";

    QName topic = new QName("http://www.petalslink.org/dsb/topicsns/", "DSBTopic", "dsb");

    String dialect = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.problems = new ArrayList<String>();
    }

    public void testNotifyDocument() throws Exception {
        NotificationSender sender = initializeEnv();

        Document payload = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(LocalNotificationSenderTest.class.getResourceAsStream("/notify.xml"));

        System.out
                .println("Let's Notify and see if the subscriber receives it with the right parameters...");

        // first method...
        sender.notify(payload, topic, dialect);
        StringBuffer message = new StringBuffer();
        for (String string : problems) {
            message.append(string);
            message.append("/n");
        }
        assertEquals(message.toString(), 0, problems.size());
    }

    public void testNotify() throws Exception {
        NotificationSender sender = initializeEnv();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document message = dbf.newDocumentBuilder().parse(
                LocalNotificationSenderTest.class.getResourceAsStream("/notify-message.xml"));

        Notify notify = Wsnb4ServUtils.getWsnbReader().readNotify(message);

        System.out
                .println("Let's Notify and see if the subscriber receives it with the right parameters...");

        // first method...
        sender.notify(notify);
        StringBuffer sb = new StringBuffer();
        for (String string : problems) {
            sb.append(string);
            sb.append("/n");
        }
        assertEquals(sb.toString(), 0, problems.size());
    }

    /**
     * @return
     * @throws WsnbException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws AbsWSStarFault
     * @throws TransformerException
     */
    protected NotificationSender initializeEnv() throws WsnbException, SAXException, IOException,
            ParserConfigurationException, AbsWSStarFault, TransformerException {
        URL topicNamespaces = LocalNotificationSenderTest.class.getResource("/topicNS.xml");
        List<String> supportedTopics = new ArrayList<String>();
        supportedTopics.add("DSBTopic");
        String NS = "http://dsb.petalslink.com/notification/";
        QName serviceName = new QName(NS, "Service");
        QName interfaceName = new QName(NS, "Interface");
        String endpointName = "Enpoint";

        NotificationManager notificationManager = new NotificationManagerImpl(topicNamespaces,
                supportedTopics, serviceName, interfaceName, endpointName);

        // does nothing but just check that the notification is received...
        NotificationSender sender = new AbstractNotificationSender(
                notificationManager.getNotificationProducerEngine()) {

            @Override
            protected String getProducerAddress() {
                return producer;
            }

            @Override
            protected void doNotify(Notify notify, String producerAddress,
                    EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
                    String dialect) throws NotificationException {
                System.out.println("Got a notify...");
                System.out.println("Topic : " + topic);
                System.out.println("Dialect : " + dialect);
                System.out.println("SubscriptionID " + subscriptionId);
                // this is where the message needs to be sent...
                System.out.println("Consumer Endpoint : "
                        + currentConsumerEdp.getAddress().getValue());
                try {
                    System.out.println("--- NOTIFICATION RECEIVED ---");
                    Document n = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                    System.out.println(XMLHelper.createStringFromDOMDocument(n));
                    System.out.println("--- /NOTIFICATION RECEIVED ---");
                } catch (WsnbException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

                if (!producer.equals(producerAddress)) {
                    problems.add(String.format(
                            "Producer addess are not the same, expected is %s and received is %s",
                            producer, producerAddress));
                }
            }
        };

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
        return sender;
    }

}
