/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.notification.commons.AbstractNotificationSender;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationManagerImpl;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.WSAMessageImpl;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * The Notification Manager component. It is automatically initialized at DSB
 * startup. It creates all that is needed to use notification and set them in
 * the Notification Center.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationManager.class) })
public class NotificationManagerServiceImpl implements NotificationManager {

    @Requires(name = "configuration", signature = NotificationConfiguration.class)
    private NotificationConfiguration configuration;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private NotificationManager manager;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener(priority = 10001)
    public void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing notification manager...");
        }
        this.manager = new NotificationManagerImpl(configuration.getTopicSet(),
                configuration.getTopicNamespaces(), configuration.getServiceName(),
                configuration.getInterfaceName(), configuration.getEndpointName());
        NotificationCenter.get().setNotificationManager(this);

        AbstractNotificationSender sender = new AbstractNotificationSender(
                this.manager.getNotificationProducerEngine()) {

            @Override
            protected String getProducerAddress() {
                return "dsb://KernelService";
            }

            @Override
            protected final void doNotify(Notify notify, String producerAddress,
                    EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
                    String dialect) throws NotificationException {

                if (currentConsumerEdp == null || currentConsumerEdp.getAddress() == null
                        || currentConsumerEdp.getAddress().getValue() == null) {
                    // no address found...
                    log.debug("No address found, do not send notification");
                    return;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Need to send the message to a subscriber which is : "
                            + currentConsumerEdp.getAddress().getValue());
                }

                // we use a WSA endpoint to send the notification...
                // extract data from address
                URI uri = currentConsumerEdp.getAddress().getValue();
                Message message = null;

                Client client = null;

                if (isExternalService(uri)) {
                    message = new WSAMessageImpl(uri.toString());
                    ServiceEndpoint se = new ServiceEndpoint();
                    se.setEndpointName(message.getEndpoint());
                    se.setServiceName(message.getService());
                    se.setInterfaces(new QName[] { message.getInterface() });
                    try {
                        client = ClientFactoryRegistry.getFactory().getClient(se);
                    } catch (ClientException e) {
                        if (log.isDebugEnabled()) {
                            e.printStackTrace();
                        }
                        throw new NotificationException(e.getMessage());
                    }
                } else {
                    System.out.println("!!! Internal service : TODO NotificationSender class!!!");
                    return;
                    // URI is service@endpoint
                    /*
                     * componentName = AddressingHelper.getComponent(uri); ns =
                     * String.format(WSAConstants.NS_TEMPLATE, componentName);
                     * serviceName = AddressingHelper.getServiceName(uri); ep =
                     * AddressingHelper.getEndpointName(uri);
                     */
                    // TODO how to define internal addresses???
                }

                try {
                    final Document payload = Wsnb4ServUtils.getWsnbWriter()
                            .writeNotifyAsDOM(notify);

                    message.setPayload(payload);
                    message.setOperation(WsnbConstants.NOTIFY_QNAME);
                    if (client != null) {
                        client.fireAndForget(message);
                    } else {
                        log.error("Can not get client to send message");
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (WsnbException e) {
                    e.printStackTrace();
                } finally {
                    if (client != null) {
                        // releasing client
                        System.out.println("Releasing client");
                        try {
                            ClientFactoryRegistry.getFactory().release(client);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        };
        NotificationCenter.get().setNotifificationSender(sender);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.NotificationManager#getTopicNamespace
     * ()
     */
    public TopicNamespaceType getTopicNamespace() {
        return manager.getTopicNamespace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.api.NotificationManager#
     * getSupportedTopics()
     */
    public List<String> getSupportedTopics() {
        return manager.getSupportedTopics();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.NotificationManager#getTopicSet()
     */
    public TopicSetType getTopicSet() {
        return manager.getTopicSet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getTopicsManagerEngine()
     */
    public TopicsManagerEngine getTopicsManagerEngine() {
        return manager.getTopicsManagerEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getSubscriptionManagerEngine()
     */
    public SubscriptionManagerEngine getSubscriptionManagerEngine() {
        return manager.getSubscriptionManagerEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.NotificationManager#
     * getNotificationProducerEngine()
     */
    public NotificationProducerEngine getNotificationProducerEngine() {
        return manager.getNotificationProducerEngine();
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
