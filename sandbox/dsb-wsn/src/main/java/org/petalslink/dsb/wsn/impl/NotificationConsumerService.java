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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.common.utils.WsstarCommonUtils;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.api.WsrfrlConstants;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.WsrfrpConstants;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.AbsNotificationConsumerEngine;

/**
 * A raw service implementation which is just in charge of receiving
 * notifications... ie the notification consumer
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerService extends AbsNotificationConsumerEngine {

    protected static final String RECEIVED_TERMINATION_NOTIFICATION = "receivedTerminationNotification";

    protected static final String RECEIVED_RP_VALUE_CHANGE_NOTIFICATION = "receivedRPValueChangeNotification";

    protected static final String RECEIVED_BUSINESS_LOGIC_NOTIFICATION = "receivedBusinessLogicNotification";

    private Map<String, Object> properties = null;

    private boolean isDebug = false;

    public NotificationConsumerService(Logger logger, Map<String, Object> properties,
            boolean isDebug) {
        super(logger);
        this.properties = properties;
        this.isDebug = isDebug;
    }

    public void notify(Notify request) {

        // Business logic part :
        // Extract the "business logic" message part and store it
        if (this.isDebug) {
            String msg = "\t[DEBUG - From consumer] Performs \"buisness code\" on receiving this \"Notify\" request...\n";
            this.logger.log(Level.FINE, msg);
            System.out.println(msg);
        }

        List<NotificationMessageHolderType> msgs = request.getNotificationMessage();

        Element firstBusinessMsg = msgs.get(0).getMessage().getAny();
        if (this.isDebug) {
            System.out.println("\t[DEBUG - From consumer] notification message details : \n"
                    + WsstarCommonUtils.prettyPrint(firstBusinessMsg.getOwnerDocument()));
        }
        if (firstBusinessMsg.getLocalName().contains(
                WsrfrlConstants.TERMINATION_NOTIFICATION_QNAME.getLocalPart())
                && firstBusinessMsg.getNamespaceURI().equals(
                        WsrfrlConstants.TERMINATION_NOTIFICATION_QNAME.getNamespaceURI())) {
            this.properties.put(NotificationConsumerService.RECEIVED_TERMINATION_NOTIFICATION,
                    firstBusinessMsg);
        } else if (firstBusinessMsg.getLocalName().contains(
                WsrfrpConstants.RESOURCE_PROPERTY_VALUE_CHANGE_NOTIFICATION_QNAME.getLocalPart())
                && firstBusinessMsg.getNamespaceURI().equals(
                        WsrfrpConstants.RESOURCE_PROPERTY_VALUE_CHANGE_NOTIFICATION_QNAME
                                .getNamespaceURI())) {
            this.properties.put(NotificationConsumerService.RECEIVED_RP_VALUE_CHANGE_NOTIFICATION,
                    firstBusinessMsg);
        } else {
            this.properties.put(NotificationConsumerService.RECEIVED_BUSINESS_LOGIC_NOTIFICATION,
                    firstBusinessMsg);
        }
    }

}
