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
 * $Id: SoapComponent.java 154 25 sept. 06 alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soapproxy;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;

import javax.jbi.JBIException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Flow;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.phaseresolver.PhaseException;
import org.apache.axis2.phaseresolver.PhaseHolder;
import org.apache.axis2.phaseresolver.PhaseMetadata;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsReceiver;
import org.ow2.petals.binding.soapproxy.listener.incoming.SoapExternalListenerManager;
import org.ow2.petals.component.framework.bc.AbstractBindingComponent;

import static org.ow2.petals.binding.soapproxy.Constants.Axis2.AXIS2_XML;
import static org.ow2.petals.binding.soapproxy.Constants.Component.TRANSFORMER_SYSTEM_PROPERY_NAME;
import static org.ow2.petals.binding.soapproxy.Constants.Component.TRANSFORMER_SYSTEM_PROPERY_VALUE;

/**
 * The SOAP binding component.
 * 
 * @author alouis - EBM Websourcing
 * @author chamerling - EBM Websourcing
 * 
 */
public class SoapComponent extends AbstractBindingComponent {

    /**
     * The listener processing SOAP request providing from external clients.
     * Incoming SOAP requests.
     */
    protected SoapExternalListenerManager externalListenerManager;

    /**
     * The SOAP component context
     */
    protected SoapComponentContext soapContext;

    /**
     * The PEtALS receiver
     */
    private PetalsReceiver petalsReceiver;

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.component.common.AbstractComponent#doStart()
     */
    @Override
    public void doStart() throws JBIException {

        try {
            this.externalListenerManager.start();
        } catch (final AxisFault e) {
            this.getLogger().severe(e.getMessage());
            throw new JBIException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.component.common.AbstractComponent#doStop()
     */
    @Override
    public void doStop() throws JBIException {
        try {
            this.externalListenerManager.stop();
        } catch (final AxisFault e) {
            this.getLogger().severe(e.getMessage());
            throw new JBIException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.component.common.AbstractComponent#doInit()
     */
    @Override
    protected void doInit() throws JBIException {

        // create the SOAP component context
        this.soapContext = new SoapComponentContext(this.getContext(), this
                .getComponentConfiguration(), this.getLogger());

        // the axis2.xml is required
        final File axis2File = new File(this.getContext().getInstallRoot(), AXIS2_XML);
        if (!axis2File.exists()) {
            throw new JBIException("Can not get axis2 configuration file");
        }

        // get axis configuration context
        this.getLogger().fine("Creating Axis configuration context...");
        ConfigurationContext axisConfigurationContext = null;
        try {
            axisConfigurationContext = ConfigurationContextFactory
                    .createConfigurationContextFromFileSystem(this.getContext().getInstallRoot(),
                            axis2File.getAbsolutePath());

            this.getLogger().fine("Axis configuration context created.");
            this.soapContext.setAxis2ConfigurationContext(axisConfigurationContext);
        } catch (final AxisFault e) {
            this.getLogger().log(Level.WARNING, e.getMessage());
            throw new JBIException("Can not initialize SOAP PROXY BC", e);
        }

        // create the receiver
        this.petalsReceiver = new PetalsReceiver(this, this.getChannel(), this.getLogger());

        // set SOAP HTTP Server
        this.externalListenerManager = new SoapExternalListenerManager(this, this.getChannel(),
                this.soapContext, this.petalsReceiver, this.getLogger());

        // for each module, we add its flows (if any) to the corresponding axis
        // phases
        // FIXME = Not sure that we need to engage all!!!
        // this.engageModulesHandlers(axisConfigurationContext.getAxisConfiguration());

        ServiceCreator creator = new ServiceCreator();
        creator.init(this.soapContext, this.getContext(), this.externalListenerManager
                .getSoapServerConfig(), this.petalsReceiver, this.getLogger());

        creator.createServices();

        if (System.getProperty(TRANSFORMER_SYSTEM_PROPERY_NAME) == null) {
            System.setProperty(TRANSFORMER_SYSTEM_PROPERY_NAME, TRANSFORMER_SYSTEM_PROPERY_VALUE);
        }
    }

    /**
     * @param axisConfiguration
     * @throws JBIException
     */
    @SuppressWarnings("unchecked")
    private void engageModulesHandlers(final AxisConfiguration axisConfiguration)
            throws JBIException {
        final Iterator<String> moduleNames = axisConfiguration.getModules().keySet().iterator();
        while (moduleNames.hasNext()) {
            try {
                this.engageModuleHandlersIntoPhases(
                        axisConfiguration.getModule(moduleNames.next()), axisConfiguration);
            } catch (final PhaseException e) {
                this.getLogger().log(Level.WARNING, e.getMessage());
                throw new JBIException("Can not initialize SOAP BC", e);
            }
        }
        this.getLogger().fine("Axis modules flows added");
    }

    /**
     * For each Axis phase, we add the corresponding module's handlers if any.
     * 
     * @param module
     *            the module to process
     * @param axisConfiguration
     *            the axis configuration
     * @throws PhaseException
     *             can the thrown by method addHandler()
     */
    private void engageModuleHandlersIntoPhases(final AxisModule module,
            final AxisConfiguration axisConfiguration) throws PhaseException {
        Flow moduleFlow = null;
        PhaseHolder phaseHolder = null;

        this.getLogger().fine("Engaging module " + module.getName() + " in flows");

        // for each phase, we add the corresponding module's handlers if any
        for (int type = PhaseMetadata.IN_FLOW; type <= PhaseMetadata.FAULT_OUT_FLOW; type++) {

            if (type == PhaseMetadata.IN_FLOW) {

                phaseHolder = new PhaseHolder(axisConfiguration.getInFlowPhases());
                moduleFlow = module.getInFlow();

            } else if (type == PhaseMetadata.OUT_FLOW) {

                phaseHolder = new PhaseHolder(axisConfiguration.getOutFlowPhases());
                moduleFlow = module.getOutFlow();

            } else if (type == PhaseMetadata.FAULT_IN_FLOW) {

                phaseHolder = new PhaseHolder(axisConfiguration.getInFaultFlowPhases());
                moduleFlow = module.getFaultInFlow();

            } else if (type == PhaseMetadata.FAULT_OUT_FLOW) {

                phaseHolder = new PhaseHolder(axisConfiguration.getOutFaultFlowPhases());
                moduleFlow = module.getFaultOutFlow();
            }

            if ((moduleFlow != null) && (phaseHolder != null)) {
                for (int j = 0; j < moduleFlow.getHandlerCount(); j++) {
                    this.getLogger().fine(
                            "Adding module flow " + (moduleFlow.getHandler(j).getName())
                                    + " to phase holder " + phaseHolder + " for module "
                                    + module.getName());
                    phaseHolder.addHandler(moduleFlow.getHandler(j));
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    public SoapComponentContext getSoapContext() {
        return this.soapContext;
    }

    /**
     * 
     * @return
     */
    public SoapExternalListenerManager getExternalListenerManager() {
        return this.externalListenerManager;
    }
}
