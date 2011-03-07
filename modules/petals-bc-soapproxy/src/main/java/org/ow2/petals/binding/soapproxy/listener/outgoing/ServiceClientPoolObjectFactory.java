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

package org.ow2.petals.binding.soapproxy.listener.outgoing;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.RobustOutOnlyAxisOperation;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.ow2.petals.binding.soapproxy.SoapComponentContext;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsAxisService;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.util.UtilFactory;

public class ServiceClientPoolObjectFactory extends BasePoolableObjectFactory {

    private final String address;

    private final QName operation;

    private final URI mep;

    private final SoapComponentContext soapContext;

    private final Logger logger;

    private final String soapAction;

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
            final URI mep, final SoapComponentContext context, final Logger logger,
            String soapAction) {
        this.address = address;
        this.operation = operation;
        this.soapAction = soapAction;
        this.mep = mep;
        this.soapContext = context;
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    @Override
    public Object makeObject() throws Exception {

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Creating a service client for : " + this.address
                    + ", with operation '" + this.operation + "', and MEP '" + this.mep + "'");
        }

        final AxisService service = new AxisService(
                PetalsAxisService.OUTGOING_SERVICE_CLIENT_PREFIX
                        + UtilFactory.getIdUtil().createId());

        final AxisOperation axisOperation;
        if (MEPConstants.IN_ONLY_PATTERN.equals(this.mep)) {
            axisOperation = new OutOnlyAxisOperation(this.operation);
        } else if (MEPConstants.ROBUST_IN_ONLY_PATTERN.equals(this.mep)) {
            axisOperation = new RobustOutOnlyAxisOperation(this.operation);
        } else if (MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(this.mep)
                || MEPConstants.IN_OUT_PATTERN.equals(this.mep)) {
            axisOperation = new OutInAxisOperation(this.operation);
        } else {
            axisOperation = null;
        }
        service.addOperation(axisOperation);

        // create Options for the stub
        final Options options = ServiceClientPoolObjectFactory.createOptions(this.address,
                this.soapAction);

        try {
            final ServiceClient serviceClient = new ServiceClient(this.soapContext
                    .getAxis2ConfigurationContext(), service);
            serviceClient.setOptions(options);

            // engage the Axis2 modules
            this.engageModules(serviceClient);

            return serviceClient;
        } catch (final AxisFault e) {
            e.printStackTrace();
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
    private static Options createOptions(final String address, final String soapAction) {

        final Options options = new Options();

        // set destination address
        options.setTo(new EndpointReference(address));

        // set the soap action
        if (soapAction != null) {
            options.setAction(soapAction);
        }

        // set the timeout
        final long timeout = 30000L;
        if (timeout != -1L) {
            options.setTimeOutInMilliSeconds(timeout);
        }

        // Get the soapEnvelopeNamespaceURI version to use - optional,
        // default
        // is 1.1 version
        // final String soapEnvelopeNamespaceURI = AbstractExternalServiceCaller
        // .retrieveSOAPEnvelopeNamespaceURI(extensions);
        // options.setSoapVersionURI(soapEnvelopeNamespaceURI);

        // set transport
        // TODO : More transports to come?
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

        // get proxy settings if they are defined in the extensions
        // final HttpTransportProperties.ProxyProperties proxyProperties =
        // AbstractExternalServiceCaller
        // .retrieveProxySettings(extensions);
        // if (proxyProperties != null) {
        // options.setProperty(HTTPConstants.PROXY, proxyProperties);
        // }

        // options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
        // AbstractExternalServiceCaller.retrieveChunkedMode(extensions));

        options.setCallTransportCleanup(true);

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
        // if (this.provides != null) {
        // List<String> modules =
        // this.soapContext.getProvidersManager().getServiceContext(
        // this.provides).getModules();
        // if (modules != null) {
        // for (final String name : modules) {
        // this.engageModule(serviceClient, name);
        // }
        // }
        // } else {
        // // this.engageModule(serviceClient,
        // // org.ow2.petals.binding.soapproxy.Constants.Axis2.ADDRESSING_MODULE);
        // }
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
