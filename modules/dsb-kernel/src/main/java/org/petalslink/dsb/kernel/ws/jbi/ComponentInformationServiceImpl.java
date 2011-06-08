/**
 * 
 */
package org.petalslink.dsb.kernel.ws.jbi;

import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.api.jbi.ComponentInformationService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ComponentInformationService.class) })
public class ComponentInformationServiceImpl implements ComponentInformationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.jbi.ComponentInformationService#getComponentWSDL
     * (java.lang.String)
     */
    public Set<String> getComponentWSDL(String componentName) throws DSBWebServiceException {
        log.start();
        throw new DSBWebServiceException("Not implemented");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.jbi.ComponentInformationService#getComponentDescription(java.lang.String)
     */
    public String getComponentDescription(String componentName) throws DSBWebServiceException {
        log.start();
        throw new DSBWebServiceException("Not implemented");
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.jbi.ComponentInformationService#getComponentNames()
     */
    public Set<String> getComponentNames() throws DSBWebServiceException {
        log.start();
        throw new DSBWebServiceException("Not implemented");
    }

}
