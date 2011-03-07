/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.ws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.management.component.ComponentInformationService;
import org.petalslink.dsb.kernel.util.EndpointHelper;
import org.petalslink.dsb.ws.api.PlatformServiceInformationService;
import org.petalslink.dsb.ws.api.ServiceEndpoint;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = PlatformServiceInformationService.class) })
public class PlatformServiceInformationServiceImpl implements PlatformServiceInformationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "endpointregistry", signature = EndpointRegistry.class)
    private EndpointRegistry endpointRegistry;

    @Requires(name = "component-information", signature = ComponentInformationService.class)
    private ComponentInformationService componentInformationService;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public String getPlatformRESTService(String restURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getPlatformRESTService with params restURL = "
                    + restURL);
        }

        String base = this.getRESTServiceAddress();

        ServiceEndpoint endpoint = this.getRESTServiceEndpoint(restURL);
        if ((endpoint == null)) {
            return null;
        }

        String serviceName = this.getRESTServiceName(endpoint);
        return base + serviceName;
    }

    /**
     * {@inheritDoc}
     */
    public String getPlatformWebService(String wsdlURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getPlatformWebService with params wsdlURL = "
                    + wsdlURL);
        }

        List<ServiceEndpoint> endpoint = this.getWebServiceEndpoint(wsdlURL);
        if ((endpoint == null) || (endpoint.size() == 0)) {
            return null;
        }

        // get the SOAP URL from the context...
        String base = this.getWebServiceAddress();
        String serviceName = this.getSOAPServiceName(endpoint.get(0));
        return base + serviceName;
    }

    /**
     * @param endpoint
     * @return
     */
    private String getSOAPServiceName(ServiceEndpoint endpoint) {
        String soapServiceName = endpoint.getEndpoint();
        if (soapServiceName
                .startsWith(org.petalslink.dsb.kernel.Constants.SOAP_PLATFORM_ENDPOINT_PREFIX)) {
            soapServiceName = soapServiceName.substring(
                    org.petalslink.dsb.kernel.Constants.SOAP_PLATFORM_ENDPOINT_PREFIX.length(),
                    soapServiceName.length());
        }
        return soapServiceName + "Service";
    }

    /**
     * @return
     */
    private String getWebServiceAddress() {
        String baseProxy = this.componentInformationService
                .getProperty("petals-bc-soap", "service");

        if (baseProxy == null) {
            return "";
        }
        String result = baseProxy.replaceAll("\\$HOST", this.configurationService
                .getContainerConfiguration().getHost());

        if (result.charAt(result.length() - 1) != '/') {
            result = result + "/";
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint getRESTServiceEndpoint(String restURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getRESTServiceEndpoint with params restURL = "
                    + restURL);
        }
        try {
            return EndpointHelper.getRESTEndpoint(new URI(restURL));
        } catch (URISyntaxException e) {
            throw new PEtALSWebServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getWebServiceEndpoint(String wsdlURI)
            throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getWebServiceEndpoint with params wsdlURI = "
                    + wsdlURI);
        }
        try {
            return EndpointHelper.getEndpoints(new URI(wsdlURI));
        } catch (URISyntaxException e) {
            throw new PEtALSWebServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRESTServiceBound(String restURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : isRESTServiceBound with params restURL = " + restURL);
        }
        ServiceEndpoint serviceEndpoint = this.getRESTServiceEndpoint(restURL);
        if (serviceEndpoint == null) {
            throw new PEtALSWebServiceException("Can not generate an Endpoint from the given URL "
                    + restURL);
        }

        // look at the registry...
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.endpointRegistry.query(serviceEndpoint.getEndpoint(), serviceEndpoint
                    .getItf(), serviceEndpoint.getService(), null, null, null, null);
        } catch (RegistryException e) {
            throw new PEtALSWebServiceException(e.getMessage());
        }
        return (endpoints != null) && (endpoints.size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWebServiceBound(String wsdlURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : isWebServiceBound with params wsdlURL = " + wsdlURL);
        }
        List<ServiceEndpoint> serviceEndpoint = this.getWebServiceEndpoint(wsdlURL);
        if (serviceEndpoint == null) {
            throw new PEtALSWebServiceException("Can not generate an Endpoint from the given URL "
                    + wsdlURL);
        }

        // look at the registry...
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = new ArrayList<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>();
        for (ServiceEndpoint serviceEndpoint2 : serviceEndpoint) {
            try {
                endpoints.addAll(this.endpointRegistry.query(serviceEndpoint2.getEndpoint(),
                        serviceEndpoint2.getItf(), serviceEndpoint2.getService(), null, null, null,
                        null));
            } catch (RegistryException e) {
                this.log.warning(e.getMessage());
            }
        }
        return (endpoints != null) && (endpoints.size() > 0);
    }

    private String getRESTServiceAddress() {
        String baseProxy = this.componentInformationService
                .getProperty("petals-bc-rest", "service");

        if (baseProxy == null) {
            return "";
        }
        String result = baseProxy.replaceAll("\\$HOST", this.configurationService
                .getContainerConfiguration().getHost());

        if (result.charAt(result.length() - 1) != '/') {
            result = result + "/";
        }

        return result;
    }

    /**
     * @param endpoint
     * @return
     */
    private String getRESTServiceName(ServiceEndpoint endpoint) {
        String endpointName = endpoint.getEndpoint();
        if (endpointName
                .startsWith(org.petalslink.dsb.kernel.Constants.REST_PLATFORM_ENDPOINT_PREFIX)) {
            endpointName = endpointName.substring(
                    org.petalslink.dsb.kernel.Constants.REST_PLATFORM_ENDPOINT_PREFIX.length(),
                    endpointName.length());
        }
        return endpoint != null ? endpointName + "Service" : "";
    }
}
