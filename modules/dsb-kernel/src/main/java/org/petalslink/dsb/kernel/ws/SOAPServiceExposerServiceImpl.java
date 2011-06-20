/**
 * 
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
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.tools.ws.KernelWebService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.Constants;
import org.petalslink.dsb.kernel.api.management.binder.BinderException;
import org.petalslink.dsb.kernel.api.management.binder.ServiceExposerRegistry;
import org.petalslink.dsb.ws.api.SOAPServiceExposer;
import org.petalslink.dsb.ws.api.ServiceEndpoint;

/**
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = KernelWebService.class),
        @Interface(name = "webservice", signature = SOAPServiceExposer.class) })
public class SOAPServiceExposerServiceImpl implements SOAPServiceExposer, KernelWebService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @Requires(name = "exposer-registry", signature = ServiceExposerRegistry.class)
    private ServiceExposerRegistry serviceExposerRegistry;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.ServiceExposer#expose(org.petalslink.dsb.ws
     * .api.ServiceEndpoint)
     */
    public boolean expose(final ServiceEndpoint serviceEndpoint) throws PEtALSWebServiceException {
        this.log.call();
        org.petalslink.dsb.kernel.api.management.binder.ServiceExposer exposer = serviceExposerRegistry
                .getServiceExposer(Constants.SOAP_SERVICE_EXPOSER);
        if (exposer == null) {
            throw new PEtALSWebServiceException(
                    "Can not find a valid service exposer for protocol '"
                            + Constants.SOAP_SERVICE_EXPOSER + "'");
        }

        try {
            exposer.expose(serviceEndpoint);
        } catch (BinderException e) {
            final String message = "Can not expose service due to internal problem";
            if (log.isDebugEnabled()) {
                this.log.warning(message, e);
            } else {
                this.log.warning(message + " : " + e.getMessage());
            }
            throw new PEtALSWebServiceException(message, e);
        }

        // FIXME : Return value!
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.ServiceExposer#delete(org.petalslink.dsb.ws
     * .api.ServiceEndpoint)
     */
    public boolean delete(ServiceEndpoint serviceEndpoint) throws PEtALSWebServiceException {
        throw new PEtALSWebServiceException("Not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.ws.api.ServiceExposer#getWebServices()
     */
    public Set<String> getWebServices() throws PEtALSWebServiceException {
        throw new PEtALSWebServiceException("Not implemented");
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.tools.ws.KernelWebService#getComponent()
     */
    public Component getComponent() {
        return this.component;
    }
}
