
package org.ow2.petals.binding.soap;

import org.ow2.petals.commons.logger.ConsumeFlowStepBeginLogData;

@SuppressWarnings("serial")
public final class SoapConsumeFlowStepBeginLogData extends ConsumeFlowStepBeginLogData {

    public static final String REQUESTED_URL_KEY = "requestedURL";

    public SoapConsumeFlowStepBeginLogData(String flowInstanceId, String flowStepId,
            String flowInterfaceName, String flowServiceName, String flowEndpointName,
            String operationName, String requestedUrl) {
        super(flowInstanceId, flowStepId, flowInterfaceName, flowServiceName, flowEndpointName,
                operationName);
        putData(REQUESTED_URL_KEY, requestedUrl);
    }
}
