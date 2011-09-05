/**
 * 
 */
package org.petalslink.dsb.component.wsnpoller;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.notification.commons.NotificationConstants;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.service.poller.api.Job;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingTransport;
import org.petalslink.dsb.service.poller.api.ServiceInformation;
import org.petalslink.dsb.xmlutils.XMLHelper;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * Poll a service and wrap its response into a WSN one...
 * 
 * @author chamerling
 * 
 */
public class ServiceInvokeToWSNJob implements Job {

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    final Log logger = LogFactory.getLog(ServiceInvokeToWSNJob.class);

    public void invoke(PollingContext context) throws PollerException {
        if (logger.isDebugEnabled()) {
            logger.debug("In invoke Job");
        }

        ServiceInformation serviceInformation = context.getToPoll();
        if (serviceInformation == null) {
            throw new PollerException("Can not find the service to poll from context");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Invoking the remote Service located at : " + serviceInformation);
        }

        PollingTransport transport = context.getTransport();
        if (transport == null) {
            throw new PollerException("Can not find a message transport in the context");
        }

        Document result = transport.send(context.getInputMessage(), serviceInformation);

        if (logger.isDebugEnabled()) {
            logger.debug("Got a response from the polled service : (TODO) " + result);
        }

        System.out.println("Forward response...");

        if (result != null && context.getResponseTo() != null
                && context.getExtensions().get("topic") != null) {

            System.out.println("Sending response to " + context.getResponseTo());

            // let's send the response to the defined service. We wrap the
            // response into a WSNotification Notify message

            // FIXME
            // the service we originally polled...
            String producerAddress = "dsb://wsnpoller";

            // defined by configuration
            System.out.println(context.getExtensions());
            System.out.println(context.getExtensions().get("topic"));
            QName topic = QName.valueOf(context.getExtensions().get("topic"));
            QName finalTopic = new QName(topic.getNamespaceURI(), topic.getLocalPart(), "dsb");
            try {
                Notify n = NotificationHelper.createNotification(producerAddress, null, null,
                        finalTopic, NotificationConstants.DIALECT_CONCRETE, result);
                Document doc = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(n);

                try {
                    System.out.println(com.ebmwebsourcing.easycommons.xml.XMLHelper
                            .createStringFromDOMDocument(doc));
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

                transport.send(doc, context.getResponseTo());

            } catch (NotificationException e) {
                e.printStackTrace();
            } catch (WsnbException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Not forwarded...");
            if (result == null) {
                System.out.println("Not forwarded#..");

                logger.info("No response from the polled service");
            }

            if (context.getResponseTo() == null) {
                System.out.println("Not forwarded##..");

                logger.info("No service defined to send the response to");
            }
        }
    }

}
