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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

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
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationFilterStore.class) })
public class FileFederationFilterStoreImpl implements FederationFilterStore {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    private Map<String, Set<String>> filters;

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
    public synchronized Map<String, Set<String>> load() {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        Properties props = new Properties();
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File federationFilterConfig = new File(configPath, Constants.FED_FILTERS);
        if (federationFilterConfig.exists() && !federationFilterConfig.isDirectory()) {
            try {
                props.load(new FileInputStream(federationFilterConfig));
                for (Object key : props.keySet()) {
                    Set<String> set = new HashSet<String>();
                    String federationName = key.toString();
                    String values = props.getProperty(federationName);
                    StringTokenizer tokenizer = new StringTokenizer(values, ",");
                    while (tokenizer.hasMoreTokens()) {
                        set.add(tokenizer.nextToken().trim());
                    }
                    result.put(federationName, set);
                }
            } catch (Exception e) {
                if (this.log.isDebugEnabled()) {
                    this.log.warning("Can not load service filter config file", e);
                } else {
                    this.log.warning("Can not load service filter config file");
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Set<String>> getValues() {
        if (this.filters == null) {
            this.filters = this.load();
        }
        return this.filters;
    }

    /**
     * {@inheritDoc}
     */
    public void save(Map<String, Set<String>> filters) {
        // TODO Auto-generated method stub
    }

}
