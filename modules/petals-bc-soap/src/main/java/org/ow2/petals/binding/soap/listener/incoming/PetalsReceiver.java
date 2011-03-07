/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
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
 * $Id: Axis2BCListener.java 154 19 avr. 2006 wjoseph $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.ow2.petals.component.framework.AbstractComponent;

import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_CLIENT;

/**
 * The message receiver used by the binding component. The SOAP message is
 * forwarded to the JBI endpoint.
 * 
 * @version $Id$
 * @since Petals 1.0
 * @author alouis,wjoseph,chamerling - EBM Websourcing
 * 
 */
public class PetalsReceiver extends AbstractMessageReceiver {

    protected Map<String, RequestProcessor> requestProcessors;

    protected AbstractComponent component;

    protected DeliveryChannel channel;

    protected Logger logger;

    /**
     * Creates a new instance of PetalsReceiver
     * 
     * @param context
     * @param channel
     * @param bindingSUM
     * @param log
     */
    public PetalsReceiver(final AbstractComponent component, final DeliveryChannel channel,
            final Logger logger) {
        super();
        this.logger = logger;
        this.component = component;
        this.channel = channel;
        this.requestProcessors = new HashMap<String, RequestProcessor>();

        this.initRequestProcessors();
    }

    /**
     * Init the request processors. TODO : Add the processors from component
     * configuration file.
     * 
     */
    protected void initRequestProcessors() {
        this.addProcessor(new RESTProcessor(this.component, this.channel, this.logger));
        this.addProcessor(new SOAPProcessor(this.component, this.channel, this.logger));
    }

    /**
     * Add a processor to the processors list
     * 
     * @param processor
     */
    protected void addProcessor(final RequestProcessor processor) {
        this.requestProcessors.put(processor.getProcessorName(), processor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.engine.MessageReceiver#invokeBusinessLogic(org.apache
     * .axis2.context.MessageContext)
     */
    @Override
    public final void invokeBusinessLogic(final MessageContext msgContext) throws AxisFault {
        this.logger.log(Level.FINE, "Receiving a message from Axis2 engine");

        // FIX NPE (Axis2 bug)
        final Parameter param = new Parameter(
                org.apache.axis2.Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS,
                Boolean.TRUE);
        if (msgContext.getAxisOperation() != null) {
            msgContext.getAxisOperation().addParameter(param);
        } else if (msgContext.getAxisService() != null) {
            msgContext.getAxisService().addParameter(param);
        } else if (msgContext.getAxisServiceGroup() != null) {
            msgContext.getAxisServiceGroup().addParameter(param);
        }
        // send message to JBI endpoint
        MessageContext outMsgContext = process(msgContext);
        if (outMsgContext != null) {
            // send back the response to the WS consumer
            this.sendBackResponse(outMsgContext);
        }
    }

    /**
     * Send the response to the WS consumer.
     * 
     * @param outMsgContext
     * @throws AxisFault
     */
    protected void sendBackResponse(final MessageContext outMsgContext) throws AxisFault {
        AxisEngine.send(outMsgContext);
    }

    /**
     * Sends the SOAP Envelop of the request message directly to a JBI service
     * and operation on the JBI container. The response message from the JBI
     * service invocation is transmitted as an outgoing message.
     * 
     * @param inMessage
     *            MessageContext for incoming message.
     * @param performanceNotification
     *            The performance notification whose the UID will be forwarded
     *            to the service provider.
     * @return The out message context
     * @throws AxisFault
     *             error while processing request, send the JBI Exchange,
     *             processing the JBI response, or if the JBI response is a
     *             Fault.
     * @return The out message context
     */
    protected MessageContext process(final MessageContext inContext) throws AxisFault {

        if (!(inContext.getAxisService() instanceof PetalsAxisService)) {
            throw new AxisFault("Can not retrieve a valid PEtALS Axis service from context",
                    FAULT_CLIENT);
        }

        final RequestProcessor processor = this.getProcessor(inContext);
        if (processor == null) {
            throw new AxisFault("Can not process the incoming request, "
                    + "message processor has not been found for this type of request");
        }

        return processor.process(inContext, this.getSOAPFactory(inContext));
    }

    /**
     * Get the processor for the given message context.
     * 
     * @param context
     * @return the processor of the SOAP one which is the default one if no
     *         valid processor has been found.
     */
    protected RequestProcessor getProcessor(final MessageContext context) {
        RequestProcessor result = null;
        if (context.isDoingREST()) {
            result = this.getRESTProcessor();
        } else {
            result = this.getSOAPProcessor();
        }
        return result;
    }

    /**
     * Get the REST processor
     * 
     * @return
     */
    protected RequestProcessor getRESTProcessor() {
        return this.requestProcessors.get("REST");
    }

    /**
     * Get the SOAP processor
     * 
     * @return
     */
    protected RequestProcessor getSOAPProcessor() {
        return this.requestProcessors.get("SOAP");
    }

}
