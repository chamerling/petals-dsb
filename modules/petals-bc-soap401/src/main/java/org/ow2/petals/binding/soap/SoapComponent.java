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

package org.ow2.petals.binding.soap;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;

import javax.jbi.JBIException;
import javax.naming.Context;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Flow;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.phaseresolver.PhaseException;
import org.apache.axis2.phaseresolver.PhaseHolder;
import org.apache.axis2.phaseresolver.PhaseMetadata;
import org.apache.axis2.transport.jms.JMSConstants;
import org.apache.axis2.transport.jms.JMSListener;
import org.ow2.petals.binding.soap.Constants.Notification;
import org.ow2.petals.binding.soap.listener.incoming.PetalsReceiver;
import org.ow2.petals.binding.soap.listener.incoming.SoapExternalListenerManager;
import org.ow2.petals.component.framework.ComponentInformation;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;

import static org.ow2.petals.binding.soap.Constants.Axis2.AXIS2_XML;
import static org.ow2.petals.binding.soap.Constants.Component.TRANSFORMER_SYSTEM_PROPERY_NAME;
import static org.ow2.petals.binding.soap.Constants.Component.TRANSFORMER_SYSTEM_PROPERY_VALUE;

/**
 * The SOAP binding component.
 * 
 * @author alouis - EBM Websourcing
 * @author chamerling - EBM Websourcing
 * 
 */
public class SoapComponent extends PetalsBindingComponent {

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

            // Start the JMS transport layer
            /*
            try {
                this.soapContext.getAxis2ConfigurationContext().getAxisConfiguration()
                        .getTransportIn(Constants.TRANSPORT_JMS).getReceiver().start();
            } catch (final AxisJMSException e) {
                this.getLogger().info("The JMS Transport is not available.");
                this.getLogger().log(
                        Level.FINE,
                        "Unable to start the JMS Transport (" + e.getMessage()
                                + "). SOAP over JMS is not available.", e);
            }
            */

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

            // Stop the JMS transport layer
            /*
            this.soapContext.getAxis2ConfigurationContext().getAxisConfiguration().getTransportIn(
                    Constants.TRANSPORT_JMS).getReceiver().stop();
*/
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
    protected void postDoInit() throws JBIException {

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

            this.createJMSTransporter(axisConfigurationContext);

            this.getLogger().fine("Axis configuration context created.");
            this.soapContext.setAxis2ConfigurationContext(axisConfigurationContext);
        } catch (final AxisFault e) {
            this.getLogger().log(Level.WARNING, e.getMessage());
            throw new JBIException("Can not initialize SOAP BC", e);
        }

        // create the receiver
        this.petalsReceiver = new PetalsReceiver(this, this.getChannel(), this.getLogger());

        // set SOAP HTTP Server
        this.externalListenerManager = new SoapExternalListenerManager(this, this.getChannel(),
                (AbstractServiceUnitManager) this.getServiceUnitManager(), this.soapContext,
                this.petalsReceiver, this.getLogger());

        // for each module, we add its flows (if any) to the corresponding axis
        // phases
        this.engageModulesHandlers(axisConfigurationContext.getAxisConfiguration());

        ((SoapSUManager) this.getServiceUnitManager()).init(this.soapContext, this.getContext(),
                this.externalListenerManager.getSoapServerConfig(), this.petalsReceiver, this
                        .getLogger());

        // initialize the proxy SOAP address of the broker
        if ((this.getComponentConfiguration().getNotifications() != null)
                && this.getComponentConfiguration().getNotifications().isValue()) {
            // TODO: provide the endpoint consuming automatically the broker via
            // WS-Addressing
            if (this.notificationBrokerController != null) {
                this.notificationBrokerController
                        .setNotificationBrokerReferenceAddress(this.externalListenerManager
                                .getSoapServerConfig().getServicesURL()
                                + '/' + Notification.NOTIFICATION_BROKER_SERVICE);
                this.notificationBrokerController
                        .setPublisherRegistrationManagerReferenceAddress(this.externalListenerManager
                                .getSoapServerConfig().getServicesURL()
                                + '/' + Notification.PUBLISHER_REGISTRATION_MANAGER_SERVICE);
                this.notificationBrokerController
                        .setSubscriptionManagerReferenceAddress(this.externalListenerManager
                                .getSoapServerConfig().getServicesURL()
                                + '/' + Notification.SUBSCRIPTION_MANAGER_SERVICE);
            }
        }

        if (System.getProperty(TRANSFORMER_SYSTEM_PROPERY_NAME) == null) {
            System.setProperty(TRANSFORMER_SYSTEM_PROPERY_NAME, TRANSFORMER_SYSTEM_PROPERY_VALUE);
        }

        // share the service path property
        ComponentInformation componentInformation = this.getPlugin(ComponentInformation.class);
        if (componentInformation != null) {
            componentInformation.addProperty("service", this.externalListenerManager
                    .getSoapServerConfig().getServicesURL());
        }
    }

    /**
     * <p>
     * Create the JMS transport layer including a default connection factory.
     * </p>
     * <p>
     * The default connection factory is defined into the components extensions.
     * See component extensions for more information about the configuration of
     * this deafult connection factory.
     * </p>
     * <p>
     * If the component extensions does not include needed values, the JMS
     * transport layer will be activated without default JMS connection factory.
     * </p>
     * <p>
     * The JMS transport layer is defined as (following the syntax of
     * axis2.xml), italic bold value are component extensions:<br>
     * <code>&lt;transportReceiver name="jms" class="org.apache.axis2.transport.jms.JMSListener"&gt;<br>
     * &lt;parameter name="default" locked="false"><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;parameter name="java.naming.factory.initial" locked="false"&gt;<b><i>org.apache.activemq.jndi.ActiveMQInitialContextFactory</i></b>&lt;/parameter&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;parameter name="java.naming.provider.url" locked="false"&gt;<b><i>tcp://localhost:61616</i></b>&lt;/parameter&gt;<br>        
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;parameter name="transport.jms.ConnectionFactoryJNDIName" locked="false"&gt;<b><i>QueueConnectionFactory</i></b>&lt;/parameter&gt;<br>
     * &nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
     * &lt;/transportReceiver&gt;</code>
     * </p>
     * 
     * @param axisConfigurationContext
     * @throws AxisFault
     */
    private void createJMSTransporter(final ConfigurationContext axisConfigurationContext)
            throws AxisFault {

        final TransportInDescription jmsTransportDescription = new TransportInDescription(
                Constants.TRANSPORT_JMS);
        jmsTransportDescription.setReceiver(new JMSListener());
        axisConfigurationContext.getAxisConfiguration().addTransportIn(jmsTransportDescription);

        final String jndiInitialFactory = this.getComponentExtensions().get(
                org.ow2.petals.binding.soap.Constants.JmsTransportLayer.JNDI_INITIAL_FACTORY);
        final String jndiProviderUrl = this.getComponentExtensions().get(
                org.ow2.petals.binding.soap.Constants.JmsTransportLayer.JNDI_PROVIDER_URL);
        final String confacJndiName = this.getComponentExtensions().get(
                org.ow2.petals.binding.soap.Constants.JmsTransportLayer.CONFAC_JNDINAME);
        if ((jndiInitialFactory != null) && (jndiProviderUrl != null) && (confacJndiName != null)
                && (jndiInitialFactory.length() > 0) && (jndiProviderUrl.length() > 0)
                && (confacJndiName.length() > 0)) {

            this.soapContext.setJmsJndiInitialFactory(jndiInitialFactory);
            this.soapContext.setJmsJndiProviderUrl(jndiProviderUrl);
            this.soapContext.setJmsConnectionFactoryName(confacJndiName);

            this.getLogger().info(
                    "Create the default JMS connection factory ('"
                            + JMSConstants.DEFAULT_CONFAC_NAME + "'):");
            this
                    .getLogger()
                    .info(
                            "\t"
                                    + org.ow2.petals.binding.soap.Constants.JmsTransportLayer.JNDI_INITIAL_FACTORY
                                    + ": " + jndiInitialFactory);
            this
                    .getLogger()
                    .info(
                            "\t"
                                    + org.ow2.petals.binding.soap.Constants.JmsTransportLayer.JNDI_PROVIDER_URL
                                    + ": " + jndiProviderUrl);
            this.getLogger().info(
                    "\t" + org.ow2.petals.binding.soap.Constants.JmsTransportLayer.CONFAC_JNDINAME
                            + ": " + confacJndiName);

            final OMFactory omFactory = OMAbstractFactory.getOMFactory();

            final OMElement initialContextFactory = omFactory.createOMElement(new QName(
                    DeploymentConstants.TAG_PARAMETER));
            initialContextFactory.addAttribute(DeploymentConstants.ATTRIBUTE_NAME,
                    Context.INITIAL_CONTEXT_FACTORY, null);
            initialContextFactory.setText(jndiInitialFactory);

            final OMElement providerUrl = omFactory.createOMElement(new QName(
                    DeploymentConstants.TAG_PARAMETER));
            providerUrl
                    .addAttribute(DeploymentConstants.ATTRIBUTE_NAME, Context.PROVIDER_URL, null);
            providerUrl.setText(jndiProviderUrl);

            final OMElement connectionFactoryName = omFactory.createOMElement(new QName(
                    DeploymentConstants.TAG_PARAMETER));
            connectionFactoryName.addAttribute(DeploymentConstants.ATTRIBUTE_NAME,
                    JMSConstants.CONFAC_JNDI_NAME_PARAM, null);
            connectionFactoryName.setText(confacJndiName);

            final OMElement defaultConnectionFactory = omFactory.createOMElement(new QName(
                    DeploymentConstants.TAG_PARAMETER));
            defaultConnectionFactory.addChild(initialContextFactory);
            defaultConnectionFactory.addChild(providerUrl);
            defaultConnectionFactory.addChild(connectionFactoryName);

            jmsTransportDescription.addParameter(new Parameter(JMSConstants.DEFAULT_CONFAC_NAME,
                    defaultConnectionFactory));

        } else {
            this.getLogger().info(
                    "The JMS transport layer configuration is not complete. It is disabled.");
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

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ow2.petals.component.framework.bc.AbstractBindingComponent#
     * createServiceUnitManager()
     */
    @Override
    protected AbstractServiceUnitManager createServiceUnitManager() {

        return new SoapSUManager(this);
    }

}
