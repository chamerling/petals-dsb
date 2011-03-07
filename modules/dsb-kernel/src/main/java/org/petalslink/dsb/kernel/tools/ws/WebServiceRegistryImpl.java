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

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.WebServiceHelper;
import org.ow2.petals.util.LoggingUtil;

/**
 * A registry with all the components which belong to the same composite as the
 * current one and which are JAXWS annotated.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WebServiceRegistry.class) })
public class WebServiceRegistryImpl implements WebServiceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * Used to retrieve the current component. It is automatically injected by
     * Fractal.
     */
    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    Map<String, WebServiceInformationBean> webservices;

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
    public synchronized Set<WebServiceInformationBean> getWebServices() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getWebServices");
        }
        if (this.webservices == null) {
            this.webservices = new HashMap<String, WebServiceInformationBean>();
            this.load();
        }
        return new HashSet<WebServiceInformationBean>(this.webservices.values());
    }

    /**
     * {@inheritDoc}
     */
    public void load() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : load");
        }

        try {
            SuperController sc = Fractal.getSuperController(this.component);
            if (sc.getFcSuperComponents().length != 1) {
                this.log.warning("Can not find a super component to look at WS children");
            } else {
                Component parentcontainer = sc.getFcSuperComponents()[0];
                ContentController cc = Fractal.getContentController(parentcontainer);
                for (Component component : cc.getFcSubComponents()) {
                    String name = Fractal.getNameController(component).getFcName();
                    Object[] itfs = component.getFcInterfaces();
                    for (Object object : itfs) {
                        Class<?>[] cs = object.getClass().getInterfaces();
                        for (Class<?> class1 : cs) {
                            boolean isWs = WebServiceHelper.hasWebServiceAnnotation(class1);
                            if (isWs) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug("The component " + name
                                            + " is a compliant web service");
                                }
                                WebServiceInformationBean bean = new WebServiceInformationBean();
                                bean.clazz = class1;
                                bean.componentName = name;
                                bean.implem = object;
                                this.webservices.put(name, bean);
                            }
                        }
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Found " + this.webservices.size() + " in architecture");
                }
            }
        } catch (Exception e) {
            this.log.warning(e.getMessage());
        }
    }
}
