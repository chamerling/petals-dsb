/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ConfigurationService.class) })
public class FileConfigurationServiceImpl implements ConfigurationService {

    @Monolog(name = "logger")
    private Logger logger;

    private org.ow2.petals.kernel.api.log.Logger log;

    @Requires(name = "configuration", signature = org.ow2.petals.kernel.configuration.ConfigurationService.class)
    private org.ow2.petals.kernel.configuration.ConfigurationService configurationService;

    private String baseURL;

    private String adminURL;

    private String listenerURL;

    private boolean active;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        log = new LoggingUtil(logger);
        log.start();

        this.load();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        log.start();
    }

    /**
     * Load configuration from a configuration file. If not found, the
     * properties are null...
     */
    protected void load() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File f = new File(configPath, Constants.CONFIG_FILE);
        if (f.exists() && f.isFile()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(f));

                this.active = Boolean
                        .getBoolean(props.getProperty(Constants.ACTIVE_PARAM, "false"));
                this.adminURL = props.getProperty(Constants.ADMINURL_PARAM,
                        Constants.DEFAULT_MONITORING_REGISTRATION_URL);
                this.baseURL = props.getProperty(Constants.BASEURL_PARAM,
                        Constants.DEFAULT_BASE_URL);
                if (!this.baseURL.endsWith("/")) {
                    this.baseURL = this.baseURL + "/";
                }
                this.listenerURL = props.getProperty(Constants.LISTENERURL_PARAM);

            } catch (Exception e) {
                log.warning(e.getMessage());
            }
        } else {
            log.warning("The monitoring configuration file has not been found : "
                    + Constants.CONFIG_FILE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.monitor.wsdm.ConfigurationService#getBaseURL()
     */
    public String getBaseURL() {
        return this.baseURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.monitor.wsdm.ConfigurationService#getAdminURL()
     */
    public String getAdminURL() {
        return this.adminURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.monitor.wsdm.ConfigurationService#getListenerURL
     * ()
     */
    public String getListenerURL() {
        return this.listenerURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.monitor.wsdm.ConfigurationService#isActive()
     */
    public boolean isActive() {
        return this.active;
    }

}
