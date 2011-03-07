/**
 * 
 */
package org.petalslink.dsb.kernel.tools.remote;

import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.tools.ws.WebServiceException;
import org.petalslink.dsb.kernel.tools.ws.FractalWSHelper;
import org.petalslink.dsb.kernel.tools.ws.WebServiceExposer;
import org.petalslink.dsb.kernel.tools.ws.WebServiceInformationBean;


/**
 * Exposes all the components which are JAXWS annotated as Web services without
 * any restriction. It will introspect the container composites and components
 * to find all the JAXWS annotated components. This component can be placed
 * anywhere, it will go to the root component to retrieve all the components to
 * expose.
 * 
 * @author chamerling - PetalsLink
 * 
 */
public class AllJAXWSContainerWebServiceExposerImpl implements ContainerWebServiceExposer {

    @Requires(name = "webserviceexposer", signature = WebServiceExposer.class)
    private WebServiceExposer webServiceExposer;

    /**
     * Used to retrieve the current component. It is automatically injected by
     * Fractal.
     */
    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    @Monolog(name = "logger")
    private Logger logger;

    @Requires(name = "configuration", signature = ContainerConfiguration.class)
    private ContainerConfiguration containerConfiguration;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.tools.remote.ContainerWebServiceExposer#
     * expose()
     */
    public void expose() {
        Set<WebServiceInformationBean> beans = getAllWebServiceInformationBean();
        // define the URL of the components before sending them to the
        // webservice exposer
        for (WebServiceInformationBean bean : beans) {
            bean.setUrl(getURL(bean));
        }

        try {
            Set<WebServiceInformationBean> exposed = webServiceExposer.expose(beans);
        } catch (WebServiceException e) {
            e.printStackTrace();
        }
    }

    private String getURL(WebServiceInformationBean bean) {
        return ComponentAddressResolver
                .getComponentAddress(containerConfiguration, bean.getClazz());
    }

    private Set<WebServiceInformationBean> getAllWebServiceInformationBean() {
        // get the top level component to be able to get all the components...
        Component root = FractalWSHelper.getRootComponent(this.component);
        try {
            return FractalWSHelper.getAllBeans(Fractal.getContentController(root));
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        return null;
    }

}
