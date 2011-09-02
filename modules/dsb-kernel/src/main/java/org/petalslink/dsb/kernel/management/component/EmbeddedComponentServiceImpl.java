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
package org.petalslink.dsb.kernel.management.component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.jbi.management.installation.ComponentInstallationService;
import org.ow2.petals.system.repository.artifact.Artifact;
import org.ow2.petals.system.repository.artifact.ArtifactRepositoryService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.api.DSBConfigurationService;
import org.petalslink.dsb.kernel.api.management.component.EmbeddedComponentService;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = EmbeddedComponentService.class) })
public class EmbeddedComponentServiceImpl implements EmbeddedComponentService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "dsb-configuration", signature = DSBConfigurationService.class)
    private DSBConfigurationService configurationService;

    @Requires(name = "installation", signature = ComponentInstallationService.class)
    private ComponentInstallationService componentInstallationService;

    @Requires(name = "artifact-repository", signature = ArtifactRepositoryService.class)
    private ArtifactRepositoryService artifactRepositoryService;

    @Requires(name = "adminService", signature = AdminService.class)
    private AdminService adminService;

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
    @LifeCycleListener(phase = Phase.START, priority=1000)
    public void install() {
        this.log.info("It is time to install embedded components...");

        StringBuffer sb = new StringBuffer("Installation result : ");

        // let's install things if they are not already installed
        for (String componentName : EmbeddedComponentServiceImpl.this.getComponents()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Installing component '" + componentName + "'");
            }

            // TODO : check if the component has not been installed
            // before by another process

            if ((componentName != null) && (componentName.trim().length() > 0)
                    && (this.getComponentURL(componentName) != null)) {
                if (this.adminService.getComponentByName(componentName) == null) {

                    boolean tmp = this.componentInstallationService.install(this
                            .getComponentURL(componentName));

                    if (tmp) {
                        this.log.debug("Component '" + componentName
                                + "' has been successfully installed!");
                    } else {
                        this.log.warning("Component '" + componentName
                                + "' has not been installed!");
                    }
                    sb.append(" Component = " + componentName);
                    sb.append(", installation success : ");
                    sb.append(tmp);

                } else {
                    this.log.info("The component '" + componentName + "' is already installed");
                }
            } else {
                this.log.warning("Bad component name '" + componentName
                        + "' or component not found in components repository");
            }
        }

        if (this.log.isDebugEnabled()) {
            this.log.debug(sb.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getComponents() {
        return this.configurationService.getEmbeddedComponentList();
    }

    private URL getComponentURL(String componentName) {
        URL result = null;
        List<Artifact> components = this.artifactRepositoryService.getComponents();
        boolean found = false;
        Iterator<Artifact> iter = components.iterator();
        Artifact a = null;
        while (iter.hasNext() && !found) {
            a = iter.next();
            found = componentName.equals(a.getName());
        }

        if (a != null) {
            try {
                result = a.getFile().toURL();
            } catch (MalformedURLException e) {
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("URL of component " + componentName + " is " + result);
        }
        return result;
    }
}
