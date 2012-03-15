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

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_EXTENSIONS_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.SOAP_EXTERNAL_LISTENER_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.SOAP.FAULT_SERVER;

import java.util.logging.Logger;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.FlowAttributes;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.util.MessageContextBuilder;
import org.ow2.petals.binding.soap.SoapConstants;
import org.ow2.petals.binding.soap.SoapConsumeFlowStepBeginLogData;
import org.ow2.petals.binding.soap.util.Marshaller;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.commons.PetalsExecutionContext;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.util.LoggingUtil;

import com.ebmwebsourcing.easycommons.lang.StringHelper;
import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.logger.Level;

/**
 * The message receiver used by the binding component. The SOAP message is
 * forwarded to the JBI endpoint.
 * 
 */
public class PetalsReceiver extends AbstractMessageReceiver {

    private final Logger logger;

    public PetalsReceiver(Logger logger) {
        super();
        this.logger = logger;
    }

    private final String retrieveRequestUrl(MessageContext msgContext) {
        String serviceName = msgContext.getServiceContext().getName();
        assert serviceName != null;
        String operationName = msgContext.getOperationContext().getOperationName();
        assert operationName != null;
        String transportName = msgContext.getIncomingTransportName();
        assert transportName != null;
        EndpointReference epr;
        try {
            epr = msgContext.getConfigurationContext().getListenerManager()
                    .getEPRforService(serviceName, operationName, transportName);
            assert epr != null;
            return epr.getAddress();
        } catch (AxisFault e) {
            throw new UncheckedException(e);
        }
    }

    @Override
    public final void invokeBusinessLogic(final MessageContext msgContext) throws AxisFault {
    	try {
	        // FIX NPE (Axis2 bug) TODO : is it really/still useful ?
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
    	catch(AxisFault af) {
			LoggingUtil.addMonitFailureTrace(this.logger,
					PetalsExecutionContext.getFlowAttributes(),
					af.getMessage(), Role.CONSUMER);
    		throw af;
    	}
    }

    /**
     * Send the response to the WS consumer.
     * 
     * @param outMsgContext
     * @throws AxisFault
     */
    private final void sendBackResponse(final MessageContext outMsgContext) throws AxisFault {
        /*String soapEnvelopeFilePath = DumpFileGenerator.getUniqueDumpFile().getAbsolutePath();
        ExecutionContext.getProperties().setProperty(Constants.DUMP_FILE_PATH_PROPERTY_NAME,
                soapEnvelopeFilePath);*/

        AxisEngine.send(outMsgContext);
    }

    /**
     * Process the request.
     * Sends the SOAP Envelop of the request message directly to a JBI service
     * and operation on the JBI container. The response message from the JBI
     * service invocation is transmitted as an outgoing message. 
     * 
     * @param the in message context
     * @return the out message context
     * @throws AxisFault
     *             the axis fault
     */
    private MessageContext process(final MessageContext messageContext)
            throws AxisFault {        
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "Processing the incoming SOAP message");
        }

        // Type of service has been checked before
        AxisService axisService = messageContext.getAxisService();

		AxisOperation axisOperation = messageContext.getOperationContext()
				.getAxisOperation();
		if (this.logger.isLoggable(Level.FINEST)) {
			this.logger.log(Level.FINEST,
					"soapAction found in the incoming message : "
							+ axisOperation.getSoapAction());
		}

		// JBI operation = Axis operation name
		QName jbiOperation = axisOperation.getName();

		// JBI mep = Axis operation mep
		int axisMep = axisOperation.getAxisSpecificMEPConstant();
		MEPConstants mep = null;
		if (axisMep == org.apache.axis2.wsdl.WSDLConstants.MEP_CONSTANT_IN_ONLY) {
			mep = MEPConstants.IN_ONLY_PATTERN;
		} else if (axisMep == org.apache.axis2.wsdl.WSDLConstants.MEP_CONSTANT_IN_OUT) {
			mep = MEPConstants.IN_OUT_PATTERN;
		}
		
        Parameter soapExternalListenerParam = axisService
                .getParameter(SOAP_EXTERNAL_LISTENER_SERVICE_PARAM);
        SoapExternalListener soapExternalListener = (SoapExternalListener) soapExternalListenerParam
                .getValue();
        Parameter consumesParam = axisService.getParameter(CONSUMES_SERVICE_PARAM);
        Consumes consumes = (Consumes) consumesParam.getValue();
        Parameter consumesExtensionsParam = axisService
				.getParameter(CONSUMES_EXTENSIONS_SERVICE_PARAM);
		ConfigurationExtensions consumesExtensions = (ConfigurationExtensions) consumesExtensionsParam
				.getValue();

		
		String requestUrl = retrieveRequestUrl(messageContext);
		FlowAttributes flowAttributes = PetalsExecutionContext.getFlowAttributes();
		String flowInstanceId = flowAttributes.getFlowInstanceId();
		String flowStepId = flowAttributes.getFlowStepId();
		logger.log(
				Level.MONIT,
				"",
				new SoapConsumeFlowStepBeginLogData(flowInstanceId, flowStepId,
						StringHelper.nonNullValue(consumes.getInterfaceName()),
						StringHelper.nonNullValue(consumes.getServiceName()),
						consumes.getEndpointName(), StringHelper
								.nonNullValue(jbiOperation), requestUrl));

		try {
			// create the message exchange
			Exchange exchange = createMessageExchange(messageContext,
					jbiOperation, mep, soapExternalListener, consumes,
					consumesExtensions);

			// send the message exchange through JBI
			// As the SOAP/HTTP request may wait for a response,
			SOAPFactory soapFactory = this.getSOAPFactory(messageContext);
			MessageContext responseMessageContext = sendJBIMessage(
					messageContext, soapFactory, exchange, soapExternalListener);

			return responseMessageContext;
		} catch (final MessagingException me) {
			final String errorMsg = "Error while transforming SOAP request to JBI MessageExchange";
			if (this.logger.isLoggable(Level.SEVERE)) {
				this.logger.log(Level.SEVERE, errorMsg, me);
			}
			throw new AxisFault(errorMsg, FAULT_SERVER, me);
		} catch (PEtALSCDKException pcdke) {
			final String errorMsg = "Error while transforming SOAP request to JBI MessageExchange";
			if (this.logger.isLoggable(Level.SEVERE)) {
				this.logger.log(Level.SEVERE, errorMsg, pcdke);
			}
			throw new AxisFault(errorMsg, FAULT_SERVER, pcdke);
		}
    }

    /**
     * Create the JBI message exchange
     * 
     * @param inContext
     *            the SOAP message
     * @param operation
     *            the operation
     * @param mep
     *            the MEP
     * @param soapExternalListener
     *            the SOAP external listener
     * @param consumes
     *            the consumes
     * @param consumesExtensions
     *            the consume extensions
     * 
     * @return the JBI message exchange
     * 
     * @throws MessagingException
     * @throws PEtALSCDKException
     */
    private final Exchange createMessageExchange(final MessageContext inContext,
            final QName operation, final MEPConstants mep,
            final SoapExternalListener soapExternalListener, final Consumes consumes,
            final ConfigurationExtensions consumesExtensions) throws MessagingException,
            PEtALSCDKException {
        final Exchange msgExchange;

        // create the message exchange from the extensions.
        if (mep == null) {
            msgExchange = soapExternalListener.createConsumeExchange(consumes);
        } else {
            msgExchange = soapExternalListener.createConsumeExchange(consumes, mep);
        }
        msgExchange.setOperation(operation);

        boolean axis1Compatibility = SUPropertiesHelper
                .isAxis1CompatibilityEnabled(consumesExtensions);

        Source source;
        if (inContext.getAttachmentMap() == null
                || inContext.getAttachmentMap().getContentIDSet() != null
                && inContext.getAttachmentMap().getContentIDSet().size() <= 0) {
            source = Marshaller.createSourceContent(inContext.getEnvelope(), axis1Compatibility);
        } else {
            // That's avoid to put the attachment in the payload as binary
            // node
            source = Marshaller.createSourceContentAndAttachment(inContext);
        }
        msgExchange.setInMessageContent(source);

        // add SOAP attachments to normalized message
        Marshaller.setAttachments(inContext.getAttachmentMap(), msgExchange.getInMessage());

        // Get the options from the SOAP message and put them into the JBI
        // message
        Marshaller.setProperties(inContext, msgExchange.getInMessage());

        return msgExchange;
    }

    /**
     * Put the {@link NormalizedMessage} attachments in the output Axis2
     * {@link MessageContext}.
     * 
     * @param soapFactory
     *            the SOAP factory
     * @param nm
     *            the normalized message
     * @param outMessage
     *            the SOAP message
     * @throws AxisFault
     */
    private final void handleResponseAttachments(final SOAPFactory soapFactory,
            final NormalizedMessage nm, final MessageContext outMessage) throws AxisFault {
        // process the output message with its attachments
        Marshaller.fillSOAPBodyWithAttachments(nm, soapFactory, outMessage);

        final SOAPEnvelope env = outMessage.getEnvelope();
        if (env != null && this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "SOAPENVELOPE AFTER Attachment handling");
            this.logger.log(Level.FINE, outMessage.getEnvelope().toString());
        }
    }

    /**
     * Process the JBI response. <br>
     * According to the response status :
     * <ul>
     * <li>set outContext to null for DONE</li>
     * <li>throws an AxisFault for ERROR</li>
     * <li>set outContext body to a FAULT</li>
     * <li>set outContext body to a response</li>
     * </ul>
     * 
     * @param exchange
     *            JBI response
     * @param inMessage
     *            the SOAP message
     * @param factory
     *            soap factory used to create body response
     * @param soapExternalListener
     *            the SOAP external listener
     * @return a FAULT or OUT JBI response or null if no response
     * @throws AxisFault
     *             with ERROR JBI status, or problem while creating response
     */
    private MessageContext processJBIResponse(final Exchange exchange,
            final MessageContext inMessage, final SOAPFactory factory,
            SoapExternalListener soapExternalListener) throws AxisFault {
        if (exchange.getStatus().equals(ExchangeStatus.DONE)) {
            // exchange DONE, nothing to do, no soap response
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.log(Level.FINE, "Receive a Done status message");
            }
            
        	FlowAttributes flowAttributes = PetalsExecutionContext.getFlowAttributes();
			LoggingUtil.addMonitEndOrFailureTrace(this.logger, exchange, flowAttributes);
			
            return null;
        } else if (exchange.getStatus().equals(ExchangeStatus.ERROR)) {
            // exchange ERROR: throws an axis fault
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.log(Level.FINE, "Receive an Error status message");
            }
            throw new AxisFault(FAULT_SERVER, exchange.getError());

        } else if (exchange.getStatus().equals(ExchangeStatus.ACTIVE)) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.log(Level.FINE, "Receive an Active status message");
            }
            final MessageContext outMessage = MessageContextBuilder
                    .createOutMessageContext(inMessage);
            outMessage.getOperationContext().addMessageContext(outMessage);

            try {
                if (exchange.getFault() != null) {
                    final SOAPEnvelope envelope = Marshaller.createSOAPEnvelope(factory,
                            exchange.getFault(), true);
                    outMessage.setEnvelope(envelope);
                } else {
                    // Get the output message if the message is an instance of
                    // out
                    NormalizedMessage nm = exchange.getOutMessage();
                    if (nm == null) {
                        final String errorMsg = "The MEP '" + exchange.getPattern()
                                + "' does not accept a null response";
                        if (this.logger.isLoggable(Level.WARNING)) {
                            this.logger.log(Level.WARNING, errorMsg);
                        }
                        throw new AxisFault(errorMsg, FAULT_SERVER);
                    } else {
                        final SOAPEnvelope envelope = Marshaller.createSOAPEnvelope(factory, nm,
                                false);
                        outMessage.setEnvelope(envelope);
                        handleResponseAttachments(factory, nm, outMessage);
                    }
                }
                
            	FlowAttributes flowAttributes = PetalsExecutionContext.getFlowAttributes();
    			LoggingUtil.addMonitEndOrFailureTrace(this.logger, exchange, flowAttributes);
            } catch (final MessagingException me) {
                final String errorMsg = "Error while creating SOAP response";
                if (this.logger.isLoggable(Level.WARNING)) {
                    this.logger.log(Level.WARNING, errorMsg, me);
                }
                throw new AxisFault(errorMsg, FAULT_SERVER, me);
            } finally {
                try {	    			
	                exchange.setStatus(ExchangeStatus.DONE); 
                    soapExternalListener.send(exchange);
                } catch (MessagingException me) {
                    final String errorMsg = "Error while closing JBI MessageExchange.";
                    if (this.logger.isLoggable(Level.WARNING)) {
                        this.logger.log(Level.WARNING, errorMsg, me);
                    }
                    throw new AxisFault(errorMsg, FAULT_SERVER, me);
                }
            }

            return outMessage;
        } else {
            throw new AxisFault(SoapConstants.SOAP.ERROR_WRONG_MESSAGE_STATUS + " "
                    + exchange.getStatus().toString());
        }
    }

    /**
     * Send synchronously the JBI message
     * 
     * @param inContext
     *            the message context
     * @param factory
     *            the SOAP factory
     * @param exchange
     *            the message exchange
     * @param soapExternalListener
     *            the SOAP external listener
     * @return the message context with the response
     * 
     * @throws AxisFault
     */
    private MessageContext sendJBIMessage(final MessageContext inContext,
            final SOAPFactory factory, Exchange exchange,
            final SoapExternalListener soapExternalListener) throws AxisFault {
        boolean noTimeout = true;
        try {
            noTimeout = soapExternalListener.sendSync(exchange);
        } catch (final MessagingException e) {
            final String errorMsg = "Error while sending JBI exchange with id '"
                    + exchange.getExchangeId() + "'";
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.log(Level.WARNING, errorMsg, e);
            }
            throw new AxisFault(errorMsg, FAULT_SERVER, e);
        }

        if (!noTimeout) {
            final String errorMsg = "A timeout occurs on JBI exchange with id '"
                    + exchange.getExchangeId() + "'";
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.log(Level.WARNING, errorMsg);
            }
            throw new AxisFault(errorMsg, FAULT_SERVER);
        }

        // process the response received from JBI NMR. The out context is
        // returned
        return processJBIResponse(exchange, inContext, factory, soapExternalListener);
    }
}
