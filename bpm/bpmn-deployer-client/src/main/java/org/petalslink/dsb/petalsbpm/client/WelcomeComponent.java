package org.petalslink.dsb.petalsbpm.client;

import java.util.List;

import com.ebmwebsourcing.geasytools.webeditor.api.components.IComponent;
import com.ebmwebsourcing.geasytools.webeditor.api.components.IComponentDispatcherCommand;
import com.ebmwebsourcing.geasytools.webeditor.api.components.IContentPanelComponent;
import com.ebmwebsourcing.geasytools.webeditor.api.components.menu.IMenuItem;
import com.ebmwebsourcing.geasytools.webeditor.api.components.menu.IMenuItemAction;
import com.ebmwebsourcing.geasytools.webeditor.api.components.menu.IMenuItemActionType;
import com.ebmwebsourcing.geasytools.webeditor.api.core.events.IEditorEventBus;
import com.ebmwebsourcing.geasytools.webeditor.api.project.content.events.IUserContentHandler;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.component.Component;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.project.configuration.presenter.ExportProjectPresenter;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.project.configuration.presenter.ImportProjectPresenter;
import com.ebmwebsourcing.geasytools.webeditor.impl.client.project.configuration.presenter.OpenProjectPresenter;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.descriptive.DescriptiveBPMNProjectPlugin;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.descriptive.NewDescriptiveBPMNProjectConfigurationView;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.descriptive.process.DescriptiveBPMNPrivateProcessPlugin;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.executable.ExecutableBPMNProjectPlugin;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.executable.NewExecutableBPMNProjectConfigurationView;
import com.ebmwebsourcing.petalsbpm.client.plugin.bpmn.executable.process.ExecutableBPMNPrivateProcessPlugin;
import com.ebmwebsourcing.webeditor.api.domain.project.IProjectInstance;
import com.ebmwebsourcing.webeditor.api.domain.project.ProjectValidationException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.Panel;

public class WelcomeComponent extends Component implements IContentPanelComponent,IComponent{
    
    private Panel welcomePanel;
    
    public WelcomeComponent(final IEditorEventBus eventBus) {
    
        welcomePanel = new Panel("Welcome in Petals BPM");
        welcomePanel.setClosable(true);
        welcomePanel.setCollapsible(true);
        welcomePanel.setAutoHeight(true);
        welcomePanel.setHeader(false);
        welcomePanel.setBorder(false);
        
        AbsolutePanel header = new AbsolutePanel();
        header.setStyleName("welcome-header");

        HTML welcomeLine = new HTML();
        welcomeLine.setStyleName("welcome-line");
        
        Label lbl_newProcess = new Label("Create a new BPMN 2.0 process");
        
        lbl_newProcess.setStyleName("home-label");
        
        Label lbl_sampleProcess = new Label("Load a sample process");
        lbl_sampleProcess.setStyleName("home-label");
        
        
        HTML descriptiveProcessButton = new HTML("<span>Descriptive Process</span>");
        descriptiveProcessButton.setStyleName("descriptive-process");
        descriptiveProcessButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                DescriptiveBPMNProjectPlugin plugin = new DescriptiveBPMNPrivateProcessPlugin();
                plugin.setEventBus(eventBus);
                new NewDescriptiveBPMNProjectConfigurationView(plugin).open();
            }
        });
        
        
        HTML executableProcessButton = new HTML("<span>Executable Process</span>");
        executableProcessButton.setStyleName("executable-process");
        executableProcessButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                ExecutableBPMNProjectPlugin plugin = new ExecutableBPMNPrivateProcessPlugin();
                plugin.setEventBus(eventBus);
                new NewExecutableBPMNProjectConfigurationView(plugin).open();
            }
        });
        
        

        
        
        AbsolutePanel filesButtons = new AbsolutePanel();
        filesButtons.setStyleName("files-btns");
        
        final HTML openBtn = new HTML();
        openBtn.addStyleName("btn");
        openBtn.addStyleName("open");
        openBtn.setTitle("Open a project");
        openBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                String req = OpenProjectPresenter.PLACE.getId();
                
                History.newItem(req);
                
            }
        });
        
        
        openBtn.addMouseOverHandler(new MouseOverHandler() {
            
            @Override
            public void onMouseOver(MouseOverEvent event) {
                
                openBtn.addStyleName("open-over");
                
            }
        });
        
        
        openBtn.addMouseOutHandler(new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                
                openBtn.removeStyleName("open-over");
                
            }
        });
        
        
        final HTML importBtn = new HTML();
        importBtn.addStyleName("btn");
        importBtn.addStyleName("import");
        importBtn.setTitle("Import a file");
        
        importBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                String req = ImportProjectPresenter.PLACE.getId();
                
                History.newItem(req);
                
            }
        });
        
        importBtn.addMouseOverHandler(new MouseOverHandler() {
            
            @Override
            public void onMouseOver(MouseOverEvent event) {
                
                importBtn.addStyleName("import-over");
                
            }
        });
        
        importBtn.addMouseOutHandler(new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                
                importBtn.removeStyleName("import-over");
                
            }
        });
        
        
        final HTML exportBtn = new HTML();
        exportBtn.addStyleName("btn");
        exportBtn.addStyleName("export");
        
        exportBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                String req = ExportProjectPresenter.PLACE.getId();
                
                History.newItem(req);
                
                
            }
        });
        
        exportBtn.addMouseOverHandler(new MouseOverHandler() {
            
            @Override
            public void onMouseOver(MouseOverEvent event) {
                
                exportBtn.addStyleName("export-over");
                
            }
        });
        
        exportBtn.addMouseOutHandler(new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                
                exportBtn.removeStyleName("export-over");
                
            }
        });
        
        final HTML helpBtn = new HTML();
        helpBtn.addStyleName("btn");
        helpBtn.addStyleName("help");
        helpBtn.setTitle("Help");
        
        helpBtn.addMouseOverHandler(new MouseOverHandler() {
            
            @Override
            public void onMouseOver(MouseOverEvent event) {
                
                helpBtn.addStyleName("help-over");
                
            }
        });
        
        helpBtn.addMouseOutHandler(new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                
                helpBtn.removeStyleName("help-over");
                
            }
        });
        
        helpBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                Window.open("http://research.petalslink.org/display/petalsbpm", "_blank", "");
                
            }
        });
        
        filesButtons.add(openBtn);
        filesButtons.add(importBtn);
        //filesButtons.add(exportBtn);
        filesButtons.add(helpBtn);
        
        Label lbl_welcome = new Label("Petals BPM:  BPMN 2.0 Editor");
        lbl_welcome.setStyleName("welcome-label");
        header.add(lbl_welcome,600,75);
        header.add(welcomeLine,550,70);
        header.add(descriptiveProcessButton,550,150);
        header.add(executableProcessButton,800,150);        
        header.add(filesButtons,590,300);
        
        welcomePanel.add(header);
        
        
        welcomePanel.doLayout();
        
        initWidget(welcomePanel);
    }
    
    
    @Override
    public String getId() {
        return welcomePanel.getId();
    }


    @Override
    public void disableMenuItemsByType(IMenuItemActionType type) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void enableMenuItemsByType(IMenuItemActionType type) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public List<IComponentDispatcherCommand> getAssociatedComponentsDispatcherCommands() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<IMenuItem> getMenuItems() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onActionRequest(IMenuItemAction action) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public IProjectInstance getProjectInstance() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void loadProjectInstance(IProjectInstance projectInstance) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public HandlerRegistration addClickHandler(ClickHandler arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public IUserContentHandler getUserContentHandler() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setSaved(boolean b) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean isSaved() {
        // TODO Auto-generated method stub
        return false;
    }



    @Override
    public void validate() throws ProjectValidationException {
        // TODO Auto-generated method stub
        
    }

}
