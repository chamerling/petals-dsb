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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.WebServiceException;
import org.ow2.petals.tools.ws.WebServiceManager;
import org.ow2.petals.util.LoggingUtil;

/**
 * The new web service manager implementation based on components introspection
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WebServiceManager.class) })
public class WebServiceManagerImpl implements WebServiceManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "wsregistry", signature = WebServiceRegistry.class)
    private WebServiceRegistry webServiceRegistry;

    @Requires(name = "wsexposer", signature = WebServiceExposer.class)
    private WebServiceExposer webServiceExposer;

    private boolean exposed;

    private Set<WebServiceInformationBean> services;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.services = new HashSet<WebServiceInformationBean>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void exposeServices() throws WebServiceException {
        if (!this.exposed) {
            // get the services from the registry
            Set<WebServiceInformationBean> set = this.webServiceRegistry.getWebServices();
            // TODO filter things that we do not want to expose from
            // configuration
            // file
            Set<WebServiceInformationBean> subset = set;
            Set<WebServiceInformationBean> exposed = null;
            try {
                exposed = this.webServiceExposer.expose(subset);
            } catch (org.petalslink.dsb.kernel.tools.ws.WebServiceException e) {
                throw new WebServiceException(e);
            }
            if (exposed != null) {
                this.services.addAll(exposed);
            }
            this.exposed = true;
        } else {
            throw new WebServiceException("Services already exposed");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getServiceNames() {
        List<String> result = new ArrayList<String>();
        for (WebServiceInformationBean bean : this.services) {
            result.add(bean.name);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getServicesURL() throws WebServiceException {
        List<String> result = new ArrayList<String>();
        for (WebServiceInformationBean bean : this.services) {
            result.add(bean.url);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void removeService(String name) throws WebServiceException {
        try {
            this.webServiceExposer.remove(name);
        } catch (org.petalslink.dsb.kernel.tools.ws.WebServiceException e) {
            throw new WebServiceException(e);
        }
    }

}
