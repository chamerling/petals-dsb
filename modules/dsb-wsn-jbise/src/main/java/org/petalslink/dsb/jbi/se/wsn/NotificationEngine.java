/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.ComponentWsdl;
import org.ow2.petals.component.framework.api.Constants;
import org.ow2.petals.component.framework.api.Wsdl;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.petalslink.dsb.api.WSAConstants;
import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.NotificationProducerRP;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.notification.service.NotificationProducerRPService;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
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

    private List<String> supportedTopics;

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

    public NotificationEngine(Logger logger, URL topicNamespaces, List<String> supportedTopics,
            QName serviceName, QName interfaceName, String endpointName, Client client) {
        super();
        this.topicNamespaces = topicNamespaces;
        this.supportedTopics = supportedTopics;
        this.serviceName = serviceName;
        this.interfaceName = interfaceName;
        this.endpointName = endpointName;
        this.client = client;
    }

    /**
     * 
     */
    public void init() {
        this.notificationManager = new NotificationManagerImpl(topicNamespaces, supportedTopics,
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
                System.out.println("Need to send the message to a subscriber which is : "
                        + currentConsumerEdp.getAddress().getValue());

                if (currentConsumerEdp == null || currentConsumerEdp.getAddress() == null
                        || currentConsumerEdp.getAddress().getValue() == null) {
                    // no address found...
                    return;
                }

                // we use a WSA endpoint to send the notification...
                // extract data from address
                URI uri = currentConsumerEdp.getAddress().getValue();
                String componentName = null;
                String ns = null;
                String serviceName = null;
                String interfaceName = null;
                String ep = null;
                String address = null;

                if (AddressingHelper.isExternalService(uri)) {
                    // use WSA
                    componentName = AddressingHelper.getComponent(uri);
                    ns = String.format(WSAConstants.NS_TEMPLATE, componentName);
                    serviceName = WSAConstants.SERVICE_NAME;
                    interfaceName = WSAConstants.INTERFACE_NAME;
                    ep = WSAConstants.ENDPOINT_NAME;
                    address = AddressingHelper.getInitialAddress(uri);
                } else {
                    // URI is service@endpoint
                    componentName = AddressingHelper.getComponent(uri);
                    ns = String.format(WSAConstants.NS_TEMPLATE, componentName);
                    serviceName = AddressingHelper.getServiceName(uri);
                    // interfaceName = AddressingHelper.getInterfaceName(uri);
                    ep = AddressingHelper.getEndpointName(uri);
                }

                final QName service = new QName(ns, serviceName);
                final QName interfaceQName = new QName(ns, interfaceName);
                final String endpoint = ep;
                final String to = address;

                try {
                    final Document payload = Wsnb4ServUtils.getWsnbWriter()
                            .writeNotifyAsDOM(notify);

                    MessageImpl message = new MessageImpl();
                    message.setService(service);
                    message.setPayload(payload);
                    message.setEndpoint(endpoint);
                    message.setInterface(interfaceQName);
                    message.setOperation(WsnbConstants.NOTIFY_QNAME);
                    message.setProperty(Constants.WSStar.Addressing.TO_QNAME.toString(), to);

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

        this.notificationConsumerEngine = new AbsNotificationConsumerEngine(logger) {

            @Override
            public void notify(Notify notify) {
                // the goal of the component is to forward the notifiy messages
                // to the notification engine so that it is up to the
                // notification engine to forward the notification to all the
                // interested parties.
                System.out.println("--- Got a notify, forward to internal engine ---");
                try {
                    Document doc = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                    XMLHelper.writeDocument(doc, System.out);
                } catch (WsnbException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
                System.out.println("-------------------------");
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
