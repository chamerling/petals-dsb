/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 OW2 consortium,
 * http://www.ow2.org/
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

package org.ow2.petals.binding.soap.listener.outgoing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.RobustOutOnlyAxisOperation;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.neethi.All;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.handler.WSSHandlerConstants;
import org.apache.rampart.policy.model.RampartConfig;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.binding.soap.listener.incoming.PetalsAxisService;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.util.UtilFactory;

/**
 * Get new Axis2 ServiceClient from the pool factory
 * 
 * @author Christophe DENEUX - Cap Gemini
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class ServiceClientPoolObjectFactory extends BasePoolableObjectFactory {

    private final String address;

    private final QName operation;

    private final URI mep;

    private final ConfigurationExtensions extensions;

    private final SoapComponentContext soapContext;

    private final Provides provides;

    private final Logger logger;

    private final String soapAction;
    
    private final String rampartUserName;

    /**
     * Creates a new instance of ServiceClientPoolObjectFactory
     * 
     * @param operation
     * @param mep
     * @param logger
     * @param soapAction
     * @param rampartUserName 
     * @param wsdlDescription
     * @param cdkExtensions
     * @param modules
     */
    public ServiceClientPoolObjectFactory(final String address, final QName operation,
            final URI mep, final ConfigurationExtensions extensions,
            final SoapComponentContext context, final Provides provides, final Logger logger,
            String soapAction, String rampartUserName) {
        this.address = address;
        this.operation = operation;
        this.soapAction = soapAction;
        this.mep = mep;
        this.extensions = extensions;
        this.soapContext = context;
        this.provides = provides;
        this.logger = logger;
        this.rampartUserName = rampartUserName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    @Override
    public Object makeObject() throws Exception {

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Creating a service client for : " + address + ", with operation '"
                    + this.operation + "', and MEP '" + this.mep + "'");
        }

        final AxisService service = new AxisService(
                PetalsAxisService.OUTGOING_SERVICE_CLIENT_PREFIX
                + UtilFactory.getIdUtil().createId());
        if (provides != null
                && soapContext.getProvidersManager().getServiceContext(provides) != null) {
            service.setClassLoader(soapContext.getProvidersManager().getServiceContext(provides)
                    .getClassloader());
        }

        final AxisOperation axisOperation;
        if (MEPConstants.IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new OutOnlyAxisOperation(operation);
        } else if (MEPConstants.ROBUST_IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new RobustOutOnlyAxisOperation(operation);
        } else if (MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep)
                || MEPConstants.IN_OUT_PATTERN.equals(mep)) {
            axisOperation = new OutInAxisOperation(operation);
        } else {
            axisOperation = null;
        }
        service.addOperation(axisOperation);

        // create Options for the stub
        final Options options = ServiceClientPoolObjectFactory.createOptions(address,
                this.soapAction, extensions);
        boolean policy = SUPropertiesHelper.isPolicyEnabled(this.extensions);
        Policy p = null;
        if (policy) {
            p = Axis2Utils.loadPolicy(this.soapContext.getProvidersManager().getServiceContext(provides)
                    .getPolicyPath(), this.logger);
            if (p != null) {
                if (this.rampartUserName != null && p.getAssertions() != null && p.getAssertions().get(0) instanceof ExactlyOne) {
                    ExactlyOne exactlyOne = (ExactlyOne) p.getAssertions().get(0);
                    if (exactlyOne.getPolicyComponents() != null 
                            && exactlyOne.getPolicyComponents().get(0) instanceof All) {
                        List<?> assertions = ((All)exactlyOne.getPolicyComponents().get(0)).getAssertions();
                        if (assertions != null) {
                            for (Object assertion : assertions) {
                                if (assertion instanceof RampartConfig) {
                                    // Override the rampart user defined in the policy.xml file
                                    RampartConfig rampartConfig = (RampartConfig) assertion;
                                    rampartConfig.setUser(this.rampartUserName);
                                    
                                }
                            }
                        }
                    }
                    options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, p);
                }
            }
        }

        try {
            final ServiceClient serviceClient = new ServiceClient(soapContext
                    .getAxis2ConfigurationContext(), service);
            serviceClient.setOptions(options);
            if (p != null) {
                // Do not know why but set it here too...
                serviceClient.getServiceContext().setProperty(
                        RampartMessageData.KEY_RAMPART_POLICY, p);
            }

            // engage the Axis2 modules
            this.engageModules(serviceClient);

            // policy needs the rampart module to be engaged, so engage it if
            // not already done
            if (policy
                    && !serviceClient.getAxisService().isEngaged(
                            WSSHandlerConstants.SECURITY_MODULE_NAME)) {
                this.engageModule(serviceClient, WSSHandlerConstants.SECURITY_MODULE_NAME);
            }

            // TODO: Implement enableSSLSupport
            this.enableSSLSupport(this.extensions);

            return serviceClient;
        } catch (final AxisFault e) {
            throw new MessagingException("Can't create ServiceClient", e);
        }
    }

    /**
     * Create the SOAP options of the outgoing SOAP message
     * 
     * @param address
     * @param operation
     * @param extensions
     * @return
     */
    private static Options createOptions(final String address, final String soapAction,
            final ConfigurationExtensions extensions) {

        final Options options = new Options();

        if (SUPropertiesHelper.isSOAPMode(extensions)) {

            // set destination address
            options.setTo(new EndpointReference(address));

            // set the soap action
            if (soapAction != null) {
                options.setAction(soapAction);
            }

            // set the timeout
            final long timeout = SUPropertiesHelper.retrieveTimeout(extensions);
            if (timeout != -1L) {
                options.setTimeOutInMilliSeconds(timeout);
            }

            // Get the soapEnvelopeNamespaceURI version to use - optional,
            // default
            // is 1.1 version
            final String soapEnvelopeNamespaceURI = AbstractExternalServiceCaller
            .retrieveSOAPEnvelopeNamespaceURI(extensions);
            options.setSoapVersionURI(soapEnvelopeNamespaceURI);

            // set transport
            // TODO : More transports to come?
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

            // get proxy settings if they are defined in the extensions
            final HttpTransportProperties.ProxyProperties proxyProperties = AbstractExternalServiceCaller
            .retrieveProxySettings(extensions);
            if (proxyProperties != null) {
                options.setProperty(HTTPConstants.PROXY, proxyProperties);
            }

            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
                    AbstractExternalServiceCaller.retrieveChunkedMode(extensions));

            options.setCallTransportCleanup(AbstractExternalServiceCaller
                    .retrieveCleanupTransport(extensions));

        } else if (SUPropertiesHelper.isRESTMode(extensions)) {

            // TODO: Move here options from RESTCaller.java when optimizing REST
            // calls using the pool

        }
        return options;
    }

    

    /**
     * Engage the modules for the service client. The modules are available on
     * the service unit listener since they have been defined during SU
     * deployment.
     * 
     * @param serviceClient
     * @throws AxisFault
     */
    private void engageModules(final ServiceClient serviceClient) throws AxisFault {
        if (provides == null) {
            return;
        }
        
        List<String> modules = this.soapContext.getProvidersManager().getServiceContext(provides)
        .getModules();
        if (modules != null) {
            for (final String name : modules) {
                this.engageModule(serviceClient, name);
            }
        }
    }

    /**
     * Engage a module
     * 
     * @param serviceClient
     * @param moduleName
     * @throws AxisFault
     */
    protected void engageModule(final ServiceClient serviceClient, final String moduleName)
    throws AxisFault {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Engaging module " + moduleName);
        }
        serviceClient.engageModule(moduleName);
    }

    /**
     * TODO Enable the client to contact a service over an SSL connection. The
     * required values are in the PEtALS extensions if the SU has been
     * parametrized to enable SSL.
     * 
     * @param extensions
     */
    private void enableSSLSupport(final ConfigurationExtensions extensions) {
        if (extensions == null) {
            return;
        }
        final String trustStore = extensions.get("ssl-trustStore");
        final String trustStorePassword = extensions.get("ssl-trustStorePassword");
        final String keyStore = extensions.get("ssl-keyStore");
        final String keyStorePassword = extensions.get("ssl-keyStorePassword");

        if ((trustStore != null) && (trustStorePassword != null) && (keyStore != null)
                && (keyStorePassword != null)) {
            // TODO
        }
    }
}
