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

package org.ow2.petals.binding.soapproxy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AddressingHelper;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.i18n.Messages;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsAxisService;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsReceiver;
import org.ow2.petals.binding.soapproxy.listener.incoming.SoapServerConfig;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;

/**
 * A service unit listener used to register new service into Axis Engine during
 * SU deployment.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class ServiceCreator {

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

    public ServiceCreator() {
    }

    /**
     * Creates a new instance of {@link ServiceCreator}
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

    /**
     * 
     */
    public void createServices() {

        // proxy
        final String ENDPOINT_NAME = "SOAPPROXYEndpoint";
        final String namespaceURI = "http://petals.ow2.org";
        QName INTERFACE_NAME = new QName(namespaceURI, "SOAPPROXYInterface");
        QName SERVICE_NAME = new QName(namespaceURI, "SOAPPROXYService");
        // context.setModules(this.getModules(extensions));
        // context.setServiceParams(this.getServiceParameters(extensions));
        // context.setPolicyPath(this.getPolicyPath(suRootPath, extensions));
        // context.setClassloader(this.getServiceClassloader(suRootPath,
        // extensions));

        try {
            AxisService service = this.createAxisService("SOAPProxyWebService", ENDPOINT_NAME,
                    SERVICE_NAME, INTERFACE_NAME);

            // disable the addressing for this service
            // service.set
            AddressingHelper.setAddressingRequirementParemeterValue(service, Boolean.FALSE
                    .toString());
            try {
                service.addParameter(AddressingConstants.DISABLE_ADDRESSING_FOR_IN_MESSAGES,
                        Boolean.TRUE.toString());
            } catch (AxisFault e) {
                e.printStackTrace();
            }

        } catch (PEtALSCDKException e) {
            e.printStackTrace();
            this.logger.warning("Can not create the PROXY Service");
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
    private AxisService createAxisService(final String name, String endpointName,
            QName serviceName, QName interfaceName) throws PEtALSCDKException {
        AxisService result = null;
        // Get the created service name
        final String newServiceName = name;
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
                final AxisService axisService = new PetalsAxisService(newServiceName, endpointName,
                        serviceName, interfaceName, this.componentContext, this.soapServerConfig
                                .getServicesURL(), this.petalsReceiver, this.logger);

                // populate service with service descriptor
                final QName jbiServiceQName = serviceName;
                if (jbiServiceQName != null) {
                    axisService.setTargetNamespace(jbiServiceQName.getNamespaceURI());
                }
                axisService.addParameter(new Parameter(Constants.SERVICE_CLASS, "PetalsReceiver"));
                axisService.addParameter(new Parameter(
                        Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS, Boolean.TRUE));

                this.setModulesToAxisService(axisService);

                // We set the transport layers
                axisService.setEnableAllTransports(false);
                this.setTransportHttpToAxisService(axisService);
                result = axisService;

                // axisConfig.addService(axisService);
            }
        } catch (final AxisFault e) {
            throw new PEtALSCDKException("Can not register Service into Axis context", e);
        }
        return result;
    }

    /**
     * Set and engage needed module to an Axis service
     * 
     * @param axisService
     * @param consumes
     * @throws DeploymentException
     * @throws AxisFault
     */
    private void setModulesToAxisService(final AxisService axisService)
            throws DeploymentException, AxisFault {
        // We prepare a list for the modules ClassLoaders
        List<ClassLoader> suModulesClassLoaders = null;

        /*
         * Add the modules for this new service. If the services.xml file if
         * available, the modules references can be redefined
         */
        final List<String> modules = null;
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

            // Add the service before to engaging it otherwise a NPE occur
            this.soapContext.getAxis2ConfigurationContext().getAxisConfiguration().addService(
                    axisService);

            // engage the modules if needed
            for (final String module : modules) {
                final AxisModule axisModule = axisConfig.getModule(module);
                if (!axisService.isEngaged(axisModule)) {
                    this.logger.fine("Engaging module " + axisModule.getName() + " for service");
                    axisService.engageModule(axisModule, axisService);
                }
            }
        } else {
            this.soapContext.getAxis2ConfigurationContext().getAxisConfiguration().addService(
                    axisService);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Service " + axisService.getName()
                    + " got the following modules engaged : " + axisService.getModules());
        }
    }

    /**
     * Set an Axis service as a SOAP over HTTP service.
     * 
     * @param axisService
     */
    private void setTransportHttpToAxisService(final AxisService axisService) {

        axisService.addExposedTransport(Constants.TRANSPORT_HTTP);

        this.logger.log(Level.INFO, "The Axis2 service '" + axisService.getName()
                + "' has been registered and is available at '"
                + this.soapServerConfig.getServicesURL() + "/" + axisService.getName() + "'");
    }

    /**
     * Unregister the service from Axis.
     * 
     * @param serviceQName
     * @throws PEtALSComponentSDKException
     */
    private void unregisterAxisService(final String serviceName) throws PEtALSCDKException {

        final String endPointName = serviceName;

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

}
