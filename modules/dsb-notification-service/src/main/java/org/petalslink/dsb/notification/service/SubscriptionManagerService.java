/**
 * 
 */
package org.petalslink.dsb.notification.service;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.AbstractService;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Renew;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.RenewResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.ISubscriptionManager;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class SubscriptionManagerService extends AbstractService {

    private static Logger logger = Logger.getLogger(SubscriptionManagerService.class.getName());

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    private ISubscriptionManager subscriptionManager;

    /**
     * @param interfaceName
     * @param serviceName
     * @param endpointName
     * @param wsdl
     * @param url
     */
    public SubscriptionManagerService(QName interfaceName, QName serviceName, QName endpointName,
            String wsdl, String url, ISubscriptionManager manager) {
        super(interfaceName, serviceName, endpointName, wsdl, url);
        if (manager == null) {
            throw new IllegalArgumentException("Manager is null!");
        }
        this.subscriptionManager = manager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.AbstractService#doInvoke(org.petalslink.dsb.soap
     * .api.SimpleExchange)
     */
    @Override
    protected void doInvoke(SimpleExchange exchange) throws ServiceException {
        if (exchange == null || exchange.getIn() == null) {
            throw new ServiceException("Incoming message is null...");
        }
        QName operation = exchange.getOperation();
        if (operation == null) {
            throw new ServiceException("Incoming operation is null...");
        }

        if ("Renew".equals(operation.getLocalPart())) {
            logger.finest("Renew");
            try {
                Renew renew = RefinedWsnbFactory.getInstance().getWsnbReader()
                        .readRenew(exchange.getIn());
                RenewResponse response = this.subscriptionManager.renew(renew);
                Document docResp = RefinedWsnbFactory.getInstance().getWsnbWriter()
                        .writeRenewResponseAsDOM(response);
                exchange.setOut(docResp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServiceException(e);
            }
        } else if ("Unsubscribe".equals(operation.getLocalPart())) {
            logger.finest("Unsubscribe");
            try {
                Unsubscribe unsubscribe = RefinedWsnbFactory.getInstance().getWsnbReader()
                        .readUnsubscribe(exchange.getIn());
                UnsubscribeResponse response = this.subscriptionManager.unsubscribe(unsubscribe);
                Document docResp = RefinedWsnbFactory.getInstance().getWsnbWriter()
                        .writeUnsubscribeResponseAsDOM(response);
                exchange.setOut(docResp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServiceException(e);
            }

        } else {
            throw new ServiceException("Unknown operation '" + operation + "'");
        }
    }
}
