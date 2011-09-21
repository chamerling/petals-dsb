/**
 * 
 */
package org.petalslink.dsb.sample.wsn;

import java.util.List;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.simple.HTTPProducerRPClient;
import org.petalslink.dsb.notification.commons.NotificationException;

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
public class GetTopicsSample {
    
    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }


    public static void main(String[] args) {
        String dsbSubscribe = "http://localhost:8084/petals/services/NotificationConsumerPortService";
        HTTPProducerRPClient rpclient = new HTTPProducerRPClient(dsbSubscribe);
        try {
            List<QName> topics = rpclient.getTopics();
            System.out.println(topics);
        } catch (NotificationException e) {
            e.printStackTrace();
        }
    }

}
