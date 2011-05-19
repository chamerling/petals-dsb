/**
 * 
 */
package org.petalslink.dsb.wsn.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.petalslink.dsb.wsn.api.NotificationProducerService;
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
public class NotificationProducerServiceService implements NotificationProducerService {

    private org.petalslink.dsb.wsn.impl.NotificationProducerService notificationProducerService;
    private WsnbJAXBContext wsnbJaxbContext;

    /**
     * 
     * @param notificationProducerService
     */
    public NotificationProducerServiceService(
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
    public SubscribeResponse subscribe(Subscribe request) {
        System.out.println("Got a subsribe request : ");
        
        System.out.println("ON THE SERVICE JUST RECEIVED : ");
        FilterType filter = request.getFilter();
        System.out.println("FILTER : " + filter);
        java.util.List<Object> anyFromModel = filter.getAny();
        
        for (final Object item : anyFromModel) {
            System.out.println("ITEM : " + item);
            if (item instanceof JAXBElement<?>) {
                JAXBElement<?> elemnt = (JAXBElement<?>) item;
                System.out.println("NAME = " + elemnt.getName());
            }
        }
        
        try {
            Marshaller marshaller = this.wsnbJaxbContext.createWSNotificationMarshaller();
            Document result = WsstarCommonUtils.getNamespaceDocumentBuilder().newDocument(); 
            final JAXBElement<com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe> element = 
                new JAXBElement<com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe>(WsnbConstants.SUBSCRIBE_QNAME,
                       com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe.class, request);     
              marshaller.marshal(element, result);
              System.out.println(WsstarCommonUtils.prettyPrint(result));
        } catch (JAXBException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //System.out.println(WsstarCommonUtils.prettyPrint(doc));
        
        // transform the JAXB data into local model
        SubscribeResponse response = null;

        // for now just create a DOM document from the JAXB model, and create
        // the model from it...
        try {
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribeRequest = Adapters
                    .asModel(request);
            
            System.out.println("ON THE SERVICE AS MODEL : ");
            System.out.println(WsstarCommonUtils.prettyPrint(Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(
                    subscribeRequest)));

            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse = this.notificationProducerService
                    .subscribe(subscribeRequest);
            response = createJAXBFrom(subscribeResponse);

        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AbsWSStarFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    private SubscribeResponse createJAXBFrom(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse) {
        System.out.println("TODO");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.wsn.api.NotificationProducerService#getCurrentMessage
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage request) {
        // TODO Auto-generated method stub
        return null;
    }

}
