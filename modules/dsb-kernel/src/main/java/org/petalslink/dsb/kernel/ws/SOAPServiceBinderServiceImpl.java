/**
 * 
 */
package org.petalslink.dsb.kernel.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.tools.ws.KernelWebService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.Constants;
import org.petalslink.dsb.kernel.management.binder.BinderException;
import org.petalslink.dsb.kernel.management.binder.ServiceBinderRegistry;
import org.petalslink.dsb.kernel.management.binder.ServiceRegistry;
import org.petalslink.dsb.ws.api.SOAPServiceBinder;
import org.petalslink.dsb.ws.api.ServiceEndpoint;

/**
 * @author chamerling
 *
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = KernelWebService.class),
        @Interface(name = "webservice", signature = SOAPServiceBinder.class) })
public class SOAPServiceBinderServiceImpl implements SOAPServiceBinder, KernelWebService {
    
    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;
    
    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Requires(name = "binder-registry", signature = ServiceBinderRegistry.class)
    protected ServiceBinderRegistry serviceBinderRegistry;

    @Requires(name = "service-registry", signature = ServiceRegistry.class)
    protected ServiceRegistry serviceRegistry;
    
    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.ServiceBinder#bindWebService(java.lang.String)
     */
    public List<ServiceEndpoint> bindWebService(String wsdlURL) throws PEtALSWebServiceException {
        if (this.serviceRegistry.getURLs(Constants.SOAP_SERVICE_BINDER).contains(wsdlURL)) {
            throw new PEtALSWebServiceException("This SOAP service '" + wsdlURL
                    + "' is already bound");
        }

        List<ServiceEndpoint> result = null;
        org.petalslink.dsb.kernel.management.binder.ServiceBinder binder = this.serviceBinderRegistry
                .getServiceBinder(Constants.SOAP_SERVICE_BINDER);
        if (binder != null) {
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("wsdl", wsdlURL);
            try {
                result = binder.bind(props);
            } catch (BinderException e) {
                e.printStackTrace();
                throw new PEtALSWebServiceException("Can not bind WSDL service", e);
            }
        } else {
            throw new PEtALSWebServiceException(
                    "No valid service binder can be found for SOAP services");
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.ServiceBinder#unbindWebService(java.lang.String)
     */
    public boolean unbindWebService(String wsdlURL) throws PEtALSWebServiceException {
        throw new PEtALSWebServiceException("Not implemented");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.ServiceBinder#getWebServices()
     */
    public Set<String> getWebServices() throws PEtALSWebServiceException {
        return this.serviceRegistry.getURLs(Constants.SOAP_SERVICE_BINDER);
    }

    public Component getComponent() {
        return this.component;
    }
}
