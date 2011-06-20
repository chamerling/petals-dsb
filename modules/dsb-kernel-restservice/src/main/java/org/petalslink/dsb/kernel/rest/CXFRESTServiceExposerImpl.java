/**
 * 
 */
package org.petalslink.dsb.kernel.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RESTServiceExposer.class) })
public class CXFRESTServiceExposerImpl implements RESTServiceExposer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    protected ConfigurationService configurationService;

    private Server server;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.start();
        if (server != null) {
            server.stop();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.rest.RESTExposer#expose(java.util.Set)
     */
    public Set<RESTServiceInformationBean> expose(Set<RESTServiceInformationBean> services) {
        Set<RESTServiceInformationBean> result = new HashSet<RESTServiceInformationBean>();
        String rootAddress = "http://" + configurationService.getContainerConfiguration().getHost()
                + ":" + configurationService.getContainerConfiguration().getWebservicePort()
                + "/rest/kernel/";

        List<Object> list = new ArrayList<Object>();
        for (RESTServiceInformationBean bean : services) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Adding REST service : " + bean);
            }
            if (log.isInfoEnabled()) {
                log.info("Kernel service " + bean.componentName + " is exposed as REST service at "
                        + rootAddress + bean.componentName);
            }
            list.add(bean.implem);
            result.add(bean);
        }

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        sf.setAddress(rootAddress);
        sf.setServiceBeans(list);
        server = sf.create();
        this.log.info("REST kernel services are exposed under " + rootAddress);
        return result;
    }
}
