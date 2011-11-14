package org.petalslink.dsb.petalsbpm.client;

import java.util.Collection;

import com.ebmwebsourcing.bpmn.deployer.client.to.BPMNFile;
import com.ebmwebsourcing.bpmn.deployer.client.to.ProcessExecutorDescription;
import com.ebmwebsourcing.bpmn.deployer.client.ui.BPMNDeployerPanel;
import com.gwtext.client.widgets.Window;

public class BPMNDeploymentWindow extends Window {

    public BPMNDeploymentWindow(BPMNFile file, Collection<ProcessExecutorDescription> descs) {
        super();
        this.add(new BPMNDeployerPanel(file, descs));
    }
    
}
