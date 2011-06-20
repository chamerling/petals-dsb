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
package org.petalslink.dsb.kernel.federation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationFilterService.class) })
public class FederationFilterServiceImpl implements FederationFilterService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * Map of visible endpoints. Key is the federation name. This map is filled
     * at runtime. Additional definition are loaded from the filter store
     * service.
     */
    private Map<String, Set<String>> visible;

    @Requires(name = "filterstore", signature = FederationFilterStore.class)
    private FederationFilterStore federationFilterStore;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.visible = new HashMap<String, Set<String>>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(String federationName, String serviceEndpointName) {
        return true;

        /*
         * TODO : uncomment after tests... boolean result = false; if
         * (this.getVisible().get(federationName) != null) { // * for widcards
         * result = this.getVisible().get(federationName).contains("*") ||
         * this.getVisible().get(federationName).contains(serviceEndpointName);
         * } if (this.log.isDebugEnabled()) {
         * this.log.debug("Visibility for endpoint '" + serviceEndpointName +
         * "' into federation '" + federationName + "' is '" + result + "'"); }
         * return result;
         */
    }

    /**
     * @return
     */
    private Map<String, Set<String>> getVisible() {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        if (this.visible.size() == 0) {
            // add all the values on first call
            // TODO = see later when to fill the map in a better way...
            this.visible.putAll(this.federationFilterStore.getValues());
        }

        return result;
    }

    /**
     * Visibility is changed at runtime... Not sent to the filter store service
     * for now... {@inheritDoc}
     */
    public void setVisibility(boolean visible, String federationName, String serviceEndpointName) {
        if (visible) {
            if (this.visible.get(federationName) == null) {
                this.visible.put(federationName, new HashSet<String>(1));
            }
            this.visible.get(federationName).add(serviceEndpointName);
        } else {
            // remove the endpoint if already added to the set
            if (this.visible.get(federationName) != null) {
                // TODO = something else than a set...
                this.visible.get(federationName).remove(serviceEndpointName);
            }
        }
    }

}
