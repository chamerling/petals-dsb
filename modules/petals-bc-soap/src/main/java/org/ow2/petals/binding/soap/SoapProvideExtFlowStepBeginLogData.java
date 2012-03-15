
package org.ow2.petals.binding.soap;

import org.ow2.petals.commons.logger.ProvideExtFlowStepBeginLogData;

@SuppressWarnings("serial")
public final class SoapProvideExtFlowStepBeginLogData extends ProvideExtFlowStepBeginLogData {

    private static final String REQUESTED_URL_KEY = "requestedURL";
    
    public SoapProvideExtFlowStepBeginLogData(String flowInstanceId, String flowStepId, String requestedUrl) {
        super(flowInstanceId, flowStepId);
        putData(REQUESTED_URL_KEY, requestedUrl);
    }
}
