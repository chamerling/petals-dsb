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
package org.petalslink.dsb.kernel.management;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.server.FractalHelper;
import org.ow2.petals.kernel.ws.api.InformationService;
import org.ow2.petals.kernel.ws.api.InstallationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.webapp.api.DSBManagement;
import org.petalslink.dsb.ws.api.DSBInformationService;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.api.ServiceInformation;

/**
 * This is a quick and dummy implementation, need more work on the fractal
 * configuration side.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = DSBManagement.class) })
public class DSBManagementImpl implements DSBManagement {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "information", signature = InformationService.class)
    private InformationService informationService;

    @Requires(name = "serviceinfo", signature = ServiceInformation.class)
    private ServiceInformation serviceInformation;

    @Requires(name = "dsbinformation", signature = InstallationService.class)
    private DSBInformationService dsbInformationService;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    public String getContainerInfo() {
        try {
            return this.informationService.getVersion();
        } catch (PEtALSWebServiceException e) {
        }
        return "";
    }

    public Set<String> getContainerServices() {
        try {
            return new HashSet<String>(dsbInformationService.getWebServices());
        } catch (DSBWebServiceException e) {
        }
        return null;
    }

    public Set<String> getWebServices() {
        return new HashSet<String>(serviceInformation.getExposedWebServices());
    }

    public Set<String> getRESTServices() {
        return new HashSet<String>(serviceInformation.getExposedRESTServices());
    }

    public <T> T get(Class<T> t, String componentName, String serviceName) {
        T result = null;
        try {
            SuperController sc = Fractal.getSuperController(this.component);
            Component parentcontainer = sc.getFcSuperComponents()[0];
            ContentController cc = Fractal.getContentController(parentcontainer);
            Component c = FractalHelper.getRecursiveComponentByName(cc, componentName);
            if (c != null) {
                String name = (serviceName != null) ? serviceName : "service";
                Object o = c.getFcInterface(name);
                if (o != null) {
                    try {
                        result = t.cast(o);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No such component : " + componentName);
            }
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        return result;
    }
}
