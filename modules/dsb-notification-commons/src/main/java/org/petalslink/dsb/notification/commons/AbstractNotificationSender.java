/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;

/**
 * An abstract notificaiton sender implementation. The final implementation just
 * needs to implement {@link #doNotify(Notify)} in order to use the right
 * transport layer to send the Notify message. All the logic is already done in
 * the {@link #notify(Document, QName, String)} method which gets all the
 * destinations to send notify to...
 * 
 * @author chamerling
 * 
 */
public abstract class AbstractNotificationSender implements NotificationSender {

    private final static Logger log = Logger.getLogger(AbstractNotificationSender.class.getName());

    private NotificationProducerEngine producer;

    private DocumentBuilderFactory documentFactory = null;

    public AbstractNotificationSender(final NotificationProducerEngine producer) {
        this.producer = producer;
        this.documentFactory = DocumentBuilderFactory.newInstance();
        this.documentFactory.setNamespaceAware(true);
    }

    public void notify(Document payload, final QName topic, final String dialect)
            throws NotificationException {
        String endpointNameAddress = null;
        try {
            final List<String> uuids = producer.getSubsMgr().getStoredSubscriptionUuids();
            TopicExpressionType exp = NotificationHelper.createTopicExpression(topic, dialect);
            boolean setCurrentMessage = false;

            for (final String subscriptionId : uuids) {
                List<TopicExpressionType> topicExps = producer.getSubsMgr()
                        .getTopicExpressionOfSubscription(subscriptionId);
                if (topicExps != null) {
                    for (TopicExpressionType topicExp : topicExps) {
                        if (topicExp != null) {
                            String topicExpS = topicExp.getContent().substring(
                                    topicExp.getContent().indexOf(":") + 1);
                            String expS = exp.getContent().substring(
                                    exp.getContent().indexOf(":") + 1);
                            if (topicExpS.equals(expS)) {
                                EndpointReferenceType currentConsumerEdp = producer.getSubsMgr()
                                        .getConsumerEdpRefOfSubscription(subscriptionId);

                                if (currentConsumerEdp != null) {
                                    if (log.isLoggable(Level.FINE)) {
                                        log.fine("currentConsumerEdp.getAddress().getValue() = "
                                                + currentConsumerEdp.getAddress().getValue());
                                    }

                                    if (currentConsumerEdp.getAddress().getValue().toString()
                                            .indexOf("::") > 0) {
                                        endpointNameAddress = "{"
                                                + currentConsumerEdp.getAddress().getValue()
                                                        .toString().split("::")[0]
                                                + "}"
                                                + currentConsumerEdp.getAddress().getValue()
                                                        .toString().split("::")[1];
                                    } else {
                                        endpointNameAddress = currentConsumerEdp.getAddress()
                                                .getValue().toString();
                                    }

                                    String producerAddress = getProducerAddress();

                                    final Notify notify = NotificationHelper.createNotification(
                                            producerAddress, currentConsumerEdp.getAddress()
                                                    .getValue().toString(), subscriptionId, topic,
                                            dialect, payload);
                                    this.producer.setCurrentMessage(NotificationHelper
                                            .createTopicExpression(topic, dialect), notify
                                            .getNotificationMessage().get(0).getMessage(), false);
                                    setCurrentMessage = true;

                                    // really send the notification...
                                    if (log.isLoggable(Level.FINE)) {
                                        log.fine(String.format("Sending notify to %s",
                                                currentConsumerEdp.getAddress().getValue()
                                                        .toString()));
                                    }
                                    try {
                                        doNotify(notify, producerAddress, currentConsumerEdp,
                                                subscriptionId, topic, dialect);
                                    } catch (NotificationException e) {
                                        // handle exception to aviod to break
                                        // loop...
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!setCurrentMessage) {
                String producerAddress = getProducerAddress();
                Notify notify = NotificationHelper.createNotification(producerAddress, null, null,
                        topic, dialect, payload);
                this.producer.setCurrentMessage(
                        NotificationHelper.createTopicExpression(topic, dialect), notify
                                .getNotificationMessage().get(0).getMessage(), false);
            }
        } catch (Exception e) {
            log.severe("Impossible to send notification to " + endpointNameAddress + " : "
                    + e.getMessage());
        }
    }

    /**
     * Really send the notification using the implementation transport layer.
     * 
     * @throws NotificationException
     */
    protected abstract void doNotify(Notify notify, String producerAddress,
            EndpointReferenceType currentConsumerEdp, String subscriptionId, QName topic,
            String dialect) throws NotificationException;

    /**
     * Who am I?
     * 
     * @return
     */
    protected abstract String getProducerAddress();
}
