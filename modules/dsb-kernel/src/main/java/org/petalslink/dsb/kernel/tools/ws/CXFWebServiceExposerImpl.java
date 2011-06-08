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
package org.petalslink.dsb.kernel.tools.ws;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.tools.ws.WebServiceException;
import org.ow2.petals.tools.ws.WebServiceHelper;
import org.ow2.petals.util.LoggingUtil;

/**
 * Expose the components as web services with CXF
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WebServiceExposer.class) })
public class CXFWebServiceExposerImpl implements WebServiceExposer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    protected ConfigurationService configurationService;

    protected Map<String, Server> servers;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.servers = new HashMap<String, Server>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public Set<WebServiceInformationBean> expose(Set<WebServiceInformationBean> set) {
        Set<WebServiceInformationBean> result = new HashSet<WebServiceInformationBean>();
        for (WebServiceInformationBean webServiceInformationBean : set) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Exposing component " + webServiceInformationBean.componentName
                        + " as web service");
            }
            WebServiceInformationBean service = null;
            try {
                service = this.exposeAsWebService(webServiceInformationBean);
            } catch (WebServiceException e) {
                this.log.warning(e.getMessage());
            }
            if (service != null) {
                result.add(service);
            }
        }
        return result;

    }

    protected WebServiceInformationBean exposeAsWebService(WebServiceInformationBean bean)
            throws WebServiceException {
        if ((bean == null) || (bean.implem == null)) {
            throw new WebServiceException("Can not create a web service from null things...");
        }
        
        if (isAlreadyRegistered(bean)) {
            throw new WebServiceException(String.format(
                    "The service provided by component %s is already registered",
                    bean.componentName));
        }
        
        WebServiceInformationBean result = bean;

        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setDataBinding(new JAXBDataBinding());
        sf.setServiceBean(bean.implem);

        String serviceName = null;
        Class<?> wsClass = WebServiceHelper.getWebServiceClass(bean.implem.getClass());

        if (wsClass == null) {
            throw new WebServiceException(
                    String.format(
                            "Can not find the class definition of the component to expose for component %s",
                            bean.componentName));
        }
        
        serviceName = WebServiceHelper.getWebServiceName(wsClass);
        if ((serviceName == null) || (this.servers.get(serviceName) != null)) {
            // Should not happen since this is a very bad name generated by Fractal such as Cca175585_0...
            // already checked previously...
            serviceName = bean.implem.getClass().getSimpleName();
        }
        result.name = serviceName;
        sf.setServiceClass(wsClass);

        String url = null;
        if (bean.url == null) {
            url = this.getURL(serviceName);
        } else {
            url = bean.url;
        }
        String wsdl = url + "?wsdl";
        if (this.log.isDebugEnabled()) {
            this.log.debug("Service URL is " + url);
            this.log.debug("Service WSDL is " + wsdl);
        }
        result.url = url;
        sf.setAddress(url);
        Server srv = sf.create();

        // TODO : Store the address and why not displaying the services list
        // It seems that services list is only provided when using
        // CXFServlet,
        // need to have a look to that
        // JettyHTTPServerEngineFactory jetty =
        // svrFactory.getBus().getExtension(JettyHTTPServerEngineFactory.class);
        // JettyHTTPServerEngine engine =
        // jetty.retrieveJettyHTTPServerEngine(8084);
        // Server server = engine.getServer();

        this.servers.put(serviceName, srv);
        if (this.log.isInfoEnabled()) {
            this.log.info("Kernel Component '" + bean.componentName
                    + "' has been exposed and is available at '" + url + "'");
        }
        return result;
    }

    /**
     * @param bean
     * @return
     */
    protected boolean isAlreadyRegistered(WebServiceInformationBean bean) {
        String serviceName = WebServiceHelper.getWebServiceName(bean.getClazz());
        if (serviceName == null) {
            serviceName = WebServiceHelper.getWebServiceName(bean.getClazz());
        }
        return serviceName == null ? false : servers.get(serviceName) != null;
    }

    protected String getURL(final String serviceName) {
        final ContainerConfiguration cc = this.configurationService.getContainerConfiguration();
        String pre = cc.getWebservicePrefix();
        if (pre == null) {
            pre = DEFAULT_PREFIX;
        } else {
            pre = pre.trim();
        }

        int port = cc.getWebservicePort();
        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        return "http://" + cc.getHost() + ":" + port + "/" + pre + "/" + serviceName;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WebServiceException
     */
    public void remove(String name) throws WebServiceException {
        Server server = this.servers.get(name);
        if (server != null) {
            try {
                server.stop();
            } catch (Throwable e) {
                final String message = "Web Service " + name + " can not be removed";
                if (this.log.isWarnEnabled()) {
                    this.log.warning(message, e);
                }
                throw new WebServiceException(message, e);
            }
            this.servers.remove(name);
        }
    }

}
