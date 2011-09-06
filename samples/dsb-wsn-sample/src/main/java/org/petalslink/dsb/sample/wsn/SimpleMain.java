/**
 * 
 */
package org.petalslink.dsb.sample.wsn;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.notification.client.http.simple.HTTPConsumerClient;
import org.petalslink.dsb.notification.client.http.simple.HTTPProducerClient;
import org.petalslink.dsb.notification.client.http.simple.HTTPProducerRPClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.service.NotificationConsumerService;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class SimpleMain {

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("****** CREATING LOCAL SERVER ******");

        // local address which will receive notifications
        String address = "http://localhost:8878/petals/services/NotificationConsumerPortService";
        // DSB adress to subscribe to
        String dsbSubscribe = "http://localhost:8084/petals/services/NotificationConsumerPortService";
        // DSB address to send notifications to
        String dsbNotify = "http://localhost:8084/petals/services/NotificationProducerPortService";

        // the one which will receive notifications
        System.out
                .println("Creating service which will receive notification messages from the DSB...");

        Service server = null;
        QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumer");
        QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerService");
        QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerPort");
        // expose the service
        INotificationConsumer consumer = new INotificationConsumer() {
            public void notify(Notify notify) throws WsnbException {
                System.out
                        .println("Got a notify on HTTP service, this notification comes from the DSB itself...");

                Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                System.out.println("==============================");
                try {
                    XMLHelper.writeDocument(dom, System.out);
                } catch (TransformerException e) {
                }
                System.out.println("==============================");
            }
        };
        NotificationConsumerService service = new NotificationConsumerService(interfaceName,
                serviceName, endpointName, "NotificationConsumerService.wsdl", address, consumer);

        Exposer exposer = new CXFExposer();
        try {
            server = exposer.expose(service);
            server.start();
            System.out.println("Local server is started and is ready to receive notifications");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        System.out.println("****** SUBSCRIBE TO NOTIFICATION ******");

        QName topic = new QName("http://www.petalslink.org/dsb/topicsns/", "DSBTopic", "dsb");
        HTTPProducerClient pc = new HTTPProducerClient(dsbSubscribe);
        try {
            pc.subscribe(topic, address);
        } catch (NotificationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = null;
        try {
            document = factory.newDocumentBuilder().parse(
                    Main.class.getResourceAsStream("/notify-payload.xml"));

            HTTPConsumerClient client = new HTTPConsumerClient(dsbNotify);
            client.notify(document, topic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Waiting...");

            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
        }
        
        HTTPProducerRPClient rpclient = new HTTPProducerRPClient(dsbSubscribe);
        try {
            rpclient.getTopics();
        } catch (NotificationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
