/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.net.MalformedURLException;

import javax.xml.bind.JAXBException;

import org.petalslink.dsb.wsn.api.NotificationConsumerService;
import org.petalslink.dsb.wsn.utils.Adapters;

import com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.transport.ITransporterForWsnbPublisher;

/**
 * This is a CXF version of the {@link ITransporterForWsnbPublisher}
 * 
 * @author chamerling
 * 
 */
public class WebServiceNotificationTransporter implements ITransporterForWsnbPublisher {

    public WebServiceNotificationTransporter() {
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
            NotificationConsumerService client = CXFClientFactory.getClient(address);
            client.notify(toJAXB(payload));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify toJAXB(Notify payload)
            throws WsnbException, JAXBException {
        return Adapters.asJAXB(payload);
    }

}
