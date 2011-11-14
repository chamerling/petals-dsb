package org.petalslink.dsb.petalsbpm.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.petalslink.dsb.bpmn.deployer.client.PetalsDSBDescription;

import com.ebmwebsourcing.bpmn.deployer.client.to.BPMNFile;
import com.ebmwebsourcing.bpmn.deployer.client.to.ProcessExecutorDescription;
import com.ebmwebsourcing.geasytools.webeditor.api.components.menu.IMenuItemClickHandler;
import com.ebmwebsourcing.geasytools.webeditor.api.plugin.IPlugin;
import com.ebmwebsourcing.geasytools.webeditor.api.project.response.ISaveProjectInstanceResponseHandler;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.core.EditorController;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.project.request.SaveProjectInstanceRequest;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.request.RequestEvent;
import com.ebmwebsourcing.geasytools.webeditor.ui.component.menu.GWTExtMenuComponentButton;
import com.ebmwebsourcing.geasytools.webeditor.ui.core.EditorView;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.BPMNProjectInstance;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.descriptive.process.DescriptiveBPMNPrivateProcessPlugin;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.executable.ExecutableBPMNProjectType;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.executable.process.ExecutableBPMNPrivateProcessPlugin;
import com.ebmwebsourcing.webeditor.api.domain.project.IProjectFile;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Viewport;


public class DSBPetalsBPMClient implements EntryPoint {
    
    private GWTExtMenuComponentButton bpelDeployBtn;

    @Override
    public void onModuleLoad() {
        final EditorView bpmneditorView = new EditorView();
        final Panel panel = (Panel) bpmneditorView.getMainWidget();

        new Viewport(panel);

        HashSet<IPlugin> plugins = new HashSet<IPlugin>();
        plugins.add(new DescriptiveBPMNPrivateProcessPlugin());
        plugins.add(new ExecutableBPMNPrivateProcessPlugin());

        try {
            addButtons(bpmneditorView);
            
            EditorController ec = new EditorController(bpmneditorView, plugins);

            bpmneditorView.getDefaultLayout().getMainContentPanelPlaceHolder().addComponent(
                    new WelcomeComponent(ec.getEventBus()));
            
            addButtonListeners(ec);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void addButtons(EditorView bpmneditorView) {
        bpelDeployBtn = new GWTExtMenuComponentButton(new Image(GWT.getModuleBaseURL()+"/images/icons/bus.png"));
        bpelDeployBtn.setTitle("Deploy the processes");
        bpmneditorView.getToolbarComponent().addMenuButton(bpelDeployBtn);
    }
    
    private void addButtonListeners(final EditorController ec) {
        bpelDeployBtn.setClickHandler(new IMenuItemClickHandler() {
            @Override
            public void onClick() {
                // 1 - get the current project instance
                final BPMNProjectInstance pi = (BPMNProjectInstance) ec.getProjectManager().getActualProjectInstance();
                
                // 2 - check it is an executable one
                if(!(pi.getProjectType() instanceof ExecutableBPMNProjectType)) {
                    MessageBox.alert("Deployment is only possible for executable projects");
                    return;
                }
                
                // 3 - write it on the server if it is not already done or ask the user to save before
                ec.getEventBus().fireEvent(new RequestEvent(new SaveProjectInstanceRequest(pi), new ISaveProjectInstanceResponseHandler() {
                    @Override
                    public void receiveResponse(IProjectFile result) {
                        // 4 - call the deployer window with the definitions, the file address and the PetalsDSBDescription
                        BPMNFile bpmnfile = new BPMNFile(result.getAbsolutePath(), pi.getDefinitions());
                        Collection<ProcessExecutorDescription> descs = new ArrayList<ProcessExecutorDescription>();
                        descs.add(new PetalsDSBDescription());
                        new BPMNDeploymentWindow(bpmnfile, descs).show();
                    }
                }));
            }
        });
    }
  
}
