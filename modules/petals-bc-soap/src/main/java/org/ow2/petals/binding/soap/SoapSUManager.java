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
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.jms.JMSConstants;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.petals.binding.soap.listener.incoming.PetalsReceiver;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soap.listener.incoming.jetty.AxisServletServer;
import org.ow2.petals.binding.soap.util.ComponentPropertiesHelper;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.bc.AbstractBindingComponent;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.su.BindingComponentServiceUnitManager;
import org.ow2.petals.component.framework.su.ServiceUnitDataHandler;
import org.ow2.petals.component.framework.util.ClassLoaderUtil;
import org.w3c.dom.Document;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.COMPONENT_CONTEXT_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_EXTENSIONS_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.LOGGER_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.PETALS_RECEIVER_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.WSDL_FOUND_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ADDRESS;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.SERVICE_NAME;

/**
 * A service unit listener used to register new service into Axis Engine during
 * SU deployment.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
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

    private SoapComponent soapComponent;

    /**
     * JDK logger
     */
    private Logger logger = null;

    private SoapServerConfig soapServerConfig;

    private PetalsReceiver petalsReceiver;

    public SoapSUManager(final AbstractBindingComponent bindingComponent) {
        super(bindingComponent);
        soapComponent = (SoapComponent) bindingComponent;
    }

    /**
     * Check and display warning for Consume parameters
     * 
     * @param extensions
     */
    protected void checkConsume(final ConfigurationExtensions extensions) {
        // just check the service name parameter
        if (SUPropertiesHelper.getServiceName(extensions) == null) {
            // allow address
            if (SUPropertiesHelper.getAddress(extensions) == null) {
                logger.warning("The field " + SERVICE_NAME + " can't be found into the consumes");
            } else {
                logger.info("The field " + ADDRESS + " is deprecated, please use " + SERVICE_NAME);
            }
        }
    }

    /**
     * Check and display warnings for Provide parameters
     * 
     * @param extensions
     */
    protected void checkProvide(final ConfigurationExtensions extensions) {
        // just check the address field
        // allow address
        if (SUPropertiesHelper.getWSATo(extensions) == null
                && SUPropertiesHelper.getAddress(extensions) == null) {
            if (logger.isLoggable(Level.INFO)) {
                logger
                        .info("The field 'wsa-to' isn't specified into the extensions. We'll search the WS-Addressing into the exchanges.");
            }
        }
    }

    /**
     * Clean the context
     * 
     * @param serviceUnitName
     * @param descriptor
     */
    private void cleanServiceContexts(final String serviceUnitName, final Jbi descriptor) {
        soapContext.removeJbiDescriptor(serviceUnitName);
        soapContext.removeServiceDescriptor(serviceUnitName);

        final List<Provides> providesList = descriptor.getServices().getProvides();
        ServiceUnitDataHandler suDatahandler = null;

        for (final Provides provides : providesList) {
            if (suDatahandler == null) {
                suDatahandler = getSUDataHandlerForService(provides);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Cleaning context for " + provides);
            }
            soapContext.getProvidersManager().deleteServiceContext(provides);
            soapContext.deleteServiceClientPools(provides);
        }

        final List<Consumes> consumesList = descriptor.getServices().getConsumes();
        for (final Consumes consumes : consumesList) {
            if (suDatahandler == null) {
                suDatahandler = getSUDataHandlerForService(consumes);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Cleaning context for " + consumes);
            }
            soapContext.getConsumersManager().deleteServiceContext(consumes);
        }
    }

    /**
     * Create the Axis service that will handle incoming SOAP calls whatever the
     * transport layer.
     * 
     * @param consumes
     * @param serviceDescriptor
     * @throws PEtALSComponentSDKException
     */
    private void createAxisService(final Consumes consumes, final File serviceDescriptor,
            final ConfigurationExtensions extensions) throws PEtALSCDKException {
        // Get the created service name
        String newServiceName = SUPropertiesHelper.getServiceName(extensions);
        // allow address
        if (newServiceName == null) {
            newServiceName = SUPropertiesHelper.getAddress(extensions);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("The deprecated address attribute is used. Service name: "
                        + newServiceName);
            }
        } else if (logger.isLoggable(Level.FINE)) {
            logger.fine("The service name attribute is used. Service name: " + newServiceName);
        }
        try {
            // try to find if the axisService has already been registered
            ConfigurationContext axisConfigContext = soapContext.getAxis2ConfigurationContext();
            final AxisConfiguration axisConfig = axisConfigContext.getAxisConfiguration();
            if (axisConfig.getService(newServiceName) != null) {
                // The Axis service already exists
                if (logger.isLoggable(Level.WARNING)) {
                    this.logger.log(Level.WARNING, "The service '" + newServiceName
                        + "' is already registered in Axis, you cannot register it again");
                }
            } else {
                // The Axis service does not exist, we create it.
                final AxisService axisService = new AxisService(newServiceName);

                // Add parameters for the first request dispatcher (to get the WSDL)
                Parameter wsdlFoundParam = new Parameter(WSDL_FOUND_SERVICE_PARAM, false);
                axisService.addParameter(wsdlFoundParam);
                Parameter consumesConfigParam = new Parameter(CONSUMES_SERVICE_PARAM, consumes);
                axisService.addParameter(consumesConfigParam);
                Parameter consumesExtensionsConfigParam = new Parameter(CONSUMES_EXTENSIONS_SERVICE_PARAM, extensions);
                axisService.addParameter(consumesExtensionsConfigParam);
                Parameter componentContextParam = new Parameter(COMPONENT_CONTEXT_SERVICE_PARAM, this.componentContext);
                axisService.addParameter(componentContextParam);
                Parameter loggerParam = new Parameter(LOGGER_SERVICE_PARAM, this.logger);
                axisService.addParameter(loggerParam);
                Parameter petalsReceiverParam = new Parameter(PETALS_RECEIVER_SERVICE_PARAM, this.petalsReceiver);
                axisService.addParameter(petalsReceiverParam);
                
                // set the transport layers
                // (necessary to set the transport before adding the Axis
                // service to Axis configuration)
                axisService.setEnableAllTransports(false);
                setTransportHttpsToAxisService(axisService,
                        this.component.getComponentExtensions(), extensions);
                setTransportHttpToAxisService(axisService, extensions);
                setTransportJmsToAxisService(axisService, extensions);

                // populate service with service descriptor
                final QName jbiServiceQName = consumes.getServiceName();
                if (jbiServiceQName != null) {
                    axisService.setTargetNamespace(jbiServiceQName.getNamespaceURI());
                }

                // disable WS-Addressing
                axisService.addParameter(
                                    org.apache.axis2.addressing.AddressingConstants.DISABLE_ADDRESSING_FOR_IN_MESSAGES,
                                    Boolean.TRUE.toString());
                
                axisService.addParameter(new Parameter(
                        Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS, Boolean.TRUE));

                setServiceParametersToAxisService(logger, soapContext, consumes, axisService);
//                setModulesToAxisService(axisService, consumes);

                final String suRootPath = getSUDataHandlerForConsumes(consumes).getInstallRoot();
                axisService.setClassLoader(new URLClassLoader(ClassLoaderUtil.getUrls(suRootPath), axisService.getClassLoader()));
        
                // Add the service
                axisConfig.addService(axisService);
            }
        } catch (final AxisFault e) {
            throw new PEtALSCDKException("Can not register Service into Axis context", e);
        }
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
                suDatahandler = getSUDataHandlerForService(provides);
            }

            final ServiceContext<Provides> context = soapContext.getProvidersManager()
                    .createServiceContext(provides);
            final ConfigurationExtensions extensions = suDatahandler
                    .getConfigurationExtensions(provides);
            context.setModules(SUPropertiesHelper.getModules(extensions));
            context.setServiceParams(SUPropertiesHelper.getServiceParameters(extensions));
            context.setClassloader(getServiceClassloader(suRootPath));
        }

        final List<Consumes> consumesList = descriptor.getServices().getConsumes();
        for (final Consumes consumes : consumesList) {
            if (suDatahandler == null) {
                suDatahandler = getSUDataHandlerForService(consumes);
            }

            final ServiceContext<Consumes> context = soapContext.getConsumersManager()
                    .createServiceContext(consumes);
            final ConfigurationExtensions extensions = suDatahandler
                    .getConfigurationExtensions(consumes);
            context.setModules(SUPropertiesHelper.getModules(extensions));
            context.setServiceParams(SUPropertiesHelper.getServiceParameters(extensions));
            context.setClassloader(getServiceClassloader(suRootPath));
        }
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
    protected void doDeploy(final String serviceUnitName, final String suRootPath,
            final Jbi jbiDescriptor) throws PEtALSCDKException {
        logger.log(Level.FINE, "Deploying a new AxisService for SU " + serviceUnitName);

        // Global service unit configuration
        soapContext.addJbiDescriptor(serviceUnitName, jbiDescriptor);

        // Local service configuration
        createServicesContext(serviceUnitName, suRootPath, jbiDescriptor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.su.AbstractServiceUnitManager#doStart
     * (java.lang.String)
     */
    @Override
    protected void doStart(final String serviceUnitName) throws PEtALSCDKException {
        // get descriptors
        final Jbi descriptor = soapContext.getJbiDescriptor(serviceUnitName);
        final File serviceDescription = soapContext.getServiceDescriptor(serviceUnitName);

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
                    suDatahandler = getSUDataHandlerForService(consumes);
                }
                final ConfigurationExtensions extensions = suDatahandler
                        .getConfigurationExtensions(consumes);
                checkConsume(extensions);
                createAxisService(consumes, serviceDescription, extensions);

                String redirect = SUPropertiesHelper.getHttpRedirection(extensions);
                if (redirect != null) {
                    AxisServletServer httpServer = soapComponent.getExternalListenerManager()
                            .getHttpServer();
                    String serviceName = SUPropertiesHelper.getServiceName(extensions);
                    if (serviceName == null) {
                        serviceName = SUPropertiesHelper.getAddress(extensions);
                    }
                    // Comma-separated list of redirection aliases
                    StringTokenizer st = new StringTokenizer(redirect, ",;|");
                    while (st.hasMoreTokens()) {
                        httpServer.addRedirect(st.nextToken().trim(), serviceName);
                    }
                }
            }
            // create/restore services
            final List<Provides> providesList = descriptor.getServices().getProvides();
            for (final Provides provides : providesList) {
                if (suDatahandler == null) {
                    suDatahandler = getSUDataHandlerForService(provides);
                }
                final ConfigurationExtensions extensions = suDatahandler
                        .getConfigurationExtensions(provides);
                checkProvide(extensions);
                final ServiceContext<Provides> context = soapContext.getProvidersManager()
                        .getServiceContext(provides);
                final ServiceEndpoint srvEp = componentContext.getEndpoint(provides
                        .getServiceName(), provides.getEndpointName());
                
                try {
                    Document doc = componentContext.getEndpointDescriptor(srvEp);
                    context.setServiceDescription(WSDL4ComplexWsdlFactory.newInstance()
                            .newWSDLReader().read(doc));
                } catch (final JBIException e) {
                    logger.warning("No endpoint descriptor found for the service endpoint : "
                            + srvEp);
                } catch (final WSDL4ComplexWsdlException e) {
                    logger.warning("Wsdl reading error" + e.getMessage());
                } catch (final URISyntaxException e) {
                    logger.warning("Wsdl reading error" + e.getMessage());
                }
            }
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
    protected void doStop(final String serviceUnitName) throws PEtALSCDKException {

        final Jbi descriptor = soapContext.getJbiDescriptor(serviceUnitName);
        if (descriptor != null) {
            ServiceUnitDataHandler suDatahandler = null;

            // delete registered axis services
            final List<Consumes> consumesList = descriptor.getServices().getConsumes();
            for (final Consumes consumes : consumesList) {
                if (suDatahandler == null) {
                    suDatahandler = getSUDataHandlerForService(consumes);
                }
                final ConfigurationExtensions extensions = suDatahandler
                        .getConfigurationExtensions(consumes);
                unregisterAxisService(extensions);

                String redirect = extensions.get("http-services-redirection");
                if (redirect != null) {
                    // Comma-separated list of redirection aliases
                    StringTokenizer st = new StringTokenizer(redirect, ",;|");
                    while (st.hasMoreTokens()) {
                        soapComponent.getExternalListenerManager().getHttpServer().removeRedirect(
                                st.nextToken().trim());
                    }
                }
            }
        }
    }

    @Override
    protected void doUndeploy(final String serviceUnitName) throws PEtALSCDKException {
        final Jbi descriptor = getServiceUnitDataHandlers().get(serviceUnitName).getDescriptor();
        cleanServiceContexts(serviceUnitName, descriptor);
    }

    /**
     * Get the service class loader
     * 
     * @param suRootPath
     * @param extensions
     */
    private URLClassLoader getServiceClassloader(final String suRootPath) {
        return ClassLoaderUtil.createClassLoader(suRootPath, Thread.currentThread()
                .getContextClassLoader());
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

//    /**
//     * Set and engage needed module to an Axis service
//     * 
//     * @param axisService
//     * @param consumes
//     * @throws DeploymentException
//     * @throws AxisFault
//     */
//    private void setModulesToAxisService(final AxisService axisService, final Consumes consumes)
//            throws DeploymentException, AxisFault {
//        /*
//         * Add the modules for this new service.
//         */
//        final List<String> modules = soapContext.getConsumersManager().getModules(consumes);
//        if (modules != null) {
//            final AxisConfiguration axisConfig = soapContext.getAxis2ConfigurationContext()
//                    .getAxisConfiguration();
//
//            final String suRootPath = getSUDataHandlerForConsumes(consumes).getInstallRoot();
//            axisService.setClassLoader(new URLClassLoader(ClassLoaderUtil.getUrls(suRootPath), axisService.getClassLoader()));
//            
//            // engage the modules if needed
//            for (final String module : modules) {
//                final AxisModule axisModule = axisConfig.getModule(module);
//                if (!axisService.isEngaged(axisModule)) {
//                    axisService.engageModule(axisModule, axisService);
//                }
//            }
//            
//            // Add the service
//            soapContext.getAxis2ConfigurationContext().getAxisConfiguration().addService(
//                    axisService);
//        }
//    }

    /**
     * Set to an Axis service the service parameters provided as CDATA from the
     * extension 'service-parameters'.
     */
    @SuppressWarnings("unchecked")
    private static final void setServiceParametersToAxisService(final Logger logger,
            final SoapComponentContext soapContext, Consumes consumes, final AxisService axisService)
            throws AxisFault {

        // Set the associated service parameters
        try 
        {
            soapContext.getConsumersManager().addServiceParameters(consumes, axisService);       
        } catch (final XMLStreamException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.getMessage());
            }
        } catch (final DeploymentException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.getMessage());
            }
        }
    }

    /**
     * Set an Axis service as a SOAP over HTTPS service.
     * 
     * @param axisService
     */
    private void setTransportHttpsToAxisService(final AxisService axisService,
            final ConfigurationExtensions componentExtensions,
            final ConfigurationExtensions suExtensions) {

        if (ComponentPropertiesHelper.isHttpsEnabled(componentExtensions)
                && SUPropertiesHelper.isHttpsTransportEnabled(suExtensions)) {
            axisService.addExposedTransport(Constants.TRANSPORT_HTTPS);

            logger.log(Level.INFO, "The Axis2 service '"
                    + axisService.getName()
                    + "' has been registered and is available at '"
                    + soapServerConfig.getServiceURL(axisService.getName(),
                            Constants.TRANSPORT_HTTPS) + "'");
        }
    }

    /**
     * Set an Axis service as a SOAP over HTTP service.
     * 
     * @param axisService
     */
    private void setTransportHttpToAxisService(final AxisService axisService,
            final ConfigurationExtensions extensions) {

        if (SUPropertiesHelper.isHttpTransportEnabled(extensions)) {
            axisService.addExposedTransport(Constants.TRANSPORT_HTTP);

            logger.log(Level.INFO, "The Axis2 service '"
                    + axisService.getName()
                    + "' has been registered and is available at '"
                    + soapServerConfig.getServiceURL(axisService.getName(),
                            Constants.TRANSPORT_HTTP) + "'");
        }
    }

    /**
     * Set an Axis service as a SOAP over JMS service.
     * 
     * @param axisService
     * @throws AxisFault
     */
    private void setTransportJmsToAxisService(final AxisService axisService,
            final ConfigurationExtensions extensions) throws AxisFault {

        if (SUPropertiesHelper.isJmsTransportEnabled(extensions)) {

            axisService.addParameter(new Parameter(JMSConstants.PARAM_DESTINATION, axisService.getName()));

            axisService.addExposedTransport(Constants.TRANSPORT_JMS);

            logger.log(Level.INFO, "The Axis2 service '" + axisService.getName()
                    + "' has been registered and is available through JMS.");
        }
    }

    /**
     * Unregister the service from Axis.
     * 
     * @param serviceQName
     * @throws PEtALSComponentSDKException
     */
    private void unregisterAxisService(final ConfigurationExtensions extensions)
            throws PEtALSCDKException {
        String serviceName = SUPropertiesHelper.getServiceName(extensions);
        if (serviceName == null) {
            serviceName = SUPropertiesHelper.getAddress(extensions);
        }
        final AxisConfiguration axisConfig = soapContext.getAxis2ConfigurationContext()
                .getAxisConfiguration();
        logger.log(Level.INFO, "Removing Axis service '" + serviceName + "'");
        try {
            // register an axis service to axis engine
            final AxisService axisService = axisConfig.getService(serviceName);
            // Feature request : #306664
            // FIXME : We do not have to remove the service group. It is
            // temporary until the next version of Axis2 where the removeService
            // method will fix it.
            if (axisService != null) {
                axisConfig.removeServiceGroup(serviceName);
                axisService.getAxisConfiguration().removeService(serviceName);
            } else {
                logger.log(Level.WARNING, "Service '" + serviceName
                        + "' not found, can not be unregistered from Axis2");
            }
        } catch (final AxisFault e) {
            throw new PEtALSCDKException("Can not remove service from Axis context", e);
        }
    }
}
