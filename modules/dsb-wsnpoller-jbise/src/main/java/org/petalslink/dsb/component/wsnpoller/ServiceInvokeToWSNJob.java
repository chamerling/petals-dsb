/**
 * 
 */
package org.petalslink.dsb.component.wsnpoller;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.notification.commons.NotificationConstants;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.service.poller.api.Job;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingTransport;
import org.petalslink.dsb.service.poller.api.ServiceInformation;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
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

        if (result != null && context.getResponseTo() != null) {

            if (logger.isDebugEnabled()) {
                logger.debug("Sending response to " + context.getResponseTo());
            }

            // let's send the response to the defined service. We wrap the
            // response into a WSNotification Notify message

            // FIXME
            // the service we originally polled...
            String producerAddress = "dsb://wsnpoller";

            String topicLocalPart = context.getExtensions().get("topicName");
            String topicURI = context.getExtensions().get("topicURI");
            String topicPrefix = context.getExtensions().get("topicPrefix");

            QName finalTopic = new QName(topicURI, topicLocalPart, topicPrefix);
            try {
                Notify n = NotificationHelper.createNotification(producerAddress, null, null,
                        finalTopic, NotificationConstants.DIALECT_CONCRETE, result);
                Document doc = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(n);

                if (logger.isDebugEnabled()) {
                    logger.debug("Going to send : ");
                    try {
                        logger.debug(com.ebmwebsourcing.easycommons.xml.XMLHelper
                                .createStringFromDOMDocument(doc));
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }

                transport.send(doc, context.getResponseTo());

            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    e.printStackTrace();
                }
            }

        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Message not forwarded...");
            }

            if (result == null) {
                logger.info("No response from the polled service");
            }

            if (context.getResponseTo() == null) {
                logger.info("No service defined to send the response to");
            }
        }
    }
}
