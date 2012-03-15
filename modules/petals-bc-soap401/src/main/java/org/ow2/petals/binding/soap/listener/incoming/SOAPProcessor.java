/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.WsdlHelper;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.util.StringHelper;
import org.w3c.dom.Element;

import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_CLIENT;
import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_SERVER;


/**
 * Processor to handle incoming SOAP messages.
 * 
 * Created on 14 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class SOAPProcessor extends RequestProcessor {

    /**
     * 
     * @param component
     * @param channel
     * @param log
     */
    public SOAPProcessor(final AbstractComponent component, final DeliveryChannel channel,
            final Logger log) {
        super(component, channel, log);
    }

    
    /* (non-Javadoc)
     * @see org.ow2.petals.binding.soap.listener.incoming.RequestProcessor#process(org.apache.axis2.context.MessageContext, org.apache.axiom.soap.SOAPFactory)
     */
    protected MessageContext process(final MessageContext inContext,
            final SOAPFactory factory) throws AxisFault {
        this.logger.log(Level.FINE, "Processing the incoming SOAP message");

        // Type of service has been checked before
        final PetalsAxisService axisService = (PetalsAxisService) inContext.getAxisService();

        // Retrieve message operation and service
        String soapAction = inContext.getWSAAction();
        
        this.logger.log(Level.FINEST, "soapAction found in the incoming message : " +soapAction);
        
        QName jbiOperation;        
        jbiOperation = retrieveSOAPOperation(inContext, soapAction, axisService);
        
        if (jbiOperation == null) {
            throw new AxisFault("Operation not found in message context", FAULT_CLIENT);
        }
        
        // get timeout
        final long timeout = SUPropertiesHelper.retrieveTimeout(axisService
                .getConsumesCDKExtensions());

        // create the message exchange
        MessageExchange exchange = null;
        try {
            exchange = createMessageExchange(inContext, jbiOperation, axisService);
        } catch (final AxisFault e) {
            final String errorMsg = "Error while transforming SOAP request to JBI MessageExchange";
            this.logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }
        
        //Set the soap action jbi property.
        exchange.setProperty("soap-action", soapAction);

        // send the message exchange through JBI
        // As the SOAP/HTTP request may wait for a response,
        // use the sendSynchrone MEP
        boolean sent = false;
        try {

            if (timeout == -1L) {
                sent = this.channel.sendSync(exchange);
            } else {
                sent = this.channel.sendSync(exchange, timeout);
            }

        } catch (final MessagingException e) {
            final String errorMsg = "Error while sending message through JBI NMR.";
            this.logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }

        if (!sent) {
            throw new AxisFault("A timeout occurs calling the consumed service.", FAULT_SERVER);
        }
        // process the response received from JBI NMR. The out context is
        // returned
        return processJBIResponse(exchange, inContext, factory);
    }

    /**
     * Return the operation according to the WSAAction SOAP attribute. If not
     * found in the WSA, get it from the SOAP body and the WSDL description (depending
     * if the style of the WSDL is rpc or document).
     * 
     * @param inContext
     *            SOAPrequest context
     * @return operation as QName, null if no WSAAction is specified
     */
    protected QName retrieveSOAPOperation(final MessageContext inContext, String soapAction, PetalsAxisService axisService) {
        QName operationName = null;

        try {
            Description desc = axisService.getDescription();

            BindingOperation res = null;
            if (!StringHelper.isNullOrEmpty(soapAction)){ 
                res = WsdlHelper.findOperationUsingSoapAction(soapAction, desc);
            }

            if(res == null) {
                Element firstElement = XMLUtils.toDOM(inContext.getEnvelope().getBody().getFirstElement());
                String endpointName = axisService.getConsumes().getEndpointName();
                QName serviceName = axisService.getConsumes().getServiceName();
                res = WsdlHelper.findOperationUsingElement(firstElement, desc, endpointName, serviceName);
            }
            operationName= res.getOperation().getQName();

            if (this.logger.isLoggable(Level.FINEST)){
                this.logger.log(Level.FINEST, "jbiOperation retrieved : {" + operationName.getNamespaceURI()+"}"+operationName.getLocalPart());
            }
        } catch (Exception e1) {
            //To be removed when the new mechanism (findOperation() upstairs) has been improved!!!:
            if (!StringHelper.isNullOrEmpty(soapAction)) {

                String namespace = soapAction.substring(0,
                        inContext.getWSAAction().lastIndexOf('/')+1);
                String opname = inContext.getWSAAction().substring(inContext.getWSAAction().lastIndexOf('/') + 1);
                final QName operation = new QName(namespace, opname);
                operationName = operation;
            } else {
                operationName = this.retrieveOperationFromSOAPBody(inContext);
            }            

            if (this.logger.isLoggable(Level.FINEST)){
                this.logger.log(Level.FINEST, "jbiOperation retrieved using old mechanism : {" + operationName.getNamespaceURI()+"}"+operationName.getLocalPart());
            }
        }
        return operationName;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.binding.soap.listener.incoming.RequestProcessor#
     * getProcessorName()
     */
    @Override
    protected String getProcessorName() {
        return "SOAP";
    }
}
