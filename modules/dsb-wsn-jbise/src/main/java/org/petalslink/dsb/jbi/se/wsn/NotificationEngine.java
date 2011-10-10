/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn;

import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.ComponentWsdl;
import org.ow2.petals.component.framework.api.Wsdl;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.petalslink.dsb.api.util.EndpointHelper;
import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.service.NotificationProducerRPService;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
import org.petalslink.dsb.service.client.WSAMessageImpl;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.resource.datatypes.api.WsrfrConstants;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.AbsNotificationConsumerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class NotificationEngine {

    private Logger logger;

    NotificationManager notificationManager;

    private URL topicNamespaces;
    
    private URL topicSet;

    private QName serviceName;

    private QName interfaceName;

    private String endpointName;

    private AbsNotificationConsumerEngine notificationConsumerEngine;

    /**
     * This one is used to send notifications to service bus endpoints
     */
    private AbstractNotificationSender internalNotificationSender;

    Wsdl consumerWSDL;

    Wsdl producerWSDL;

    private Client client;

    private ServiceEngine serviceEngine;

    public NotificationEngine(Logger logger, URL topicSet, URL topicNamespaces, QName serviceName, QName interfaceName, String endpointName, Client client) {
        super();
        this.logger = logger;
        this.topicNamespaces = topicNamespaces;
        this.topicSet = topicSet;
        this.serviceName = serviceName;
        this.interfaceName = interfaceName;
        this.endpointName = endpointName;
        this.client = client;
    }

    /**
     * 
     */
    public void init() {
        this.notificationManager = new NotificationManagerImpl(topicSet, topicNamespaces,
                serviceName, interfaceName, endpointName);
        this.internalNotificationSender = new AbstractNotificationSender(this
                .getNotificationManager().getNotificationProducerEngine()) {

            @Override
            protected String getProducerAddress() {
                return "jbi://" + endpointName;
            }

            @Override
            protected void doNotify(Notify notify, String producerAddress,
                    EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
                    String dialect) throws NotificationException {

                if (currentConsumerEdp == null || currentConsumerEdp.getAddress() == null
                        || currentConsumerEdp.getAddress().getValue() == null) {
                    // no address found...
                    logger.fine("No address found, do not send notification");
                    return;
                }
                
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Need to send the message to a subscriber which is : "
                            + currentConsumerEdp.getAddress().getValue());
                }

                // we use a WSA endpoint to send the notification...
                // extract data from address
                URI uri = currentConsumerEdp.getAddress().getValue();
                Message message = null;

                if (EndpointHelper.isDSBService(uri)) {
                    message = new MessageImpl();
                    message.setEndpoint(EndpointHelper.getEndpoint(uri));
                    message.setService(EndpointHelper.getService(uri));
                    
                } else if (AddressingHelper.isExternalService(uri)) {
                    message = new WSAMessageImpl(uri.toString());
                } else {
                    System.out.println("Internal service : TODO NotificationEngine class!");
                    return;
                }

                try {
                    final Document payload = Wsnb4ServUtils.getWsnbWriter()
                            .writeNotifyAsDOM(notify);

                    message.setPayload(payload);
                    message.setOperation(WsnbConstants.NOTIFY_QNAME);
                    client.fireAndForget(message);
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (WsnbException e) {
                    e.printStackTrace();
                }

                // need to map between the address and the DSB endpoint to send
                // the message to... then we may use some WS-Addressing thing to
                // pass the initial address...
            }
        };

        // The one which receives notifications from consumers, and forward them to the notification engine
        this.notificationConsumerEngine = new AbsNotificationConsumerEngine(logger) {

            @Override
            public void notify(Notify notify) {
                // the goal of the component is to forward the notifiy messages
                // to the notification engine so that it is up to the
                // notification engine to forward the notification to all the
                // interested parties.
                if (logger.isLoggable(Level.FINE)) {

                    logger.fine("--- Got a notify, forward to internal engine ---");
                    try {
                        Document doc = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                        logger.fine(XMLHelper.createStringFromDOMDocument(doc));
                    } catch (WsnbException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                    logger.fine("-------------------------");
                }
                
                try {
                    internalNotificationSender.notify(notify);
                } catch (NotificationException e) {
                    // toulouse we'we got a problem!
                    e.printStackTrace();
                }
            }
        };
        consumerWSDL = loadDocument("WS-NotificationConsumer.wsdl");
        producerWSDL = loadDocument("WS-NotificationProducer.wsdl");

        this.serviceEngine = new ServiceEngine();
        this.serviceEngine.addService(new NotificationProducerRPService(null, null, null, null,
                null, this.getNotificationManager().getNotificationProducerEngine()), new QName[] {
                new QName(WsrfrConstants.WS_RESOURCE_NAMESPACE_URI, "GetResourceProperty"),
                new QName(WsrfrConstants.WS_RESOURCE_NAMESPACE_URI, "UpdateResourceProperties") });
    }

    /**
     * @param string
     * @return
     */
    private Wsdl loadDocument(String string) {
        try {
            return new ComponentWsdl(UtilFactory.getWSDLUtil().createWsdlDescription(
                    AbstractComponent.class.getResource("/" + string), true));
        } catch (WSDLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the notificationManager
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /**
     * @return the consumerWSDL
     */
    public Wsdl getConsumerWSDL() {
        return consumerWSDL;
    }

    /**
     * @return the producerWSDL
     */
    public Wsdl getProducerWSDL() {
        return producerWSDL;
    }

    /**
     * @return
     */
    public AbsNotificationConsumerEngine getNotificationConsumerEngine() {
        return this.notificationConsumerEngine;
    }

    public ServiceEngine getServiceEngine() {
        return this.serviceEngine;
    }
}
