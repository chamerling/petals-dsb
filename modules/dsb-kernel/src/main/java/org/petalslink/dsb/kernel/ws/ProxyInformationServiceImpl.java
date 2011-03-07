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

import java.net.MalformedURLException;
import java.net.URI;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.management.component.ComponentInformationService;
import org.petalslink.dsb.ws.api.ProxyInformationService;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ProxyInformationService.class) })
public class ProxyInformationServiceImpl implements ProxyInformationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
    public String getRESTProxy(String restURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getRESTProxy with params restURL = " + restURL);
        }

        if (restURL == null) {
            throw new PEtALSWebServiceException("Null URL is not allowed...");
        }

        try {
            URI.create(restURL).toURL();
        } catch (MalformedURLException e) {
            throw new PEtALSWebServiceException(e.getMessage());
        }

        String result = null;
        String baseProxy = this.componentInformationService.getProperty("petals-bc-rest", "proxy");
        if (baseProxy == null) {
            throw new PEtALSWebServiceException(
                    "Can not define the proy URL, the REST component seems to be inactive");
        }

        // replace host value
        result = baseProxy.replaceAll("\\$HOST", this.configurationService
                .getContainerConfiguration().getHost());

        if (result.charAt(result.length() - 1) != '/') {
            result = result + "/";
        }
        result = result + restURL;
        return result;
    }

    /**
     * TODO : For now the REST proxy also serves as SOAP one... {@inheritDoc}
     */
    public String getSOAPProxy(String wsdlURL) throws PEtALSWebServiceException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getSOAPProxy with params wsdlURL = " + wsdlURL);
        }

        if (wsdlURL == null) {
            throw new PEtALSWebServiceException("Null URL is not allowed...");
        }

        try {
            URI.create(wsdlURL).toURL();
        } catch (MalformedURLException e) {
            throw new PEtALSWebServiceException(e.getMessage());
        }

        String result = null;
        String baseProxy = this.componentInformationService.getProperty("petals-bc-rest", "proxy");
        if (baseProxy == null) {
            throw new PEtALSWebServiceException(
                    "Can not define the proy URL, the REST component seems to be inactive");
        }

        // replace host value
        result = baseProxy.replaceAll("\\$HOST", this.configurationService
                .getContainerConfiguration().getHost());

        if (result.charAt(result.length() - 1) != '/') {
            result = result + "/";
        }
        result = result + wsdlURL;
        return result;
    }
}
