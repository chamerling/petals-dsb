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
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.ServiceBuilder;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.transport.jms.JMSConstants;
import org.apache.axis2.util.XMLUtils;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.petals.binding.soap.listener.incoming.PetalsAxisService;
import org.ow2.petals.binding.soap.listener.incoming.PetalsReceiver;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.binding.soap.util.SoapSUClassLoader;
import org.ow2.petals.component.framework.ComponentInformation;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.su.BindingComponentServiceUnitManager;
import org.ow2.petals.component.framework.su.ServiceUnitDataHandler;
import org.ow2.petals.component.framework.util.ClassLoaderUtil;
import org.ow2.petals.ws.fault.WsnFault;
import org.ow2.petals.ws.notification.WsnManager;
import org.ow2.petals.ws.topic.Topic;
import org.w3c.dom.Document;

import static org.ow2.petals.binding.soap.Constants.Axis2.SERVICES_XML;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODULES;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.POLICY_PATH;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.SERVICE_PARAMETERS;

/**
 * A service unit listener used to register new service into Axis Engine during
 * SU deployment.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class SoapSUManager extends BindingComponentServiceUnitManager {

    /**
     * The JBI component context
     */
    private ComponentContext componentContext;

    /**
     * The SOAP component context (configuration values)
     */
    private SoapComponentContext soapContext;

    /**
     * JDK logger
     */
    private Logger logger;

    /**
     * 
     */
    private SoapServerConfig soapServerConfig;

    private PetalsReceiver petalsReceiver;

    public SoapSUManager(SoapComponent bindingComponent) {
        super(bindingComponent);
    }

    /**
     * Creates a new instance of {@link SoapSUManager}
     * 
     * @param soapContext
     * @param axisConfigurationContext
     * @param componentContext
     * @param servicesURL
     * @param logger
     */
    public void init(final SoapComponentContext soapContext,
            final ComponentContext componentContext, final SoapServerConfig soapServerConfig,
            final PetalsReceiver petalsReceiver, final Logger logger) {
        this.soapContext = soapContext;
        this.componentContext = componentContext;
        this.soapServerConfig = soapServerConfig;
        this.petalsReceiver = petalsReceiver;
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.su.AbstractServiceUnitManager#doDeploy
     * (java.lang.String, java.lang.String,
     * org.ow2.petals.component.framework.jbidescriptor.generated.Jbi)
     */
    @Override
    protected void doDeploy(String serviceUnitName, String suRootPath, Jbi jbiDescriptor)
            throws PEtALSCDKException {
        this.logger.log(Level.FINE, "Deploying a new AxisService for SU " + serviceUnitName);

        // Global service unit configuration
        this.soapContext.addJbiDescriptor(serviceUnitName, jbiDescriptor);

        final File servicesXml = new File(suRootPath, SERVICES_XML);
        if (servicesXml.exists()) {
            this.soapContext.addServiceDescriptor(serviceUnitName, servicesXml);
        }

        // Local service configuration
        this.createServicesContext(serviceUnitName, suRootPath, jbiDescriptor);
    }

    /**
     * Create the services context
     * 
     * @param serviceUnitName
     * @param suRootPath
     * @param descriptor
     */
    private void createServicesContext(final String serviceUnitName, final String suRootPath,
            final Jbi descriptor) {
        ServiceUnitDataHandler suDatahandler = null;

        final List<Provides> providesList = descriptor.getServices().getProvides();
        for (final Provides provides : providesList) {
            if (suDatahandler == null) {
                suDatahandler = this.getSUDataHandlerForService(provides);
            }

            ServiceContext<Provides> context = this.soapContext.getProvidersManager()
                    .createServiceContext(provides);
            ConfigurationExtensions extensions = suDatahandler.getConfigurationExtensions(provides);
            context.setModules(this.getModules(extensions));
            context.setServiceParams(this.getServiceParameters(extensions));
            context.setPolicyPath(this.getPolicyPath(suRootPath, extensions));
            context.setClassloader(this.getServiceClassloader(suRootPath, extensions));
        }

        final List<Consumes> consumesList = descriptor.getServices().getConsumes();
        for (final Consumes consumes : consumesList) {
            if (suDatahandler == null) {
                suDatahandler = this.getSUDataHandlerForService(consumes);
            }

            ServiceContext<Consumes> context = this.soapContext.getConsumersManager()
                    .createServiceContext(consumes);
            ConfigurationExtensions extensions = suDatahandler.getConfigurationExtensions(consumes);
            context.setModules(this.getModules(extensions));
            context.setServiceParams(this.getServiceParameters(extensions));
            context.setPolicyPath(this.getPolicyPath(suRootPath, extensions));
            context.setClassloader(this.getServiceClassloader(suRootPath, extensions));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.su.AbstractServiceUnitManager#doStart
     * (java.lang.String)
     */
    @Override
    protected void doStart(String serviceUnitName) throws PEtALSCDKException {
        // get descriptors
        final Jbi descriptor = this.soapContext.getJbiDescriptor(serviceUnitName);
        final File serviceDescription = this.soapContext.getServiceDescriptor(serviceUnitName);

        if (descriptor != null) {

            ServiceUnitDataHandler suDatahandler = null;
            /*
             * Register new Axis2 service only on consumes nodes. Create an
             * AxisService for each requested endpoint. If an enpoint has not
             * been found, simply log a severe error. TODO : If a service
             * creation fails, and if services have been already registered for
             * this service unit, unregister them.
             */
            final List<Consumes> consumesList = descriptor.getServices().getConsumes();
            for (final Consumes consumes : consumesList) {
                if (suDatahandler == null) {
                    suDatahandler = this.getSUDataHandlerForService(consumes);
                }
                final ConfigurationExtensions extensions = suDatahandler
                        .getConfigurationExtensions(consumes);
                this.checkConsume(extensions);
                List<String> urls = this
                        .createAxisService(consumes, serviceDescription, extensions);
                for (String url : urls) {
                    this.addToExpose(url);
                }
            }

            // create/restore topics and services
            final List<Provides> providesList = descriptor.getServices().getProvides();
            for (final Provides provides : providesList) {
                if (suDatahandler == null) {
                    suDatahandler = this.getSUDataHandlerForService(provides);
                }
                final ConfigurationExtensions extensions = suDatahandler
                        .getConfigurationExtensions(provides);
                this.checkProvide(extensions);
                if (SUPropertiesHelper.isServiceMode(extensions)) {
                    this.createWSAccess(suDatahandler.getConfigurationExtensions(provides));
                } else if (SUPropertiesHelper.isTopicMode(extensions)) {
                    this.createTopic(suDatahandler.getConfigurationExtensions(provides));
                } else {
                    this.logger.warning("Invalid mode specified in the Service Unit");
                }

                ServiceContext<Provides> context = this.soapContext.getProvidersManager()
                        .getServiceContext(provides);
                ServiceEndpoint srvEp = this.componentContext.getEndpoint(
                        provides.getServiceName(), provides.getEndpointName());
                Document doc;
                try {
                    doc = this.componentContext.getEndpointDescriptor(srvEp);
                    context.setServiceDescription(WSDL4ComplexWsdlFactory.newInstance()
                            .newWSDLReader().read(doc));
                } catch (JBIException e) {
                    this.logger.severe("No endpoint descriptor found for the service endpoint : "
                            + srvEp);
                } catch (WSDL4ComplexWsdlException e) {
                    this.logger.severe("Wsdl reading error" + e.getMessage());
                } catch (URISyntaxException e) {
                    this.logger.severe("Wsdl reading error" + e.getMessage());
                }
            }
        }
    }

    /**
     * Create an external web service provider
     * 
     * @param provides
     */
    protected void createWSAccess(final ConfigurationExtensions extensions) {

        final String externalWS = SUPropertiesHelper.getWSATo(extensions);
        final String mode = SUPropertiesHelper.getMode(extensions);

        if (externalWS != null) {
            this.logger.info("Providing access to external service '" + externalWS + "' in mode '"
                    + mode + "'");

            this.addToConsume(externalWS);

        } else {
            this.logger.info("No external Web Service specified (WS-Addressing), mode is '" + mode
                    + "'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.su.AbstractServiceUnitManager#doStop
     * (java.lang.String)
     */
    @Override
    protected void doStop(String serviceUnitName) throws PEtALSCDKException {

        final Jbi descriptor = this.soapContext.getJbiDescriptor(serviceUnitName);
        if (descriptor != null) {
            ServiceUnitDataHandler suDatahandler = null;

            // delete registered axis services
            final List<Consumes> consumesList = descriptor.getServices().getConsumes();
            for (final Consumes consumes : consumesList) {
                if (suDatahandler == null) {
                    suDatahandler = this.getSUDataHandlerForService(consumes);
                }
                this.unregisterAxisService(suDatahandler.getConfigurationExtensions(consumes));
                this.removeFromExpose(this.soapServerConfig.getServicesURL()
                        + "/"
                        + SUPropertiesHelper.getServiceName(suDatahandler
                                .getConfigurationExtensions(consumes)));
            }

            // unregister topic
            final List<Provides> providesList = descriptor.getServices().getProvides();
            for (final Provides provides : providesList) {
                if (suDatahandler == null) {
                    suDatahandler = this.getSUDataHandlerForService(provides);
                }
                this.deleteTopic(suDatahandler.getConfigurationExtensions(provides));
                this.removeFromConsume(SUPropertiesHelper.getAddress(suDatahandler
                        .getConfigurationExtensions(provides)));
            }
        }
    }

    @Override
    protected void doUndeploy(String serviceUnitName) throws PEtALSCDKException {

        ServiceUnitDataHandler suDatahandler = null;

        final Jbi descriptor = this.getServiceUnitDataHandlers().get(serviceUnitName)
                .getDescriptor();

        // remove the topic persistance
        final List<Provides> providesList = descriptor.getServices().getProvides();
        for (final Provides provides : providesList) {
            if (suDatahandler == null) {
                suDatahandler = this.getSUDataHandlerForService(provides);
            }
            this.unregisterTopic(suDatahandler.getConfigurationExtensions(provides));
        }

        this.cleanServiceContexts(serviceUnitName, descriptor);
    }

    /**
     * Create the Axis service that will handle incoming SOAP calls whatever the
     * transport layer.
     * 
     * @param consumes
     * @param serviceDescriptor
     * @throws PEtALSComponentSDKException
     */
    private List<String> createAxisService(final Consumes consumes, final File serviceDescriptor,
            final ConfigurationExtensions extensions) throws PEtALSCDKException {

        List<String> serviceURLs = new ArrayList<String>(1);

        // Get the created service name
        final String newServiceName = SUPropertiesHelper.getServiceName(extensions);
        if (newServiceName == null) {
            throw new PEtALSCDKException("Can not create an Axis service with a null name");
        }

        try {
            // try to find if the axisService has already been registered
            final AxisConfiguration axisConfig = this.soapContext.getAxis2ConfigurationContext()
                    .getAxisConfiguration();
            if (axisConfig.getService(newServiceName) != null) {
                // The Axis service already exists
                this.logger.log(Level.WARNING, "The service '" + newServiceName
                        + "' is already registered in Axis, you can not register it twice");

            } else {

                // The Axis service does not exist, we create it.
                final AxisService axisService = new PetalsAxisService(newServiceName,
                        this.componentContext, consumes, extensions, this.soapServerConfig
                                .getServicesURL(), this.petalsReceiver, this.logger);

                // populate service with service descriptor
                final QName jbiServiceQName = consumes.getServiceName();
                if (jbiServiceQName != null) {
                    axisService.setTargetNamespace(jbiServiceQName.getNamespaceURI());
                }
                axisService.addParameter(new Parameter(Constants.SERVICE_CLASS, "PetalsReceiver"));
                axisService.addParameter(new Parameter(
                        Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS, Boolean.TRUE));

                this.setServiceParametersToAxisService(axisService, consumes, serviceDescriptor);
                this.setModulesToAxisService(axisService, consumes);

                // We set the transport layers
                axisService.setEnableAllTransports(false);
                boolean httpOn = this.setTransportHttpToAxisService(axisService, extensions);
                boolean jmsOn = this.setTransportJmsToAxisService(axisService, extensions);

                if (httpOn) {
                    serviceURLs.add(this.soapServerConfig.getServicesURL() + "/" + newServiceName);
                }

                // axisConfig.addService(axisService);
            }
        } catch (final AxisFault e) {
            throw new PEtALSCDKException("Can not register Service into Axis context", e);
        }

        return serviceURLs;
    }

    /**
     * Set to an Axis service the service parameters provided as CDATA from the
     * extension 'service-parameters' or in the embedded file 'services.xml'.
     * 
     * @param axisService
     * @param consumes
     * @throws AxisFault
     */
    private void setServiceParametersToAxisService(final AxisService axisService,
            final Consumes consumes, final File serviceDescriptor) throws AxisFault {
        // we set the associated service parameters
        final String serviceParameters = this.soapContext.getConsumersManager()
                .getServiceParameters(consumes);
        if (serviceParameters != null) {
            try {
                final OMElement parametersElements = this.buildParametersOM(serviceParameters);

                // get an iterator on all <parameter> children
                final Iterator<OMElement> itr = parametersElements.getChildrenWithName(new QName(
                        DeploymentConstants.TAG_PARAMETER));

                // iterate on parameters and set them to the associated
                // axisService
                while (itr.hasNext()) {
                    final OMElement parameterElement = itr.next();

                    if (DeploymentConstants.TAG_PARAMETER.equalsIgnoreCase(parameterElement
                            .getLocalName())) {
                        axisService.addParameter(this.getParameter(parameterElement));
                    }
                }
            } catch (final XMLStreamException e) {
                this.logger.warning(e.getMessage());
            } catch (final DeploymentException e) {
                this.logger.warning(e.getMessage());
            }
        }

        // Try to load axis configuration from services.xml
        if (serviceDescriptor != null) {
            try {
                final FileInputStream fis = new FileInputStream(serviceDescriptor);
                final ServiceBuilder builder = new ServiceBuilder(fis, this.soapContext
                        .getAxis2ConfigurationContext(), axisService);
                builder.populateService(builder.buildOM());
            } catch (final XMLStreamException e) {
                this.logger.warning(e.getMessage());
            } catch (final FileNotFoundException e) {
                this.logger.warning(e.getMessage());
            } catch (final DeploymentException e) {
                this.logger.warning(e.getMessage());
            }
        }
    }

    /**
     * Set and engage needed module to an Axis service
     * 
     * @param axisService
     * @param consumes
     * @throws DeploymentException
     * @throws AxisFault
     */
    private void setModulesToAxisService(final AxisService axisService, final Consumes consumes)
            throws DeploymentException, AxisFault {
        // We prepare a list for the modules ClassLoaders
        List<ClassLoader> suModulesClassLoaders = null;

        /*
         * Add the modules for this new service. If the services.xml file if
         * available, the modules references can be redefined
         */
        final List<String> modules = this.soapContext.getConsumersManager().getModules(consumes);
        if (modules != null) {
            suModulesClassLoaders = new ArrayList<ClassLoader>();
            final AxisConfiguration axisConfig = this.soapContext.getAxis2ConfigurationContext()
                    .getAxisConfiguration();
            for (final String module : modules) {
                final AxisModule axisModule = axisConfig.getModule(module);
                if (axisModule == null) {
                    throw new DeploymentException(Messages.getMessage(
                            DeploymentErrorMsgs.MODULE_NOT_FOUND, module));
                }
                axisService.addModuleref(axisModule.getName());

                // We add the current module ClassLoader to the list
                suModulesClassLoaders.add(axisModule.getModuleClassLoader());
            }

            final String suRootPath = this.getSUDataHandlerForConsumes(consumes).getInstallRoot();

            // We set the service ClassLoader = initial ClassLoader
            // + jars of suRootPath + modules ClassLoaders
            axisService.setClassLoader(new SoapSUClassLoader(ClassLoaderUtil.getUrls(suRootPath),
                    suModulesClassLoaders, axisService.getClassLoader()));

            // Add the service before to engaging it otherwise a NPE occur
            this.soapContext.getAxis2ConfigurationContext().getAxisConfiguration().addService(
                    axisService);

            // engage the modules if needed
            for (final String module : modules) {
                final AxisModule axisModule = axisConfig.getModule(module);
                if (!axisService.isEngaged(axisModule)) {
                    axisService.engageModule(axisModule, axisService);
                }
            }
        }
    }

    /**
     * Set an Axis service as a SOAP over HTTP service.
     * 
     * @param axisService
     */
    private boolean setTransportHttpToAxisService(final AxisService axisService,
            final ConfigurationExtensions extensions) {

        boolean result = false;

        if (SUPropertiesHelper.isHttpTransportEnable(extensions)) {
            axisService.addExposedTransport(Constants.TRANSPORT_HTTP);
            result = true;

            this.logger.log(Level.INFO, "The Axis2 service '" + axisService.getName()
                    + "' has been registered and is available at '"
                    + this.soapServerConfig.getServicesURL() + "/" + axisService.getName() + "'");
        }
        return result;
    }

    /**
     * Set an Axis service as a SOAP over JMS service.
     * 
     * @param axisService
     * @throws AxisFault
     */
    private boolean setTransportJmsToAxisService(final AxisService axisService,
            final ConfigurationExtensions extensions) throws AxisFault {
        boolean result = false;
        if (SUPropertiesHelper.isJmsTransportEnable(extensions)) {

            axisService.addParameter(new Parameter(JMSConstants.DEST_PARAM, axisService.getName()));

            axisService.addExposedTransport(Constants.TRANSPORT_JMS);
            result = true;

            this.logger.log(Level.INFO, "The Axis2 service '" + axisService.getName()
                    + "' has been registered and is available through JMS.");
        }
        return result;
    }

    /**
     * Process the parameterElement object from the OM, and returns the
     * corresponding Parameter.
     * 
     * @param parameterElement
     *            <code>OMElement</code>
     * @return the Parameter parsed
     * @throws DeploymentException
     *             if bad paramName
     */
    private Parameter getParameter(final OMElement parameterElement) throws DeploymentException {

        final Parameter parameter = new Parameter();
        // setting parameterElement
        parameter.setParameterElement(parameterElement);
        // setting parameter Name
        final OMAttribute paramName = parameterElement.getAttribute(new QName(
                DeploymentConstants.ATTRIBUTE_NAME));
        if (paramName == null) {
            throw new DeploymentException(Messages.getMessage(
                    DeploymentErrorMsgs.BAD_PARAMETER_ARGUMENT, parameterElement.toString()));
        }
        parameter.setName(paramName.getAttributeValue());
        // setting parameter Value (the child element of the parameter)
        final OMElement paramValue = parameterElement.getFirstElement();
        if (paramValue != null) {
            parameter.setValue(paramValue);
            parameter.setParameterType(Parameter.OM_PARAMETER);
        } else {
            final String paratextValue = parameterElement.getText();

            parameter.setValue(paratextValue);
            parameter.setParameterType(Parameter.TEXT_PARAMETER);
        }

        return parameter;
    }

    /**
     * Creates the OMElement corresponding to the parameters String, included in
     * parameters tags.
     * 
     * @param parameters
     *            the parameters
     * @return Returns <code>OMElement</code> .
     * @throws javax.xml.stream.XMLStreamException
     * 
     */
    private final OMElement buildParametersOM(String parameters) throws XMLStreamException {
        if (parameters != null) {
            parameters = "<parameters>" + parameters + "</parameters>";
        }
        final OMElement element = (OMElement) XMLUtils.toOM(new StringReader(parameters));
        element.build();
        return element;
    }

    /**
     * Unregister the service from Axis.
     * 
     * @param serviceQName
     * @throws PEtALSComponentSDKException
     */
    private void unregisterAxisService(final ConfigurationExtensions extensions)
            throws PEtALSCDKException {

        final String endPointName = SUPropertiesHelper.getServiceName(extensions);

        final AxisConfiguration axisConfig = this.soapContext.getAxis2ConfigurationContext()
                .getAxisConfiguration();

        this.logger.log(Level.INFO, "Removing Axis service '" + endPointName + "'");

        try {
            // register an axis service to axis engine
            final AxisService axisService = axisConfig.getService(endPointName);

            // Feature request : #306664
            // FIXME : We do not have to remove the service group. It is
            // temporary until the next version of Axis2 where the removeService
            // method will fix it.
            if (axisService != null) {
                axisConfig.removeServiceGroup(endPointName);
                axisService.getAxisConfiguration().removeService(endPointName);
            } else {
                this.logger.log(Level.WARNING, "Service '" + endPointName
                        + "' not found, can not be unregistered from Axis2");
            }
        } catch (final AxisFault e) {
            throw new PEtALSCDKException("Can not remove service from Axis context", e);
        }
    }

    /**
     * Get the the policy path
     * 
     * @param suRootPath
     * @param extensions
     */
    private File getPolicyPath(final String suRootPath, final ConfigurationExtensions extensions) {
        final String policyPath = extensions.get(POLICY_PATH);
        File result = null;

        if (policyPath == null) {
            return result;
        }

        File path = new File(suRootPath, policyPath);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Setting policy path to " + path.getAbsolutePath());
        }

        if (path.exists() && path.isDirectory()) {
            result = path;
        } else {
            this.logger.warning("The policy path does not exists, it will be ignored "
                    + path.getAbsolutePath());
        }
        return result;
    }

    /**
     * Get the modules
     * 
     * @param provides
     */
    private List<String> getModules(final ConfigurationExtensions extensions) {
        List<String> result = new ArrayList<String>();

        // get modules from extension
        final String token = extensions.get(MODULES);

        if (token != null) {
            // get individual modules values
            final StringTokenizer st = new StringTokenizer(token, ",");
            result.add(Constants.MODULE_ADDRESSING);
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
        }
        return result;
    }

    /**
     * Get the service class loader
     * 
     * @param suRootPath
     * @param extensions
     */
    private URLClassLoader getServiceClassloader(final String suRootPath,
            final ConfigurationExtensions extensions) {
        return ClassLoaderUtil.createClassLoader(suRootPath, Thread.currentThread()
                .getContextClassLoader());
    }

    /**
     * Clean the context
     * 
     * @param serviceUnitName
     * @param descriptor
     */
    private void cleanServiceContexts(final String serviceUnitName, final Jbi descriptor) {
        this.soapContext.removeJbiDescriptor(serviceUnitName);
        this.soapContext.removeServiceDescriptor(serviceUnitName);

        final List<Provides> providesList = descriptor.getServices().getProvides();
        ServiceUnitDataHandler suDatahandler = null;

        for (final Provides provides : providesList) {
            if (suDatahandler == null) {
                suDatahandler = this.getSUDataHandlerForService(provides);
            }

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Cleaning context for " + provides);
            }
            this.soapContext.getProvidersManager().deleteServiceContext(provides);
        }

        final List<Consumes> consumesList = descriptor.getServices().getConsumes();
        for (final Consumes consumes : consumesList) {
            if (suDatahandler == null) {
                suDatahandler = this.getSUDataHandlerForService(consumes);
            }

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Cleaning context for " + consumes);
            }
            this.soapContext.getConsumersManager().deleteServiceContext(consumes);
        }
    }

    /**
     * Add parameters for the associated service to the componentContext.
     * 
     * @param extensions
     */
    private String getServiceParameters(final ConfigurationExtensions extensions) {
        return extensions.get(SERVICE_PARAMETERS);
    }

    /**
     * Create a new topic on SU deployment
     * 
     * @param topicName
     */
    protected void createTopic(final ConfigurationExtensions extensions) {

        final String name = SUPropertiesHelper.getTopicName(extensions);
        final WsnManager notificationManager = this.soapContext.getWsnManager();

        if (SUPropertiesHelper.isTopicMode(extensions)) {
            Topic topic = null;
            this.logger.info("Creating new topic '" + name + "'");
            final QName topicName = QName.valueOf(name);
            try {
                // add topic
                topic = notificationManager.addTopic(topicName);

            } catch (final WsnFault e) {
                this.logger.warning("Can not create topic : " + e.getMessage());
            }

            if (topic != null) {
                try {
                    notificationManager.reloadSubscriptions(topicName);
                } catch (final Exception e) {
                    this.logger.warning(e.getMessage());
                }
            }
        }
    }

    /**
     * Delete the topic on SU undeployment
     * 
     * @param topicName
     */
    protected void deleteTopic(final ConfigurationExtensions extensions) {
        final String name = SUPropertiesHelper.getTopicName(extensions);
        if (SUPropertiesHelper.isTopicMode(extensions)) {
            final WsnManager notificationManager = this.soapContext.getWsnManager();

            final QName topicName = QName.valueOf(name);
            this.logger.info("Deleting topic '" + topicName + "'");
            final Topic topic = notificationManager.getTopic(topicName);
            if (topic != null) {
                try {
                    notificationManager.deleteTopic(topicName);
                } catch (final WsnFault e) {
                    this.logger.warning("Fault while deleting topic : " + e.getMessage());
                }
            }
        }
    }

    /**
     * Unregister a topic
     * 
     * @param provides
     */
    protected void unregisterTopic(final ConfigurationExtensions extensions) {
        final String name = SUPropertiesHelper.getTopicName(extensions);
        final String mode = SUPropertiesHelper.getMode(extensions);

        if ("TOPIC".equals(mode)) {
            this.logger.info("Unregister topic " + name);
            final WsnManager notificationManager = this.soapContext.getWsnManager();
            final QName topicName = QName.valueOf(name);
            notificationManager.cleanTopic(topicName);
        }
    }

    /**
     * Check and display warnings for Provide parameters
     * 
     * @param extensions
     */
    protected void checkProvide(ConfigurationExtensions extensions) {
        // just check the address field
        if ((SUPropertiesHelper.getAddress(extensions) != null)
                && (SUPropertiesHelper.getWSATo(extensions) == null)) {
            this.logger.warning("The field 'address' is deprecated, please use 'wsa-to'");
        }
    }

    /**
     * Check and display warning for Consume parameters
     * 
     * @param extensions
     */
    protected void checkConsume(ConfigurationExtensions extensions) {
        // just check the service name parameter
        if ((SUPropertiesHelper.getAddress(extensions) != null)
                && (SUPropertiesHelper.getServiceName(extensions) == null)) {
            this.logger.warning("The field 'address' is deprecated, please use 'service-name'");
        }
    }

    /**
     * @param restService
     */
    private void addToExpose(String serviceURL) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> exposed = this.getComponentInformation().getExposedServices();
        if (exposed != null) {
            exposed.add(serviceURL);
        }
    }

    private void removeFromExpose(String serviceURL) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> exposed = this.getComponentInformation().getExposedServices();
        if (exposed != null) {
            exposed.remove(serviceURL);
        }
    }

    private void addToConsume(String serviceURL) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> services = this.getComponentInformation().getConsumedServices();
        if (services != null) {
            services.add(serviceURL);
        }
    }

    private void removeFromConsume(String serviceURL) {
        if (this.getComponentInformation() == null) {
            return;
        }
        Set<String> services = this.getComponentInformation().getConsumedServices();
        if (services != null) {
            services.remove(serviceURL);
        }
    }

    public ComponentInformation getComponentInformation() {
        return ((PetalsBindingComponent) this.component).getPlugin(ComponentInformation.class);
    }

}
