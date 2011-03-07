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
package org.petalslink.dsb.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.petalslink.dsb.kernel.federation.FederationConfigurationService;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = DSBConfigurationService.class) })
public class DSBConfigurationServiceImpl implements DSBConfigurationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    private long embeddedComponentDelay = Constants.EMBEDDED_COMPONENT_DELAY;

    private List<String> embeddedComponentList;

    private long endpointsPollingPeriod = Constants.ENDPOINTS_POLLING_PERIOD;

    private long endpointsPollingDelay = Constants.ENDPOINTS_POLLING_DELAY;

    private Map<String, List<String>> services2BindAtStartup;

    private long embeddedServicesDelay = Constants.EMBEDDED_SERVICES_DELAY;

    private int webAppPort;

    private Map<String, String> mapping;

    private String remoteTransport;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");

        this.embeddedComponentList = new ArrayList<String>();
        this.services2BindAtStartup = new HashMap<String, List<String>>();
        this.mapping = new HashMap<String, String>(2);
        this.loadConfig();
    }

    /**
     * 
     */
    protected void loadConfig() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File soa4allPropFile = new File(configPath, Constants.DSB_CFG_FILE);
        if (soa4allPropFile.exists() && soa4allPropFile.isFile()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(soa4allPropFile));

                // let's populate things
                String tmp = props.getProperty(Constants.EMBEDDED_COMPONENT_DELAY_PROPERTY);
                if (tmp != null) {
                    try {
                        this.embeddedComponentDelay = Long.parseLong(tmp);
                    } catch (Exception e) {
                    }
                }

                tmp = props.getProperty(Constants.EMBEDDED_COMPONENT_LIST_PROPERTY);
                if (tmp != null) {
                    tmp = tmp.trim();
                    String[] components = tmp.split(",");
                    if (components != null) {
                        for (String string : components) {
                            if (string.trim().length() > 0) {
                                this.embeddedComponentList.add(string.trim().toLowerCase());
                            }
                        }
                    }
                }

                tmp = props.getProperty(Constants.ENDPOINTS_POLLING_PERIOD_PROPERTY);
                if (tmp != null) {
                    try {
                        this.endpointsPollingPeriod = Long.parseLong(tmp);
                    } catch (Exception e) {
                    }
                }

                tmp = props.getProperty(Constants.ENDPOINTS_POLLING_DELAY_PROPERTY);
                if (tmp != null) {
                    try {
                        this.endpointsPollingDelay = Long.parseLong(tmp);
                    } catch (Exception e) {
                    }
                }

                tmp = props.getProperty(Constants.EMBEDDED_SERVICES_DELAY_PROPERTY);
                if (tmp != null) {
                    try {
                        this.embeddedServicesDelay = Long.parseLong(tmp);
                    } catch (Exception e) {
                    }
                }

                tmp = props.getProperty(Constants.WEBAPP_PORT);
                if (tmp != null) {
                    try {
                        this.webAppPort = Integer.parseInt(tmp);
                    } catch (Exception e) {
                        this.webAppPort = Constants.DEFAULT_WEBAPP_PORT;
                    }
                }
                if (this.webAppPort == 0) {
                    this.webAppPort = Constants.DEFAULT_WEBAPP_PORT;
                }

                for (String key : props.stringPropertyNames()) {
                    if ((key != null) && key.startsWith(Constants.MAPPING_PREFIX)) {
                        this.mapping.put(key.substring(Constants.MAPPING_PREFIX.length()), props
                                .getProperty(key));
                    }
                }

                this.remoteTransport = props.getProperty(Constants.REMOTE_TRANSPORT);
                if (this.remoteTransport != null) {
                    this.remoteTransport = this.remoteTransport.toLowerCase().trim();
                }

            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }

        // get the wsdl to bind at startup
        File wsdl2Bind = new File(configPath, Constants.WEBSERVICE_TO_BIND_AT_STARTUP_FILE);
        if (wsdl2Bind.exists() && wsdl2Bind.isFile()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(wsdl2Bind));
                for (Object key : props.keySet()) {
                    String url = props.getProperty(key.toString());

                    if (url != null) {
                        // the value can be a CSV one, let's cut it in a list
                        List<String> urls = new ArrayList<String>();
                        if (url.indexOf(',') >= 0) {
                            String[] tmp = url.split(",");
                            for (String u : tmp) {
                                if (u.trim().length() > 0) {
                                    urls.add(u.trim());
                                }
                            }
                        } else if (url.trim().length() > 0) {
                            urls.add(url.trim());
                        }

                        String k = key.toString();
                        if (k.indexOf('.') > 0) {
                            k = k.substring(0, k.indexOf('.'));
                        }

                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Adding Service to bind '" + k + " : " + urls + "'");
                        }

                        if (this.services2BindAtStartup.get(k) == null) {
                            this.services2BindAtStartup.put(k, new ArrayList<String>());
                        }

                        for (String finalUrl : urls) {
                            if (this.log.isInfoEnabled()) {
                                this.log.info("The service '" + finalUrl
                                        + "' is defined to be bound to the Service Bus");
                            }
                            this.services2BindAtStartup.get(k).add(finalUrl);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public long getEmbeddedComponentDelay() {
        return this.embeddedComponentDelay;
    }

    /**
     * {@inheritDoc}
     */
    public long getEmbeddedServicesDelay() {
        return this.embeddedServicesDelay;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getEmbeddedComponentList() {
        return this.embeddedComponentList;
    }

    /**
     * {@inheritDoc}
     */
    public long getEndpointsPollingPeriod() {
        return this.endpointsPollingPeriod;
    }

    /**
     * {@inheritDoc}
     */
    public long getEndpointsPollingDelay() {
        return this.endpointsPollingDelay;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getServices2BindAtStartup() {
        return this.services2BindAtStartup;
    }

    /**
     * {@inheritDoc}
     */
    public int getWebAppPort() {
        return this.webAppPort;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getProtocolToComponentMapping() {
        return this.mapping;
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteTransport() {
        return this.remoteTransport;
    }

    /**
     * {@inheritDoc}
     */
    public int getWSTransportPort() {
        // TODO
        return 9998;
    }

    /**
     * {@inheritDoc}
     */
    public String getFederationURL() {
        log.warning("Deprecated, use the FederationConfigurationService instead!");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFederationAware() {
        log.warning("Deprecated, use the FederationConfigurationService instead!");
        return false;
    }

}
