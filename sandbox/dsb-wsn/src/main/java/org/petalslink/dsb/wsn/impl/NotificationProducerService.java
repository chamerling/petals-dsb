/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.wsn.impl;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.jaxb.addressing.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.GetResourcePropertyResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourceProperties;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourcePropertiesResponse;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.utils.WstopException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsnb.services.transport.ITransporterForWsnbPublisher;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * 
 * @author chamerling
 * 
 */
public class NotificationProducerService implements INotificationProducer, INotificationProducerRP {

    private final String PETALS_NAMESPACE = "http://petals.ow2.org/petals-se-NotificationProducer";

    // private final String persistedSubscriptionFolderName =
    // "SaveSubscriptionManagerRP" + File.separatorChar;

    private final QName NOTIFICATION_PRODUCER_SERVICE_QNAME = new QName(PETALS_NAMESPACE,
            "NotificationBrokerService");

    private final QName NOTIFICATION_PRODUCER_INTERFACE_QNAME = new QName(
            WsnbConstants.WS_BASE_NOTIFICATION_NAMESPACE_URI, "NotificationProducer");

    private final String NOTIFICATION_PRODUCER_ENDPOINT = "NotificationProducerServiceEndpoint";

    // private final QName SUBSCRIPTION_MANAGER_SERVICE_QNAME = new
    // QName(PETALS_NAMESPACE , "SubscriptionManagerService");
    // private final QName SUBSCRIPTION_MANAGER_INTERFACE_QNAME = new
    // QName(WsnbConstants.WS_BASE_NOTIFICATION_NAMESPACE_URI ,
    // "SubscriptionManager");
    // private final String SUBSCRIPTION_MANAGER_ENDPOINT =
    // "SubscriptionManagerEndpoint";

    // private WsnbPersistence persistenceMgr = null;
    //
    // private Map<String, Notify> notifyPayloadToForward = null;
    //
    // private ConcurrentHashMap<String, EndpointReferenceType>
    // subscriptionOnProducers = null;
    //
    private NotificationProducerEngine notifProdEngine = null;

    private TopicsManagerEngine/* WsnbTopicsMgrEngine */topicsMgrEngine = null;

    private SubscriptionManagerService subsMgrServ = null;

    private ITransporterForWsnbPublisher notifSender = null;

    public NotificationProducerService(Logger logger, InputStream supportedTopicsConfig,
            boolean isFixedTopicSet, TopicNamespaceType topicNSForRPChangeValueNotif,
            String nsPrefix, String mainPersistenceFolderName,
            ITransporterForWsnbPublisher transporter) throws WsnbException {

        this.notifSender = transporter;

        // this.persistenceMgr = new WsnbPersistence(new
        // File(mainPersistenceFolderName + File.separatorChar +
        // persistedSubscriptionFolderName));

        this.topicsMgrEngine = new /* WsnbTopicsMgrEngine */TopicsManagerEngine(/* supportedTopicsConfig */);

        this.subsMgrServ = new SubscriptionManagerService(logger/*
                                                                 * , this.
                                                                 * topicsMgrEngine
                                                                 * supportedTopicsConfig
                                                                 */, mainPersistenceFolderName,
                this.notifSender);

        // this.subsMgrEngine.setSubscriptionsManagerEdp(SUBSCRIPTION_MANAGER_ENDPOINT);
        // this.subsMgrEngine.setSubscriptionsManagerInterface(SUBSCRIPTION_MANAGER_INTERFACE_QNAME);
        // this.subsMgrEngine.setSubscriptionsManagerService(SUBSCRIPTION_MANAGER_SERVICE_QNAME);

        TopicSetType supportedTopics;
        try {
            supportedTopics = Wsnb4ServUtils.getWstopReader().readTopicSetType(
                    new InputSource(supportedTopicsConfig));
        } catch (WstopException e) {
            throw new WsnbException(e);
        }

        this.notifProdEngine = new NotificationProducerEngine(logger, this.topicsMgrEngine,
                this.subsMgrServ.getSubsMgrEngine(), isFixedTopicSet, supportedTopics,
                topicNSForRPChangeValueNotif, nsPrefix, this.notifSender);

        this.notifProdEngine.setNotificationProducerEdp(NOTIFICATION_PRODUCER_ENDPOINT);
        this.notifProdEngine
                .setNotificationProducerInterface(NOTIFICATION_PRODUCER_INTERFACE_QNAME);
        this.notifProdEngine.setNotificationProducerService(NOTIFICATION_PRODUCER_SERVICE_QNAME);

        // /*
        // wsnbPullPointService = new PullPointMgr(logger);
        // wsnbCreatePullPointService = new CreatePullPointMgr(logger,
        // wsnbPullPointService);
        // */
        //
        // this.subscriptionOnProducers = new ConcurrentHashMap<String,
        // EndpointReferenceType>();
        //
        // //this.initActorAsRPAndAttributes(true);
        //
        // this.notifyPayloadToForward = new HashMap<String, Notify>();

    }

    public void restorePreviousState() throws AbsWSStarFault, WsnbException {

        Document supportedTopicsAsDOM;
        try {
            supportedTopicsAsDOM = Wsnb4ServUtils.getWstopWriter().writeTopicSetTypeAsDOM(
                    this.notifProdEngine.getActorAsRP().getTopicSet());
        } catch (WstopException e) {
            throw new WsnbException(e);
        }
        this.subsMgrServ.restorePreviousState(this.topicsMgrEngine, supportedTopicsAsDOM);
    }

    public/* WsnbTopicsMgrEngine */TopicsManagerEngine getWstopTopicsMgr() {
        return this.topicsMgrEngine;
    }

    public SubscriptionManagerService getSubscriptionManagerService() {
        return subsMgrServ;
    }

    public QName getSUBSCRIPTION_MANAGER_SERVICE_QNAME() {
        return this.subsMgrServ.getSUBSCRIPTION_MANAGER_SERVICE_QNAME();
    }

    public QName getNOTIFICATION_PRODUCER_INTERFACE_QNAME() {
        return NOTIFICATION_PRODUCER_INTERFACE_QNAME;
    }

    public String getSUBSCRIPTION_MANAGER_SERVICE_EDP() {
        return this.subsMgrServ.getSUBSCRIPTION_MANAGER_ENDPOINT();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#subscribe
     * (com.ebmwebsourcing.wsstar.notification.api.basenotification.abstraction.
     * Subscribe)
     */
    public SubscribeResponse subscribe(Subscribe payload) throws WsnbException, AbsWSStarFault {
        return this.notifProdEngine.subscribe(payload);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#
     * getCurrentMessage
     * (com.ebmwebsourcing.wsstar.notification.api.basenotification
     * .abstraction.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage payload)
            throws WsnbException, AbsWSStarFault {
        return this.notifProdEngine.getCurrentMessage(payload);
    }

    /**
     * 
     * @param topic
     * @param notification
     * @param isSameNotifyRequest
     * @throws WsnbException
     * @throws AbsWSStarFault
     */
    public void setCurrentNotifyMessage(TopicExpressionType topic,
            NotificationMessageHolderType.Message notification, boolean isSameNotifyRequest)
            throws WsnbException, AbsWSStarFault {
        this.notifProdEngine.setCurrentMessage(topic, notification, isSameNotifyRequest);
    }

    // /**
    // * Update "TopicSet" resource property
    // *
    // * @param newTopicSet new "TopicSet" property value
    // * @throws AbsWSStarFault
    // * @throws WsnbException
    // */
    // public boolean updateTopicSet(TopicSetType newTopicSet) throws
    // AbsWSStarFault, WsnbException {
    //
    // boolean result = false;
    // try {
    // //this.notifProdEngine.updateSupportedTopic(RefinedWstopFactory.getInstance().getWstopWriter().writeTopicSetTypeAsDOM(newTopicSet));
    // List<Element> properties= new CopyOnWriteArrayList<Element>();
    // properties.add(Wsnb4ServUtils.getWstopWriter().writeTopicSetTypeAsDOM(newTopicSet).getDocumentElement());
    //
    // UpdateType content =
    // RefinedWsrfrpFactory.getInstance().createUpdateType(properties);
    // UpdateResourceProperties request =
    // RefinedWsrfrpFactory.getInstance().createUpdateResourceProperties(content);
    //
    // result = (this.notifProdEngine.updateResourceProperties(request) !=
    // null);
    //
    // } catch (WstopException e) {
    // throw new WsnbException(e);
    // } catch (WsrfrpException e) {
    // throw new WsnbException(e);
    // }
    //
    // return result;
    // }
    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * getResourceProperty(javax.xml.namespace.QName)
     */
    public GetResourcePropertyResponse getResourceProperty(QName property) throws WsnbException,
            AbsWSStarFault {
        return this.notifProdEngine.getResourceProperty(property);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * updateResourceProperties
     * (com.ebmwebsourcing.wsstar.resourceproperties.datatypes
     * .api.abstraction.UpdateResourceProperties)
     */
    public UpdateResourcePropertiesResponse updateResourceProperties(
            UpdateResourceProperties request) throws WsnbException, AbsWSStarFault {
        return this.notifProdEngine.updateResourceProperties(request);
    }

    /**
     * Sample of method called to generate a "Notification" respect to a
     * situation and send it to targeted "NotificationConsumer"
     * 
     * @param payloadToUse
     *            the notification formatted as a "Notify" datatype
     * 
     * @throws AbsWSStarFault
     * @throws WsnbException
     */
    public void notifyNewSituation(Notify payloadToUse) throws WsnbException {
        // look for existing subscritpion ...
        System.out.println("Sending notification to consumers...");

        List<String> knownSubscriptions = this.subsMgrServ.getSubsMgrEngine()
                .getStoredSubscriptionUuids();

        // store notification for "GetCurrentMessage" request
        List<NotificationMessageHolderType> notifications = payloadToUse.getNotificationMessage();
        for (NotificationMessageHolderType notificationItem : notifications) {
            try {
                this.setCurrentNotifyMessage(notificationItem.getTopic(),
                        notificationItem.getMessage(), false);
            } catch (AbsWSStarFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for (String subscriptionId : knownSubscriptions) {
            // send notification ...
            com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType endpoint = this.subsMgrServ
                    .getSubsMgrEngine().getConsumerEdpRefOfSubscription(subscriptionId);
            System.out.println("ADRESS TO SEND NOTIFICATION TO " + endpoint.getAddress()
                    + " for subscription ID : " + subscriptionId);

            // let's have a look if we can send the message to the subscriber
            // based on the TOPIC he subscribed and on the current message
            // one...
            // TODO
            System.out.println("FILTER TO DO BEFORE SENDING");
            this.notifSender.sendNotifyRequest(endpoint, payloadToUse);
        }
    }

}
