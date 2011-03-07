/**
 * 
 */
package org.petalslink.dsb.kernel.federation;

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
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;

/**
 * A properties file based {@link FederationConfigurationService}
 * implementation.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationConfigurationService.class) })
public class FileFederationConfigurationServiceImpl implements FederationConfigurationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    /**
     * URL from configuration file
     */
    private String federationURL;
    
    /**
     * Active from configuration file
     */
    private boolean active;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        log = new LoggingUtil(logger);
        log.start();

        this.loadConfiguration();
    }

    protected void loadConfiguration() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File fedProps = new File(configPath, Constants.FED_CONFIG_FILE);
        if (fedProps.exists() && fedProps.isFile()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(fedProps));
                
                this.active = Boolean.getBoolean(props.getProperty(Constants.FED_ACTIVE, "false"));
                this.federationURL = props.getProperty(Constants.FED_URL);
                
            } catch (Exception e) {
                log.warning(e.getMessage());
            }
        } else {
            log.warning("The federation configuration file has not been found : "
                    + Constants.FED_CONFIG_FILE);
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        log.start();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.federation.FederationConfigurationService#isActive
     * ()
     */
    public boolean isActive() {
        return this.active;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.federation.FederationConfigurationService#
     * getFederationURL()
     */
    public String getFederationURL() {
        return this.federationURL;
    }

}
