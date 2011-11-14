package org.petalslink.dsb.bpmn.deployer.client;

import com.ebmwebsourcing.bpmn.deployer.client.to.ProcessExecutorDescription;

public class PetalsDSBDescription extends ProcessExecutorDescription {

    @Override
    public String getDisplayName() {
        return "Petals DSB";
    }

    @Override
    public String getProcessExecutorService() {
        return "org.petalslink.dsb.bpmn.deployer.server.PetalsDSBBPELService";
    }

}
