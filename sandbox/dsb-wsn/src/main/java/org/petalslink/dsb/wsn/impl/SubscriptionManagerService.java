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

import java.io.File;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.addressing.datatypes.api.utils.WsaException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
// import
// com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.PauseSubscription;
// import
// com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.PauseSubscriptionResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Renew;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.RenewResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.persistence.WsnbPersistence;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsnb.services.transport.ITransporterForWsnbPublisher;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * The WS-BrokeredNotification service implementation
 * 
 * @author tdejean - eBMWebsourcing
 * 
 */
public class SubscriptionManagerService {

    private final String PETALS_NAMESPACE = "http://petals.ow2.org/petals-se-SubscriptionManager";

    private final String persistedSubscriptionFolderName = "SaveSubscriptionManagerRP"
            + File.separatorChar;

    private final QName SUBSCRIPTION_MANAGER_SERVICE_QNAME = new QName(PETALS_NAMESPACE,
            "SubscriptionManagerService");

    private final QName SUBSCRIPTION_MANAGER_INTERFACE_QNAME = new QName(
            WsnbConstants.WS_BASE_NOTIFICATION_NAMESPACE_URI, "SubscriptionManager");

    private final String SUBSCRIPTION_MANAGER_ENDPOINT = "SubscriptionManagerServiceEndpoint";

    private WsnbPersistence persistenceMgr = null;

    private SubscriptionManagerEngine subsMgrEngine = null;

    public SubscriptionManagerService(Logger logger, String mainPersistenceFolderName,
            ITransporterForWsnbPublisher notificationSender) throws WsnbException {

        if (mainPersistenceFolderName != null) {
            this.persistenceMgr = new WsnbPersistence(new File(mainPersistenceFolderName
                    + File.separatorChar + persistedSubscriptionFolderName));
        }

        this.subsMgrEngine = new SubscriptionManagerEngine(logger, /*
                                                                    * this.
                                                                    * topicsMgrEngine
                                                                    * ,
                                                                    */this.persistenceMgr,
                notificationSender);

        this.subsMgrEngine.setSubscriptionsManagerEdp(SUBSCRIPTION_MANAGER_ENDPOINT);
        this.subsMgrEngine.setSubscriptionsManagerInterface(SUBSCRIPTION_MANAGER_INTERFACE_QNAME);
        this.subsMgrEngine.setSubscriptionsManagerService(SUBSCRIPTION_MANAGER_SERVICE_QNAME);

    }

    public void restorePreviousState(TopicsManagerEngine topicsMgr, Document supportedTopicsAsDOM)
            throws AbsWSStarFault, WsnbException {
        this.subsMgrEngine.restorePersistedSubscriptions(topicsMgr, supportedTopicsAsDOM);
    }

    public SubscriptionManagerEngine getSubsMgrEngine() {
        return subsMgrEngine;
    }

    public QName getSUBSCRIPTION_MANAGER_SERVICE_QNAME() {
        return SUBSCRIPTION_MANAGER_SERVICE_QNAME;
    }

    public QName getSUBSCRIPTION_MANAGER_INTERFACE_QNAME() {
        return SUBSCRIPTION_MANAGER_INTERFACE_QNAME;
    }

    public String getSUBSCRIPTION_MANAGER_ENDPOINT() {
        return SUBSCRIPTION_MANAGER_ENDPOINT;
    }

    public UnsubscribeResponse unsubscribe(EndpointReferenceType subscriptionRef,
            Unsubscribe payload) throws WsnbException, AbsWSStarFault {
        String targetSubscriptionUuid;
        try {
            targetSubscriptionUuid = Wsnb4ServUtils
                    .getSubscriptionIdFromReferenceParams(subscriptionRef.getReferenceParameters());
        } catch (WsaException e) {
            throw new WsnbException(e);
        }
        this.subsMgrEngine.setTargetSubscriptionResourceUuid(targetSubscriptionUuid);

        return this.subsMgrEngine.unsubscribe(payload);

    }

    public RenewResponse renew(EndpointReferenceType subscriptionRef, Renew payload)
            throws WsnbException, AbsWSStarFault {
        String targetSubscriptionUuid;
        try {
            targetSubscriptionUuid = Wsnb4ServUtils
                    .getSubscriptionIdFromReferenceParams(subscriptionRef.getReferenceParameters());
        } catch (WsaException e) {
            throw new WsnbException(e);
        }
        this.subsMgrEngine.setTargetSubscriptionResourceUuid(targetSubscriptionUuid);

        return this.subsMgrEngine.renew(payload);
    }

    /*
     * public ResumeSubscriptionResponse resumeSubscription(ResumeSubscription
     * request) throws WsnbException, AbsWSStarFault { return
     * this.subsMgrEngine.resumeSubscription(request); }
     * 
     * public PauseSubscriptionResponse pauseSubscription(PauseSubscription
     * request) throws WsnbException, AbsWSStarFault { return
     * this.subsMgrEngine.pauseSubscription(request); }
     */

}
