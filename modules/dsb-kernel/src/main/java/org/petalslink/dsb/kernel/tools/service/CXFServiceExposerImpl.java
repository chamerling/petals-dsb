/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceExposer.class) })
public class CXFServiceExposerImpl implements ServiceExposer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Exposer exposer;

    private Map<String, org.petalslink.dsb.commons.service.api.Service> exposed;

    @Requires(name = "registry", signature = ServiceRegistry.class)
    private ServiceRegistry registry;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.exposer = new CXFExposer();
        this.exposed = new HashMap<String, org.petalslink.dsb.commons.service.api.Service>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        for (org.petalslink.dsb.commons.service.api.Service exposedService : exposed.values()) {
            if (log.isInfoEnabled()) {
                log.info("Stopping a service...");
            }
            exposedService.stop();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.tools.service.ServiceExposer#expose()
     */
    @LifeCycleListener(priority = 0)
    public void expose() throws DSBException {
        List<Service> services = this.registry.getServices();
        if (log.isDebugEnabled()) {
            log.debug(String.format("Got %d services to expose", services.size()));
        }

        if (services != null) {
            for (Service service : services) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("Exposing service...");
                    }
                    org.petalslink.dsb.commons.service.api.Service s = this.exposer.expose(service);
                    s.start();
                    this.exposed.put(s.getURL(), s);
                    log.info(String.format(
                            "Service is exposed and is available as Web service at %s",
                            service.getURL()));
                } catch (ServiceException e) {
                    final String message = "Problem while exposing service";
                    if (log.isDebugEnabled()) {
                        log.warning(message, e);
                    } else {
                        log.warning(message);
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    final String message = "Unknown problem while exposing service";
                    if (log.isDebugEnabled()) {
                        log.warning(message, e);
                    } else {
                        log.warning(message);
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
