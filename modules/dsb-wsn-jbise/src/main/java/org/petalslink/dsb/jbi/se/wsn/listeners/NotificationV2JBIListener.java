/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn.listeners;

import java.util.List;
import java.util.logging.Level;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.component.framework.api.Constants;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;
import org.ow2.petals.component.framework.message.ExchangeImpl;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.petalslink.dsb.jbi.se.wsn.Component;
import org.petalslink.dsb.jbi.se.wsn.NotificationEngine;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.element.Address;
import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.addressing.definition.api.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.addressing.definition.api.WSAddressingException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.WsnbConstants;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Notify;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Subscribe;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.SubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Unsubscribe;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WSNotificationExtensionException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WsnSpecificTypeHelper;
import com.ebmwebsourcing.wsstar.notification.service.fault.WSNotificationFault;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public abstract class NotificationV2JBIListener extends AbstractJBIListener {

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.listener.AbstractJBIListener#
     * onNotificationMessage
     * (org.ow2.petals.component.framework.api.message.Exchange)
     */
    @Override
    public boolean onNotificationMessage(Exchange exchange) {

        // bypass the old stuff and add new one...
        System.out.println(String.format("We have a notification message with operation '%s'", exchange.getOperation()));
        
        NotificationEngine engine = getNotificationEngine();

        boolean response = true;
        NormalizedMessage normalizedMessage = null;
        Document document = null;
        Address address = null;

        try {
            if (this.getLogger().isLoggable(Level.FINE)) {
                for (final QName intf : exchange.getEndpoint().getInterfaces()) {
                    this.getLogger().fine("notification endpoint interface: " + intf);
                }
                this.getLogger()
                        .fine("notification endpoint service: "
                                + exchange.getEndpoint().getServiceName());
                this.getLogger().fine(
                        "notification endpoint name: " + exchange.getEndpoint().getEndpointName());
                this.getLogger()
                        .fine("notification operation name: " + exchange.getOperationName());
            }

            if (exchange.isActiveStatus()) {
                if (exchange.getFault() != null) {

                    if (this.getLogger().isLoggable(Level.WARNING)) {
                        if (UtilFactory.getExchangeUtil().isPetalsException(exchange.getFault())) {
                            this.getLogger().warning(
                                    "notification technical fault message content: "
                                            + UtilFactory.getSourceUtil().createString(
                                                    exchange.getFault().getContent()));
                        } else {
                            this.getLogger().warning(
                                    "notification business fault message content: "
                                            + UtilFactory.getSourceUtil().createString(
                                                    exchange.getFault().getContent()));
                        }
                    }

                } else {
                    
                    normalizedMessage = exchange.getInMessage();
                    
                    if (WsnbConstants.SUBSCRIBE_NAME.equals(exchange.getOperation().getLocalPart())) {
                        document = UtilFactory.getSourceUtil().createDocument(
                                normalizedMessage.getContent());

                        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe = RefinedWsnbFactory
                                .getInstance().getWsnbReader().readSubscribe(document);

                        // address =
                        // subscribe.getConsumerReference().getAddress();
                        // call the producer
                        final com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse = engine
                                .getNotificationManager().getNotificationProducerEngine()
                                .subscribe(subscribe);
                        // set the response
                        document = RefinedWsnbFactory.getInstance().getWsnbWriter()
                                .writeSubscribeResponseAsDOM(subscribeResponse);
                        normalizedMessage = exchange.getOutMessage();
                        normalizedMessage.setContent(UtilFactory.getSourceUtil()
                                .createStreamSource(document));
                        exchange.setOutMessage(normalizedMessage);

                    } else if (WsnbConstants.NOTIFY_NAME.equals(exchange.getOperation().getLocalPart())) {
                        document = UtilFactory.getSourceUtil().createDocument(
                                normalizedMessage.getContent());

                        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify notify = RefinedWsnbFactory
                                .getInstance().getWsnbReader().readNotify(document);

                        final List<com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType> notificationMessageList = notify
                                .getNotificationMessage();

                        if (notificationMessageList == null || notificationMessageList.size() != 1) {
                            exchange.setError(new Exception(
                                    "The CDK need one and only one notification message"));
                        } else if (notificationMessageList.get(0).getSubscriptionReference() != null) {
                            address = notificationMessageList.get(0).getSubscriptionReference()
                                    .getAddress();
                        }

                        engine.getNotificationConsumerEngine().notify(notify);

                    } else if (WsnbConstants.UNSUBSCRIBE_NAME.equals(exchange.getOperation().getLocalPart())) {
                        document = UtilFactory.getSourceUtil().createDocument(
                                normalizedMessage.getContent());

                        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Unsubscribe unsubscribe = RefinedWsnbFactory
                                .getInstance().getWsnbReader().readUnsubscribe(document);

                        final com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.UnsubscribeResponse unsubscribeResponse = engine
                                .getNotificationManager().getSubscriptionManagerEngine()
                                .unsubscribe(unsubscribe);
                        document = RefinedWsnbFactory.getInstance().getWsnbWriter()
                                .writeUnsubscribeResponseAsDOM(unsubscribeResponse);
                        normalizedMessage = exchange.getOutMessage();
                        normalizedMessage.setContent(UtilFactory.getSourceUtil()
                                .createStreamSource(document));
                        exchange.setOutMessage(normalizedMessage);

                    } else if (WsnbConstants.GET_CURRENT_MESSAGE_NAME.equals(exchange
                            .getOperation().getLocalPart())) {
                        System.out.println("TODO");
                    } else if (WsnbConstants.RENEW_NAME.equals(exchange.getOperation().getLocalPart())) {
                        System.out.println("TODO");
                    } else {
                        exchange.setError(new Exception(
                                "unable to identify an operation of the WS-Notification specifications"));
                    }
                }
            }
        } catch (final PEtALSCDKException e) {
            exchange.setError(new Exception(e));
            // } catch (final WSNotificationFault f) {
            // try {
            // exchange.setFault(f);
            // } catch (final MessagingException e) {
            // exchange.setError(new Exception(e));
            // }
        } catch (WsnbException e) {
            exchange.setError(new Exception(e));
        } catch (AbsWSStarFault e) {
            exchange.setError(new Exception(e));
        } catch (MessagingException e) {
            exchange.setError(new Exception(e));
        }
        return response;
    }

    NotificationEngine getNotificationEngine() {
        return ((Component) getComponent()).getNotificationEngine();
    }
}
