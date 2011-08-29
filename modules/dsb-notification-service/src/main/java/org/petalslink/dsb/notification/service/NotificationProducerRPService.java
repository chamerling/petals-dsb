/**
 * 
 */
package org.petalslink.dsb.notification.service;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.AbstractService;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourceProperties;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourcePropertiesResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.refinedabstraction.RefinedWsrfrpFactory;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.utils.WsrfrpException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class NotificationProducerRPService extends AbstractService {

    private INotificationProducerRP producerRP;

    /**
     * @param interfaceName
     * @param serviceName
     * @param endpointName
     * @param wsdl
     * @param url
     */
    public NotificationProducerRPService(QName interfaceName, QName serviceName,
            QName endpointName, String wsdl, String url, INotificationProducerRP producerRP) {
        super(interfaceName, serviceName, endpointName, wsdl, url);
        if (producerRP == null) {
            throw new IllegalArgumentException("Producer is null!");
        }
        this.producerRP = producerRP;
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
        System.out.println("NotificationRP producer");
        System.out.println("org.petalslink.dsb.notification.service.NotificationProducerRPService");

        if (exchange == null || exchange.getIn() == null) {
            throw new ServiceException("Incoming message is null...");
        }
        QName operation = exchange.getOperation();
        if (operation == null) {
            throw new ServiceException("Incoming operation is null...");
        }

        if ("GetResourceProperty".equals(operation.getLocalPart())) {
            try {
                QName qname = RefinedWsrfrpFactory.getInstance().getWsrfrpReader()
                        .readGetResourceProperty(exchange.getIn());
                com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.GetResourcePropertyResponse res = this.producerRP
                        .getResourceProperty(qname);
                Document docResp = RefinedWsrfrpFactory.getInstance().getWsrfrpWriter()
                        .writeGetResourcePropertyResponseAsDOM(res);
                exchange.setOut(docResp);
            } catch (WsnbException e) {
                e.printStackTrace();
                throw new ServiceException(e);
            } catch (AbsWSStarFault e) {
                e.printStackTrace();
                throw new ServiceException(e);
            } catch (WsrfrpException e) {
                throw new ServiceException(e);
            }
        } else if ("UpdateResourceProperties".equals(operation.getLocalPart())) {
            try {
                UpdateResourceProperties updateResourceProperties = RefinedWsrfrpFactory
                        .getInstance().getWsrfrpReader()
                        .readUpdateResourceProperties(exchange.getIn());
                UpdateResourcePropertiesResponse res = this.producerRP
                        .updateResourceProperties(updateResourceProperties);
                Document docResp = RefinedWsrfrpFactory.getInstance().getWsrfrpWriter()
                        .writeUpdateResourcePropertiesResponseAsDOM(res);
                exchange.setOut(docResp);
            } catch (WsnbException e) {
                throw new ServiceException(e);
            } catch (AbsWSStarFault e) {
                throw new ServiceException(e);
            } catch (WsrfrpException e) {
                throw new ServiceException(e);
            }
        } else {
            throw new ServiceException("Unknown operation '" + operation + "'");
        }
    }
}
