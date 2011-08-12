/**
 * 
 */
package org.petalslink.dsb.notification.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.AbstractService;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class NotificationProducerService extends AbstractService {

    private static Logger logger = Logger.getLogger(NotificationProducerService.class.getName());

    private INotificationProducer producer;

    /**
     * @param interfaceName
     * @param serviceName
     * @param endpointName
     * @param wsdl
     * @param url
     */
    public NotificationProducerService(QName interfaceName, QName serviceName, QName endpointName,
            String wsdl, String url, INotificationProducer producer) {
        super(interfaceName, serviceName, endpointName, wsdl, url);
        if (producer == null) {
            throw new IllegalArgumentException("Producer is null!");
        }
        this.producer = producer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public void doInvoke(SimpleExchange exchange) throws ServiceException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Got an invoke");
        }

        if (exchange == null || exchange.getIn() == null) {
            throw new ServiceException("Incoming message is null...");
        }
        QName operation = exchange.getOperation();
        if (operation == null) {
            throw new ServiceException("Incoming operation is null...");
        }
        
        if (logger.isLoggable(Level.FINE)) {
            logger.finest("Operation is " + operation);
        }

        if ("Subscribe".equals(operation.getLocalPart())) {
            try {
                Subscribe subscribe = RefinedWsnbFactory.getInstance().getWsnbReader()
                        .readSubscribe(exchange.getIn());
                SubscribeResponse res = this.producer.subscribe(subscribe);
                Document docResp = RefinedWsnbFactory.getInstance().getWsnbWriter()
                        .writeSubscribeResponseAsDOM(res);
                exchange.setOut(docResp);
            } catch (WsnbException e) {
                e.printStackTrace();
                throw new ServiceException(e);
            } catch (AbsWSStarFault e) {
                e.printStackTrace();
                throw new ServiceException(e);
            }
        } else if ("GetCurrentMessage".equals(operation.getLocalPart())) {
            try {
                GetCurrentMessage getCurrentMessage = RefinedWsnbFactory.getInstance()
                        .getWsnbReader().readGetCurrentMessage(exchange.getIn());
                GetCurrentMessageResponse res = this.producer.getCurrentMessage(getCurrentMessage);
                Document docResp = RefinedWsnbFactory.getInstance().getWsnbWriter()
                        .writeGetCurrentMessageResponseAsDOM(res);
                exchange.setOut(docResp);
            } catch (WsnbException e) {
                e.printStackTrace();
                throw new ServiceException(e);
            } catch (AbsWSStarFault e) {
                e.printStackTrace();
                throw new ServiceException(e);
            }
        } else {
            throw new ServiceException("Unknown operation '" + operation + "'");
        }
    }
}
