/**
 * 
 */
package org.petalslink.dsb.notification.jaxws;

import javax.xml.bind.JAXBException;

import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.petalslink.dsb.notification.commons.NotificationProducer;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.GetCurrentMessageResponseImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.NotifyImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.SubscribeImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.SubscribeResponseImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * Facade which translates incoming JAXB calls into internal ones. FIXME : Some
 * model to model translations are not implemented...
 * 
 * @author chamerling
 * 
 */
public class NotificationProducerService implements
        org.petalslink.dsb.notification.jaxws.api.NotificationProducer {

    private NotificationProducer notificationProducer;

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    /**
	 * 
	 */
    public NotificationProducerService(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.notification.api.NotificationProducerService#getCurrentMessage
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage currentMessage) {
        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage message = null;
        try {
            message = asModel(currentMessage);
        } catch (WsnbException e1) {
            e1.printStackTrace();
        }

        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse response = null;
        try {
            response = this.notificationProducer.getCurrentMessage(message);
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AbsWSStarFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return asJAXB(response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.notification.api.NotificationProducerService#subscribe
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe)
     */
    public SubscribeResponse subscribe(Subscribe subscribe) throws InvalidTopicExpressionFault,
            org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault,
            InvalidProducerPropertiesExpressionFault, UnrecognizedPolicyRequestFault,
            TopicExpressionDialectUnknownFault, NotifyMessageNotSupportedFault, InvalidFilterFault,
            UnsupportedPolicyRequestFault, InvalidMessageContentExpressionFault,
            SubscribeCreationFailedFault, TopicNotSupportedFault,
            UnacceptableInitialTerminationTimeFault {

        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe message;
        try {
            message = asModel(subscribe);
            System.out.println("+++++++++++++++++++++++++++++");
            Document dom = asDOM(subscribe);
            System.out.println(XMLPrettyPrinter.prettyPrint(dom));
            System.out.println("+++++++++++++++++++++++++++++");
            System.out.println("+++++++++++++++++++++++++++++");
            dom = asDOM(message);
            System.out.println(XMLPrettyPrinter.prettyPrint(dom));
            System.out.println("+++++++++++++++++++++++++++++");
        } catch (WsnbException e1) {
            e1.printStackTrace();
            throw new SubscribeCreationFailedFault("Can not read subscribe", e1);
        }

        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse response = null;
        try {
            response = this.notificationProducer.subscribe(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTopicExpressionFault("Can not subscribe", e);
        }
        return asJAXB(response);
    }

    public com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe asModel(
            Subscribe subscribe) throws WsnbException {
        Document dom = asDOM(subscribe);
        return RefinedWsnbFactory.getInstance().getWsnbReader().readSubscribe(dom);
    }

    /**
     * @param currentMessage
     * @return
     * @throws WsnbException
     */
    private com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage asModel(
            GetCurrentMessage currentMessage) throws WsnbException {
        Document dom = asDOM(currentMessage);
        return RefinedWsnbFactory.getInstance().getWsnbReader().readGetCurrentMessage(dom);
    }

    /**
     * @param currentMessage
     * @return
     */
    private Document asDOM(GetCurrentMessage currentMessage) {
        Document doc = null;
        // TODO
        return doc;
    }

    /**
     * @param subscribe
     * @return
     * @throws JAXBException
     */
    public Document asDOM(Subscribe subscribe) {
        Document dom = null;
        // TODO
        return dom;
    }

    public static com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify asModel(
            Notify notify) throws WsnbException {
        Document dom = asDOM(notify);
        return RefinedWsnbFactory.getInstance().getWsnbReader().readNotify(dom);
    }

    public static com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify payload) {
        return NotifyImpl.toJaxbModel(payload);
    }

    public static Document asDOM(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify notify)
            throws WsnbException {
        return Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
    }

    public static Document asDOM(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse)
            throws WsnbException {
        return Wsnb4ServUtils.getWsnbWriter().writeSubscribeResponseAsDOM(subscribeResponse);
    }

    public static Subscribe asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe) {
        return SubscribeImpl.toJaxbModel(subscribe);
    }

    public static SubscribeResponse asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse) {
        return SubscribeResponseImpl.toJaxbModel(subscribeResponse);
    }

    public static GetCurrentMessageResponse asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse getCurrentMessageResponse) {
        return GetCurrentMessageResponseImpl.toJaxbModel(getCurrentMessageResponse);
    }

    public static Document asDOM(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe)
            throws WsnbException {
        return Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(subscribe);
    }

}
