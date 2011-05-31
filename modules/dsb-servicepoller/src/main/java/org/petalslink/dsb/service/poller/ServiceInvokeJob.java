/**
 * 
 */
package org.petalslink.dsb.service.poller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.service.poller.api.Job;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingTransport;
import org.petalslink.dsb.service.poller.api.ServiceInformation;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ServiceInvokeJob implements Job {

    final Log logger = LogFactory.getLog(ServiceInvokeJob.class);

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

        // get the transport
        PollingTransport transport = context.getTransport();
        if (transport == null) {
            throw new PollerException("Can not find a message transport in the context");
        }

        Document result = transport.send(context.getInputMessage(), serviceInformation);

        if (logger.isDebugEnabled()) {
            logger.debug("Got a response from the polled service : (TODO) " + result);
        }

        if (result != null && context.getResponseTo() != null) {
            // let's send the response to the defined service
            transport.send(result, context.getResponseTo());
            // ignore response...
        } else {
            if (result == null) {
                logger.info("No response from the polled service");
            }

            if (context.getResponseTo() == null) {
                logger.info("No service defined to send the response to");
            }
        }
    }
}
