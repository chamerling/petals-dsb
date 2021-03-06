/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.api.util.EndpointHelper;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.kernel.pubsub.service.internal.InternalClient;
import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
import org.petalslink.dsb.service.client.WSAMessageImpl;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class DSBNotificationSender extends AbstractNotificationSender {

    private final static Logger log = Logger.getLogger(AbstractNotificationSender.class.getName());

    /**
     * @param producer
     */
    public DSBNotificationSender(NotificationProducerEngine producer) {
        super(producer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.AbstractNotificationSender#doNotify
     * (
     * com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify
     * , java.lang.String,
     * com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType,
     * java.lang.String, javax.xml.namespace.QName, java.lang.String)
     */
    @Override
    protected final void doNotify(Notify notify, String producerAddress,
            EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
            String dialect) throws NotificationException {

        if (currentConsumerEdp == null || currentConsumerEdp.getAddress() == null
                || currentConsumerEdp.getAddress().getValue() == null) {
            // no address found...
            log.fine("No address found, do not send notification");
            return;
        }

        if (log.isLoggable(Level.FINE)) {
            log.fine("Need to send the message to a subscriber which is : "
                    + currentConsumerEdp.getAddress().getValue());
        }

        // we use a WSA endpoint to send the notification...
        // extract data from address
        URI uri = currentConsumerEdp.getAddress().getValue();
        Message message = null;

        Client client = null;

        if (isInternalService(uri)) {
            message = new MessageImpl();
            ServiceEndpoint se = EndpointHelper.getServiceEndpoint(uri);
            message.setEndpoint(se.getEndpointName());
            message.setService(se.getServiceName());
            client = getClient(se);

        } else if (isJavaService(uri)) {
            message = new MessageImpl();
            message.getProperties().put(InternalClient.UNIQUEID, uri.toString());
            client = getInternalClient();

        } else if (isExternalService(uri)) {
            message = new WSAMessageImpl(uri.toString());
            ServiceEndpoint se = new ServiceEndpoint();
            se.setEndpointName(message.getEndpoint());
            se.setServiceName(message.getService());
            se.setInterfaces(new QName[] { message.getInterface() });
            client = getClient(se);
        } else {
            log.warning("Do not know how to process this type of URI : " + uri);
            return;
        }

        if (client == null) {
            throw new NotificationException(String.format(
                    "Can not find any client to send notification to %s", uri.toString()));
        }

        try {
            final Document payload = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);

            message.setPayload(payload);
            message.setOperation(WsnbConstants.NOTIFY_QNAME);
            client.fireAndForget(message);

        } catch (Exception e) {
            e.printStackTrace();
            if (log.isLoggable(Level.WARNING)) {
                log.log(Level.WARNING, "Can not send notification", e);
            }
        } finally {
            if (client != null) {
                try {
                    ClientFactoryRegistry.getFactory().release(client);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return
     */
    private Client getInternalClient() {
        return InternalClient.getInstance();
    }

    /**
     * @param uri
     * @return
     */
    private boolean isInternalService(URI uri) {
        return EndpointHelper.isDSBService(uri);
    }

    private boolean isJavaService(URI uri) {
        return EndpointHelper.isJavaService(uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.AbstractNotificationSender#
     * getProducerAddress()
     */
    @Override
    protected String getProducerAddress() {
        return "dsb://DSBNotificationSender";
    }

    protected Client getClient(ServiceEndpoint se) {
        try {
            return ClientFactoryRegistry.getFactory().getClient(se);
        } catch (ClientException e) {
        }
        return null;
    }

    public static final boolean isExternalService(URI address) {
        boolean result = false;

        if (address == null) {
            return result;
        }

        return address.getScheme() != null;

        // return
        // address.toString().startsWith(Constants.DSB_EXTERNAL_SERVICE_NS)
        // || !address.toString().startsWith(Constants.DSB_INTERNAL_SERVICE_NS);
    }

}
