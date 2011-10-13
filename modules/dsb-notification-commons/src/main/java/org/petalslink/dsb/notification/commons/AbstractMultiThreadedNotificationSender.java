/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;

/**
 * An abstract notificaiton sender implementation which does the stuff in
 * multithreaded mode. The final implementation just needs to implement
 * {@link #doNotify(Notify)} in order to use the right transport layer to send
 * the Notify message. All the logic is already done in the
 * {@link #notify(Document, QName, String)} method which gets all the
 * destinations to send notify to...
 * 
 * @author chamerling
 * 
 */
public abstract class AbstractMultiThreadedNotificationSender implements NotificationSender {

    private final static Logger log = Logger
            .getLogger(AbstractMultiThreadedNotificationSender.class.getName());

    private NotificationProducerEngine producer;

    private DocumentBuilderFactory documentFactory;

    private ExecutorService executorService;

    public AbstractMultiThreadedNotificationSender(final NotificationProducerEngine producer) {
        this.producer = producer;
        this.documentFactory = DocumentBuilderFactory.newInstance();
        this.documentFactory.setNamespaceAware(true);

        // TODO : This class may be a service so we can clean and manage this
        // executor service
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.notification.commons.api.NotificationSender#notify
     * (com.
     * ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify)
     */
    public final void notify(final Notify notify) throws NotificationException {
        // go in all the messages and call #notify method
        List<NotificationMessageHolderType> messages = notify.getNotificationMessage();
        for (NotificationMessageHolderType notificationMessageHolderType : messages) {
            TopicExpressionType topic = notificationMessageHolderType.getTopic();
            String topicContent = topic.getContent();
            // create a qname from the topic content
            String prefix = null;
            String localPart = null;
            if (topicContent.contains(":")) {
                prefix = topicContent.substring(0, topicContent.indexOf(":"));
                localPart = topicContent.substring(topicContent.indexOf(":") + 1);
            }
            // get the NS for the prefix
            String ns = null;
            if (prefix != null && topic.getTopicNamespaces() != null) {
                boolean found = false;
                Iterator<QName> iter = topic.getTopicNamespaces().iterator();
                while (iter.hasNext() && !found) {
                    QName qname = iter.next();
                    if (prefix.equals(qname.getLocalPart())) {
                        ns = qname.getNamespaceURI();
                        found = true;
                    }
                }
            }

            QName topicName = new QName(ns, localPart, prefix);
            Element element = notificationMessageHolderType.getMessage().getAny();
            Document dom = null;
            if (element != null) {
                dom = element.getOwnerDocument();
            }
            String dialect = topic.getDialect().toString();
            this.notify(dom, topicName, dialect);
        }
    }

    public final void notify(final Document payload, final QName topic, final String dialect)
            throws NotificationException {
        try {
            final List<String> uuids = producer.getSubsMgr().getStoredSubscriptionUuids();
            final TopicExpressionType exp = NotificationHelper
                    .createTopicExpression(topic, dialect);

            for (final String subscriptionId : uuids) {
                // dispatch all subscribers processing
                Thread t = new Thread() {
                    public void run() {

                        try {
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
                                            EndpointReferenceType currentConsumerEdp = producer
                                                    .getSubsMgr().getConsumerEdpRefOfSubscription(
                                                            subscriptionId);

                                            if (currentConsumerEdp != null) {
                                                if (log.isLoggable(Level.FINE)) {
                                                    log.fine("currentConsumerEdp.getAddress().getValue() = "
                                                            + currentConsumerEdp.getAddress()
                                                                    .getValue());
                                                }

                                                String producerAddress = getProducerAddress();
                                                final Notify notify = NotificationHelper
                                                        .createNotification(producerAddress,
                                                                currentConsumerEdp.getAddress()
                                                                        .getValue().toString(),
                                                                subscriptionId, topic, dialect,
                                                                payload);

                                                if (log.isLoggable(Level.FINE)) {
                                                    log.fine(String.format("Sending notify to %s",
                                                            currentConsumerEdp.getAddress()
                                                                    .getValue().toString()));
                                                }
                                                try {
                                                    doNotify(notify, producerAddress,
                                                            currentConsumerEdp, subscriptionId,
                                                            topic, dialect);
                                                } catch (NotificationException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (WsnbException e) {
                            e.printStackTrace();
                        } catch (NotificationException e) {
                            e.printStackTrace();
                        }

                    };
                };
                this.executorService.submit(t);
            }

            /*
             * if (setCurrentMessage) { String producerAddress =
             * getProducerAddress(); Notify notify =
             * NotificationHelper.createNotification(producerAddress, null,
             * null, topic, dialect, payload); this.producer.setCurrentMessage(
             * NotificationHelper.createTopicExpression(topic, dialect), notify
             * .getNotificationMessage().get(0).getMessage(), false); }
             */
        } catch (Exception e) {
            e.printStackTrace();
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
     * This will be used in the notification message to say who am I.
     * 
     * @return
     */
    protected abstract String getProducerAddress();

}
