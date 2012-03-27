
package org.ow2.petals.binding.soap.listener.incoming;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.COMPONENT_CONTEXT_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_EXTENSIONS_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.LOGGER_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.PETALS_RECEIVER_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.RAMPART_MODULE;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.WSDL_FOUND_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.SOAP.FAULT_SERVER;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessageExchange.Role;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.DispatchPhase;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.engine.Phase;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Input;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.binding.BindingProtocol.SOAPMEPConstants;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.WsdlHelper;
import org.ow2.petals.commons.PetalsExecutionContext;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.util.LoggingUtil;

public class PetalsDispatchPhase extends DispatchPhase {

    @Override
    public void checkPostConditions(MessageContext msgContext) throws AxisFault {

        // necessary because there is only one instance for the consumer and provider side
        if (msgContext.isServerSide()) {
            AxisService service = msgContext.getAxisService();

            // if the service has been found with the previous handler
            if(service != null) {
                Parameter wsdlFoundParam = service.getParameter(WSDL_FOUND_SERVICE_PARAM);
                boolean wsdlFound = (Boolean) wsdlFoundParam.getValue();
    
                // if the WSDL has already been retrieved
                if (!wsdlFound) {
                    Parameter consumesConfigParam = service.getParameter(CONSUMES_SERVICE_PARAM);
                    Consumes consumes = (Consumes) consumesConfigParam.getValue();
                    Parameter componentContextParam = service
                            .getParameter(COMPONENT_CONTEXT_SERVICE_PARAM);
                    ComponentContext componentContext = (ComponentContext) componentContextParam
                            .getValue();
                    Parameter loggerParam = service.getParameter(LOGGER_SERVICE_PARAM);
                    Logger logger = (Logger) loggerParam.getValue();
                    Parameter petalsReceiverParam = service.getParameter(PETALS_RECEIVER_SERVICE_PARAM);
                    PetalsReceiver petalsReceiver = (PetalsReceiver) petalsReceiverParam.getValue();
    
                    Description desc = WsdlHelper.getDescription(consumes, componentContext, logger);
                    
                    if (desc != null) {
                        addAxisOperation(service, petalsReceiver, desc);
    
                        // update the endpoint data
                        updateEndpoint(service, msgContext);
                        
                        // enable WS-Addressing if necessary
                        Parameter consumesExtensionsParam = service
                                .getParameter(CONSUMES_EXTENSIONS_SERVICE_PARAM);
                        ConfigurationExtensions consumesExtensions = (ConfigurationExtensions) consumesExtensionsParam
                                .getValue();
                        if (SUPropertiesHelper.isWSAEnabled(consumesExtensions)) {
                            Parameter disableAddressingParam = service
                                    .getParameter(
                                            org.apache.axis2.addressing.AddressingConstants.DISABLE_ADDRESSING_FOR_IN_MESSAGES);
                            service.removeParameter(disableAddressingParam);
                        }
                        
                        // reset the message context flow to invoke all the phases again
                        resetFlow(msgContext);                       
                        
                        // enable WS-Security if necessary
                        if(SUPropertiesHelper.getModules(consumesExtensions).contains(RAMPART_MODULE)) {
                            AxisModule axisModule = service.getAxisConfiguration().getModule(RAMPART_MODULE);
                            service.engageModule(axisModule, service);
                        }
                
                        wsdlFoundParam.setValue(true);
                        service.removeParameter(petalsReceiverParam);
                    } else {
						String errorMessage = "WSDL description can not been retrieved from JBI endpoint";
						LoggingUtil.addMonitFailureTrace(logger,
								PetalsExecutionContext.getFlowAttributes(),
								errorMessage, Role.CONSUMER);
						throw new AxisFault(errorMessage, FAULT_SERVER);
                    }
                } else {
                    super.checkPostConditions(msgContext);                    
                }
            }
        } 
    }

    private void updateEndpoint(AxisService service, MessageContext msgContext) throws AxisFault {
        service.getEndpoints().clear();
        Utils.addEndpointsToService(service);

        // necessary to avoid NPE
        AxisEndpoint axisEndpoint = (AxisEndpoint) msgContext
                .getProperty(WSDL2Constants.ENDPOINT_LOCAL_NAME);
        if (axisEndpoint != null) {
            axisEndpoint = service.getEndpoint(axisEndpoint.getName());
            msgContext.setProperty(WSDL2Constants.ENDPOINT_LOCAL_NAME, axisEndpoint);
        }
    }

    private void resetFlow(MessageContext msgContext) {
        ConfigurationContext confContext = msgContext.getConfigurationContext();
        List<Phase> preCalculatedPhases;
        if (msgContext.isFault() || msgContext.isProcessingFault()) {
            preCalculatedPhases = confContext.getAxisConfiguration().getInFaultFlowPhases();
            msgContext.setFLOW(MessageContext.IN_FAULT_FLOW);
        } else {
            preCalculatedPhases = confContext.getAxisConfiguration().getInFlowPhases();
            msgContext.setFLOW(MessageContext.IN_FLOW);
        }       
        
        ArrayList<Handler> executionChain = new ArrayList<Handler>();
        executionChain.addAll(preCalculatedPhases);
        msgContext.setExecutionChain(executionChain);
        msgContext.resetExecutedPhases();
    }

    private void addAxisOperation(AxisService service, PetalsReceiver petalsReceiver,
            Description desc) {
        List<Binding> bindings = desc.getBindings();
        for (Binding binding : bindings) {
            List<BindingOperation> bindingOperations = binding.getBindingOperations();
            for (BindingOperation bindingOperation : bindingOperations) {
                final AxisOperation genericOperation = new InOutAxisOperation(bindingOperation.getQName());
                genericOperation.setSoapAction(bindingOperation.getSoapAction());

                String wsdl2Mep = getWSDL2Mep(bindingOperation);
                genericOperation.setMessageExchangePattern(wsdl2Mep);

                genericOperation.setMessageReceiver(petalsReceiver);
                service.addOperation(genericOperation);
                
                                
                Operation operation = bindingOperation.getOperation();
                if(operation != null) {
					Input input = operation.getInput();
					if(input != null) {
						List<Part> parts = input.getParts();
						if(parts != null && parts.size() > 1) {
							org.ow2.easywsdl.schema.api.Element firstElement = parts.get(0).getElement();
			                service.addMessageElementQNameToOperationMapping(firstElement.getQName(), genericOperation);
						}
					}
                }
            }
        }
    }

    private String getWSDL2Mep(BindingOperation operation) {
        SOAPMEPConstants soapMep = operation.getMEP();
        String wsdl2Mep;
        if (soapMep == SOAPMEPConstants.ONE_WAY) {
            wsdl2Mep = WSDL2Constants.MEP_URI_IN_ONLY;
        } else if (soapMep == SOAPMEPConstants.REQUEST_RESPONSE) {
            wsdl2Mep = WSDL2Constants.MEP_URI_IN_OUT;
        } else {
            wsdl2Mep = WSDL2Constants.MEP_URI_OUT_ONLY;
        }
        return wsdl2Mep;
    }
}
