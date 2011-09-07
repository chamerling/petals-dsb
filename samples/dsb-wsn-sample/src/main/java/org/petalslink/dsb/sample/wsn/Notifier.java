/**
 * 
 */
package org.petalslink.dsb.sample.wsn;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.petalslink.dsb.notification.client.http.simple.HTTPConsumerClient;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
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
public class Notifier {
    
    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    public static void main(String[] args) {

        // DSB address to send notifications to
        String dsbNotify = "http://localhost:8084/petals/services/NotificationProducerPortService";

        QName topic = new QName("http://www.petalslink.org/dsb/topicsns/", "DSBTopic", "dsb");

        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            document = factory.newDocumentBuilder().parse(
                    Main.class.getResourceAsStream("/notify-payload.xml"));

            HTTPConsumerClient client = new HTTPConsumerClient(dsbNotify);
            client.notify(document, topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
