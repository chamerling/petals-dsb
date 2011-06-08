/**
 * 
 */
package org.petalslink.dsb.kernel.tools.ws;

import java.util.HashSet;
import java.util.Set;

import javax.jws.WebService;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;

/**
 * This {@link WebServiceRegistry} implementation is able to get all the
 * {@link WebService} annotated classes from the component framework
 * independently from the current component location.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WebServiceRegistry.class) })
public class GlobalWebServiceRegistryImpl implements WebServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * Used to retrieve the current component. It is automatically injected by
     * Fractal.
     */
    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    Set<WebServiceInformationBean> webservices;

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
     * @see org.petalslink.dsb.kernel.tools.ws.WebServiceRegistry#load()
     */
    public synchronized void load() {
        Component root = FractalWSHelper.getRootComponent(this.component);
        System.out.println("ROOT : " + root);
        System.out.println(root.getFcInterfaces());
        try {
            Set<WebServiceInformationBean> beans = FractalWSHelper.getAllBeans(Fractal
                    .getContentController(root));
            if (webservices == null) {
                webservices = new HashSet<WebServiceInformationBean>();
            }
            webservices.addAll(beans);
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        if (log.isDebugEnabled()) {
            int i = 0;
            log.debug("Found the following services in the framework :");
            for (WebServiceInformationBean bean : webservices) {
                log.debug((i++) + " : " + bean);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.tools.ws.WebServiceRegistry#getWebServices()
     */
    public Set<WebServiceInformationBean> getWebServices() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getWebServices");
        }
        if (this.webservices == null) {
            this.webservices = new HashSet<WebServiceInformationBean>();
            this.load();
        }
        return new HashSet<WebServiceInformationBean>(this.webservices);
    }

}
