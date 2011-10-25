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

import java.security.Principal;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.ow2.petals.binding.soap.Constants;
import org.ow2.petals.binding.soap.util.Marshaller;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.MEPType;
import org.ow2.petals.component.framework.message.ExchangeImpl;

import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_SERVER;

import com.ebmwebsourcing.wsstar.addressing.definition.api.WSAddressingException;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WSNotificationExtensionException;

/**
 * An abstract request processor. It is used by the external listener to handle
 * incoming requests such as SOAP, REST...
 * 
 * Created on 14 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public abstract class RequestProcessor {

    protected Logger logger;

    protected DeliveryChannel channel;

    protected AbstractComponent component;

    protected MessageExchangeFactory exchangeFactory;

    /**
     * 
     * @param component
     * @param channel
     * @param log
     */
    public RequestProcessor(final AbstractComponent component, final DeliveryChannel channel,
            final Logger log) {
        this.component = component;
        this.channel = channel;
        logger = log;
        exchangeFactory = channel.createExchangeFactory();
    }

    /**
     * Process the request
     * 
     * @param inContext
     * @param performanceNotification
     * @return the out message context
     * @throws AxisFault
     */
    protected abstract MessageContext process(final MessageContext inContext,
            final SOAPFactory soapfactory) throws AxisFault;

    /**
     * Process the JBI response. <br>
     * According to the response status :
     * <ul>
     * <li>
     * set outContext to null for DONE</li>
     * <li>
     * throws an AxisFault for ERROR</li>
     * <li>
     * set outContext body to a FAULT</li>
     * <li>
     * set outContext body to a response</li>
     * </ul>
     * 
     * @param exchange
     *            JBI response
     * 
     * @param factory
     *            soap factory used to create body response
     * @return a FAULT or OUT JBI response or null if no response
     * @throws AxisFault
     *             with ERROR JBI status, or problem while creating response
     */
    protected MessageContext processJBIResponse(final MessageExchange exchange,
            final MessageContext inMessage, final SOAPFactory factory) throws AxisFault {
        if (exchange.getStatus().equals(ExchangeStatus.DONE)) {
            // exchange DONE, nothing to do, no soap response
            logger.fine("Receive a Done status message");
            return null;

        } else if (exchange.getStatus().equals(ExchangeStatus.ERROR)) {
            // exchange ERROR: throws an axis fault
            logger.fine("Receive an Error status message");
            throw new AxisFault(FAULT_SERVER, exchange.getError());

        } else if (exchange.getStatus().equals(ExchangeStatus.ACTIVE)) {
            logger.fine("Receive an Active status message");
            final MessageContext outMessage = MessageContextBuilder.createOutMessageContext(inMessage);
            outMessage.getOperationContext().addMessageContext(outMessage);
            try {
                if (exchange.getFault() != null) {
                    handleFault(exchange, outMessage, factory);
                } else {
                    // Get the output message if the message is an instance of
                    // out
                    try {
                        NormalizedMessage nm = null;
                        if (exchange instanceof InOptionalOut || exchange instanceof InOut) {
                            nm = exchange.getMessage(Exchange.OUT_MESSAGE_NAME);
                        }
                        if (nm == null) {
                            final String errorMsg = "The MEP '" + exchange.getPattern()
                            + "' does not accept a null response";
                            logger.log(Level.SEVERE, errorMsg);
                            throw new AxisFault(errorMsg, FAULT_SERVER);
                        } else {
                            if (component.getNotificationBrokerController() != null
                                    && component.getNotificationBrokerController()
                                    .isBrokeredNotification(exchange)) {
                                nm = component.getNotificationBrokerController().getTransformer()
                                .transformResponseOnConsume(nm, exchange.getOperation());
                            }
                            final SOAPEnvelope envelope = Marshaller
                            .createSOAPEnvelope(factory, nm, false);
                            outMessage.setEnvelope(envelope);
                            handleResponseAttachments(factory, nm, outMessage);
                        }
                    } catch (final AxisFault e) {
                        final String errorMsg = "Error while creating SOAP response";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    } catch (final WSAddressingException e) {
                        final String errorMsg = "Error while creating an address of the notification";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    } catch (final PEtALSCDKException e) {
                        final String errorMsg = "Error while processing the notification";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    } catch (final MessagingException e) {
                        final String errorMsg = "Error while changing the notification";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    } catch (final WSNotificationException e) {
                        final String errorMsg = "Error while creating the notification";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    } catch (final WSNotificationExtensionException e) {
                        final String errorMsg = "Error while creating the notification for PEtALS";
                        logger.log(Level.SEVERE, errorMsg, e);
                        throw new AxisFault(errorMsg, FAULT_SERVER, e);
                    }
                }
            } finally {
                closeMessageExchange(exchange);
            }
            return outMessage;
        } else {
            throw new AxisFault(Constants.SOAP.ERROR_WRONG_MESSAGE_STATUS + " "
                    + exchange.getStatus().toString());
        }
    }

    /**
     * Create a message exchange matching a pattern specified in CDKExtensions.
     * If the MEP is not defined, it will be IN_OPTIONAL_OUT.
     * 
     * @param consumes
     *            The consumes
     * @return a message exchange matching the pattern given in extensions
     * @throws MessagingException
     *             if the message exchange can't be created
     */
    protected MessageExchange createMessageExchange(final Consumes consumes)
    throws MessagingException {
        MessageExchange exchange = null;
        final MEPType mep = consumes.getMep();
        if (mep != null) {
            if (MEPType.IN_ONLY.equals(mep)) {
                exchange = exchangeFactory.createInOnlyExchange();
            } else if (MEPType.ROBUST_IN_ONLY.equals(mep)) {
                exchange = exchangeFactory.createRobustInOnlyExchange();
            } else if (MEPType.IN_OUT.equals(mep)) {
                exchange = exchangeFactory.createInOutExchange();
            } else if (MEPType.IN_OPTIONAL_OUT.equals(mep)) {
                exchange = exchangeFactory.createInOptionalOutExchange();
            }
        } else {
            exchange = exchangeFactory.createInOptionalOutExchange();
        }
        return exchange;
    }

    /**
     * Create the JBI message exchange
     * 
     * @param inContext
     * @param operation
     * @param axisService
     * @param performanceNotification
     *            The performance notification whose the UID will be forwarded
     *            to the service provider.
     * @return
     * @throws AxisFault
     */
    protected MessageExchange createMessageExchange(final MessageContext inContext,
            final QName operation, final PetalsAxisService axisService) throws AxisFault {
        ExchangeImpl exchangeWrapper = null;
        Source source = null;
        ServiceEndpoint endpoint = null;
        try {
            // create the message exchange from the extensions.
            final MessageExchange exchange = createMessageExchange(axisService.getConsumes());
            source = Marshaller.createSource(inContext.getEnvelope(), SUPropertiesHelper
                    .retrieveRemoveRootElement(axisService.getConsumesCDKExtensions()));
            exchangeWrapper = new ExchangeImpl(exchange);
            exchangeWrapper.setOperation(operation);
            // Set the service name, endpoint name and interface name. The
            // Router will find the JBI endpoint to contact with these
            // informations
            exchangeWrapper.setInterfaceName(axisService.getConsumes().getInterfaceName());
            if (axisService.getConsumes().getServiceName() != null) {
                exchangeWrapper.setService(axisService.getConsumes().getServiceName());
                if (axisService.getConsumes().getEndpointName() != null) {
                    endpoint = component.getContext().getEndpoint(
                            axisService.getConsumes().getServiceName(),
                            axisService.getConsumes().getEndpointName());
                }
                exchangeWrapper.setEndpoint(endpoint);
            }
            exchangeWrapper.setInMessageContent(source);
            if (component.getNotificationBrokerController() != null
                    && component.getNotificationBrokerController().isBrokeredNotification(exchange)) {
                exchangeWrapper.setInMessage(component.getNotificationBrokerController()
                        .getTransformer().transformRequestOnConsume(exchangeWrapper.getInMessage(),
                                operation));
            }
            // add SOAP attachments to normalized message
            Marshaller.setAttachments(inContext.getAttachmentMap(), exchangeWrapper.getInMessage());

            // retrieve security subject if rampart module is engaged
            if (isSecuredService(axisService)) {
                final Subject subject = createSecuritySubject(inContext);
                if (subject != null) {
                    exchangeWrapper.getInMessage().setSecuritySubject(subject);
                }
            }

            // Get the options from the SOAP message and put them into the JBI
            // message
            Marshaller.setProperties(inContext, exchangeWrapper.getInMessage());

        } catch (final Exception e) {
            throw AxisFault.makeFault(e);
        }

        return exchangeWrapper.getMessageExchange();
    }

    /**
     * Check is the service is secured ie rampart module has been engaged by the
     * service unit.
     * 
     * @param service
     * @return
     */
    protected boolean isSecuredService(final PetalsAxisService service) {
        final ConfigurationExtensions extensions = service.getConsumesCDKExtensions();
        final String modules = extensions.get(Constants.ServiceUnit.MODULES);
        return modules != null && modules.indexOf(Constants.Axis2.RAMPART_MODULE) >= 0;
    }

    /**
     * Create the security {@link Subject} from the {@link MessageContext}. TODO
     * : Check if there are security features in REST too.
     * 
     * @param messageContext
     * @return the security subject created from the message context if we have
     *         found {@link Principal} in the message context else return null;
     */
    @SuppressWarnings("unchecked")
    protected Subject createSecuritySubject(final MessageContext messageContext) {
        logger.fine("Trying to create JAAS Security Subject");

        Subject subject = null;
        final Vector wssec = (Vector) messageContext.getProperty(WSHandlerConstants.RECV_RESULTS);
        if (wssec != null) {
            for (final Iterator iterator = wssec.iterator(); iterator.hasNext();) {
                final WSHandlerResult wshandler = (WSHandlerResult) iterator.next();
                final Vector wsSecEngineResults = wshandler.getResults();

                for (final Object object : wsSecEngineResults) {
                    final WSSecurityEngineResult wsresult = (WSSecurityEngineResult) object;

                    final Principal principal = (Principal) wsresult
                    .get(WSSecurityEngineResult.TAG_PRINCIPAL);
                    if (principal != null) {
                        if (subject == null) {
                            subject = new Subject();
                        }

                        logger.fine("Got a principal from input message context : "
                                + principal.getName());
                        subject.getPrincipals().add(principal);
                    }
                }
            }
        }
        return subject;
    }

    /**
     * Get the operation from the message context. Assume that the operation is
     * always the root element of the SOAP body.
     * 
     * @param message
     * @return null if the operation has not been found
     */
    protected QName retrieveOperationFromSOAPBody(final MessageContext message) {
        QName result = null;
        final SOAPEnvelope env = message.getEnvelope();
        final SOAPBody body = env.getBody();
        final OMElement operation = body.getFirstElement();
        if (operation != null) {
            result = operation.getQName();
        }
        return result;
    }

    /**
     * @param exchange
     * @param outMessage
     * @param factory
     * @throws AxisFault
     */
    protected void handleFault(final MessageExchange exchange, final MessageContext outMessage,
            final SOAPFactory factory) throws AxisFault {
        // exchange FAULT : return a fault message
        if (logger.isLoggable(Level.FINE)) {
            logger.warning("Got a fault on JBI response");
        }
        try {
            final SOAPEnvelope envelope = Marshaller.createSOAPEnvelope(factory, exchange
                    .getFault(),true);
            
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "SOAP FAULT FROM JBI response : " + envelope.toString());
            }
            
            outMessage.setEnvelope(envelope);
        } catch (final AxisFault e) {
            final String errorMsg = "Error while creating SOAP Fault response.";
            logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }
    }

    /**
     * Put the {@link NormalizedMessage} attachments in the output Axis2
     * {@link MessageContext}.
     * 
     * @param messageContext
     */
    protected void handleResponseAttachments(final SOAPFactory soapFactory,
            final NormalizedMessage nm, final MessageContext outMessage) {
        // put the JBI attachments in the output message
        Marshaller.copyAttachments(nm, outMessage);

        // process the output message with its attachments
        Marshaller.fillSOAPBodyWithAttachments(soapFactory, outMessage);

        final SOAPEnvelope env = outMessage.getEnvelope();
        if (env != null && logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "SOAPENVELOPE AFTER Attachment handling");
            logger.log(Level.FINE, outMessage.getEnvelope().toString());
        }
    }

    /**
     * @param exchange
     * @throws AxisFault
     */
    protected void closeMessageExchange(final MessageExchange exchange) throws AxisFault {
        // close the messageExchange with the JBI NMR
        try {
            exchange.setStatus(ExchangeStatus.DONE);
            channel.send(exchange);
        } catch (final MessagingException e) {
            final String errorMsg = "Error while closing JBI MessageExchange.";
            logger.log(Level.SEVERE, errorMsg, e);
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }
    }

    /**
     * Get the processor name. This name MUST be unique ie two processors can
     * not have the same name !
     * 
     * @return
     */
    protected abstract String getProcessorName();
}
