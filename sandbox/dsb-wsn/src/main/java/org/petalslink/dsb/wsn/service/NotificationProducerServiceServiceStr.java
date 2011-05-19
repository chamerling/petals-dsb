/**
 * 
 */
package org.petalslink.dsb.wsn.service;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.petalslink.dsb.wsn.api.NotificationProducerServiceStr;
import org.petalslink.dsb.wsn.utils.Adapters;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.WsnbJAXBContext;
import com.ebmwebsourcing.wsstar.common.utils.WsstarCommonUtils;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.FilterType;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class NotificationProducerServiceServiceStr implements NotificationProducerServiceStr {

    private org.petalslink.dsb.wsn.impl.NotificationProducerService notificationProducerService;

    private WsnbJAXBContext wsnbJaxbContext;

    /**
     * 
     * @param notificationProducerService
     */
    public NotificationProducerServiceServiceStr(
            org.petalslink.dsb.wsn.impl.NotificationProducerService notificationProducerService) {
        this.notificationProducerService = notificationProducerService;
        this.wsnbJaxbContext = WsnbJAXBContext.getInstance();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.wsn.api.NotificationProducerService#subscribe(com.
     * ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe)
     */
    public String subscribe(String request) {
        System.out.println("GOT SUBSCRIBE ON SERVER = " + request);
        String response = null;

        // for now just create a DOM document from the JAXB model, and create
        // the model from it...
        try {
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribeRequest = Wsnb4ServUtils
                    .getWsnbReader().readSubscribe(
                            Adapters.fromStreamToDocument(new ByteArrayInputStream(request
                                    .getBytes())));
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse = this.notificationProducerService
                    .subscribe(subscribeRequest);
            Document doc = Wsnb4ServUtils.getWsnbWriter().writeSubscribeResponseAsDOM(
                    subscribeResponse);
            response = WsstarCommonUtils.prettyPrint(doc);

        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AbsWSStarFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.wsn.api.NotificationProducerService#getCurrentMessage
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage)
     */
    public String getCurrentMessage(String request) {
        // TODO Auto-generated method stub
        return null;
    }

}
