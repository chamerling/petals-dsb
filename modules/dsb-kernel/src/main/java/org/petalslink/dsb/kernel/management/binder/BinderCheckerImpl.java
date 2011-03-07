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
package org.petalslink.dsb.kernel.management.binder;

import java.util.Iterator;
import java.util.List;

import javax.jbi.management.LifeCycleMBean;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.ws.api.JBIArtefactsService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.kernel.ws.api.to.Component;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.DSBConfigurationService;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = BinderChecker.class) })
public class BinderCheckerImpl implements BinderChecker {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "jbiartefacts", signature = JBIArtefactsService.class)
    private JBIArtefactsService jbiArtefactsService;

    @Requires(name = "dsbconfiguration", signature = DSBConfigurationService.class)
    private DSBConfigurationService dsbConfigurationService;

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
    public boolean canBindOnProtocol(String protocolName) {
        boolean result = false;
        if (this.dsbConfigurationService.getProtocolToComponentMapping().get(protocolName) == null) {
            result = false;
        } else {
            result = this.isComponentReady(this.dsbConfigurationService
                    .getProtocolToComponentMapping().get(protocolName));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canExposeOnProtocol(String protocolName) {
        boolean result = false;
        if (this.dsbConfigurationService.getProtocolToComponentMapping().get(protocolName) == null) {
            result = false;
        } else {
            result = this.isComponentReady(this.dsbConfigurationService
                    .getProtocolToComponentMapping().get(protocolName));
        }
        return result;
    }

    protected boolean isComponentReady(String componentName) {
        boolean result = false;
        try {
            List<Component> components = this.jbiArtefactsService.getComponents();
            Iterator<Component> iter = components.iterator();
            while (iter.hasNext() && !result) {
                Component component = iter.next();
                result = (component.getName().equals(componentName) && component.getState().equals(
                        LifeCycleMBean.STARTED));
            }
        } catch (PEtALSWebServiceException e) {
        }
        return result;
    }

}
