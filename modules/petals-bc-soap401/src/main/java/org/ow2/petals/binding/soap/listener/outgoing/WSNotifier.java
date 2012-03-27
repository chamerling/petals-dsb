/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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

package org.ow2.petals.binding.soap.listener.outgoing;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLUtils;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.ws.notification.WsnManager;
import org.w3c.dom.Document;

import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.TOPIC;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class WSNotifier extends AbstractExternalServiceCaller {

    /**
     * Creates a new instance of {@link WSNotifier}
     * 
     * @param soapContext
     * @param logger
     */
    public WSNotifier(final SoapComponentContext soapContext, final Logger logger) {
        super(soapContext, logger);
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.binding.soap.listener.outgoing.ExternalServiceCaller#call(org.ow2.petals.component.framework.api.message.Exchange, org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions, org.ow2.petals.component.framework.jbidescriptor.generated.Provides)
     */
    public void call(final Exchange exchange, final ConfigurationExtensions cdkExtensions,
            final Provides provides) {
        final QName topicName = new QName(SUPropertiesHelper.getTopicName(cdkExtensions));
        this.logger.fine("Posting message to topic : " + topicName);

        if (exchange.isInOnlyPattern() || exchange.isRobustInOnlyPattern()) {

            if (!"".equals(topicName.toString())) {
                this.sendNotification(topicName, exchange);
            } else {
                final String message = "No topic name has been found, the message can not be delivered";
                this.logger.warning(message);
                try {
                    exchange.setFault(new Exception(message));
                } catch (final MessagingException e1) {
                    this.logger.log(Level.SEVERE, "Can't return fault to consumer", e1);
                }
            }

        } else {
            final String message = "Can not handle the MEP '" + exchange.getPattern().toString()
                    + "' on WS Notification";
            this.logger.warning(message);
            try {
                exchange.setFault(new Exception(message));
            } catch (final MessagingException e1) {
                this.logger.log(Level.SEVERE, "Can't return fault to consumer", e1);
            }
        }
    }

    /**
     * Send a notification to all the topic subscribers
     * 
     * @param topicName
     * @param exchange
     */
    protected void sendNotification(final QName topicName, final Exchange exchange) {

        final WsnManager manager = this.soapContext.getWsnManager();

        try {
            final OMElement notification = this.createNotificationContentFromJBIMessage(exchange);
            manager.publish(topicName, notification, new Axis2NotificationConsumerClient(
                    this.logger));
        } catch (final Exception e) {
            try {
                exchange.setFault(e);
            } catch (final MessagingException e1) {
                this.logger.log(Level.SEVERE, "Can't return fault to consumer", e1);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.binding.soap.listener.outgoing.JBIListenerDispatcher#getDispatcherType()
     */
    public String getCallerType() {
        return TOPIC;
    }

    /**
     * Create the notification message from the JBI message exchange
     * 
     * @param exchange
     * @return
     */
    public OMElement createNotificationContentFromJBIMessage(final Exchange exchange)
            throws Exception {
        final Document contentDocument = exchange.getInMessageContentAsDocument();
        return XMLUtils.toOM(contentDocument.getDocumentElement());
    }
}
