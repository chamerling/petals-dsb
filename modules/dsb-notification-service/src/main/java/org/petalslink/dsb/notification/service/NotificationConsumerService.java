/**
 * 
 */
package org.petalslink.dsb.notification.service;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.AbstractService;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * Expose the notification consumer as a {@link Service}
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerService extends AbstractService {

    private static Logger logger = Logger.getLogger(NotificationConsumerService.class.getName());

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    /**
     * The internal Notification Consumer
     */
    private INotificationConsumer consumer;

    /**
     * @param interfaceName
     * @param serviceName
     * @param endpointName
     * @param wsdl
     * @param url
     */
    public NotificationConsumerService(QName interfaceName, QName serviceName, QName endpointName,
            String wsdl, String url, INotificationConsumer consumer) {
        super(interfaceName, serviceName, endpointName, wsdl, url);
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer is null!");
        }
        this.consumer = consumer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public void doInvoke(SimpleExchange exchange) throws ServiceException {
        if (exchange == null || exchange.getIn() == null) {
            throw new ServiceException("Incoming message is null...");
        }
        QName operation = exchange.getOperation();
        if (operation == null) {
            throw new ServiceException("Incoming operation is null...");
        }

        if ("Notify".equals(operation.getLocalPart())) {
            logger.finest("Notify");
            try {
                Notify notify = RefinedWsnbFactory.getInstance().getWsnbReader()
                        .readNotify(exchange.getIn());
                this.consumer.notify(notify);
            } catch (WsnbException e) {
                e.printStackTrace();
                throw new ServiceException(e);
            }
        } else {
            throw new ServiceException("Unknown operation '" + operation + "'");
        }
    }
}
