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

import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.KernelWebService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.api.RESTServiceBinder;
import org.petalslink.dsb.ws.api.SOAPServiceBinder;
import org.petalslink.dsb.ws.api.ServiceBinder;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = KernelWebService.class),
        @Interface(name = "webservice", signature = ServiceBinder.class) })
public class ServiceBinderImpl implements ServiceBinder, KernelWebService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;
    
    @Requires(name="restservice-binder", signature=RESTServiceBinder.class)
    protected RESTServiceBinder restServiceBinder;

    @Requires(name="soapservice-binder", signature=RESTServiceBinder.class)
    protected SOAPServiceBinder soapServiceBinder;

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
    public boolean bindWebService(String wsdlURL) throws DSBWebServiceException {
        this.log.warning("Deprecated, use SOAPServiceBinderServiceImpl instead");
        this.soapServiceBinder.bindWebService(wsdlURL);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean bindRESTService(String restURL, String endpointName)
            throws DSBWebServiceException {
        this.log.warning("Deprecated, use RESTServiceBinderServiceImpl instead");
        this.restServiceBinder.bindRESTService(restURL, endpointName);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return this.component;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getRESTServices() throws DSBWebServiceException {
        this.log.warning("Deprecated, use RESTServiceBinderServiceImpl instead");

        return this.restServiceBinder.getRESTServices();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getWebServices() throws DSBWebServiceException {
        this.log.warning("Deprecated, use SOAPServiceBinderServiceImpl instead");

        return this.soapServiceBinder.getWebServices();
    }

    /**
     * {@inheritDoc}
     */
    public boolean unbindRESTService(String restURL) throws DSBWebServiceException {
        this.log.warning("Deprecated, use RESTServiceBinderServiceImpl instead");

        return this.restServiceBinder.unbindRESTService(restURL);
    }

    /**
     * {@inheritDoc}
     */
    public boolean unbindWebService(String wsdlURL) throws DSBWebServiceException {
        this.log.warning("Deprecated, use SOAPServiceBinderServiceImpl instead");

        return this.soapServiceBinder.unbindWebService(wsdlURL);
    }

}
