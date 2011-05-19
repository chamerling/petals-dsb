/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.net.MalformedURLException;

import org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.common.utils.WsstarCommonUtils;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsnb.services.transport.ITransporterForWsnbPublisher;

/**
 * This is a CXF version of the {@link ITransporterForWsnbPublisher}
 * 
 * @author chamerling
 * 
 */
public class WebServiceNotificationTransporterStr implements ITransporterForWsnbPublisher {

    public WebServiceNotificationTransporterStr() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.transport.
     * ITransporterForWsnbPublisher
     * #sendNotifyRequest(com.ebmwebsourcing.wsstar.addressing
     * .datatypes.api.abstraction.EndpointReferenceType,
     * com.ebmwebsourcing.wsstar
     * .basenotification.datatypes.api.abstraction.Notify)
     */
    public void sendNotifyRequest(EndpointReferenceType consumerServiceEdp, Notify payload) {
        System.out.println("Sending notify!");
        try {
            String address = consumerServiceEdp.getAddress().toURL().toString();
            NotificationConsumerServiceStr client = CXFClientFactory.getClientSTr(address);
            Document doc = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(payload);
            client.notify(WsstarCommonUtils.prettyPrint(doc));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
