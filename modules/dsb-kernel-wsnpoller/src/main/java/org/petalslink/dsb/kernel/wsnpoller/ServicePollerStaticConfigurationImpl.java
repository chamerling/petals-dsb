/**
 * 
 */
package org.petalslink.dsb.kernel.wsnpoller;

import java.io.File;
import java.util.List;

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
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.WSNPoller;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServicePollerStaticConfiguration.class) })
public class ServicePollerStaticConfigurationImpl implements ServicePollerStaticConfiguration {

    public static final String CONFIG_FOLDER = "wsnpoller";

    public static final String CONFIG_FILE = "config.cfg";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "wsn-poller", signature = WSNPoller.class)
    private WSNPoller servicePoller;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configuration;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /**
     * Load the configuration from static files
     * 
     * @return
     */
    public List<Configuration> loadStaticConfiguration() {
        File configPath = getConfigurationPath();
        if (configPath == null || !configPath.exists() || !configPath.isDirectory()) {
            return null;
        }
        
        File config = new File(configPath, CONFIG_FILE);
        if (!config.exists()) {
            return null;
        }
        return ConfigurationLoader.load(config);
    }

    /**
     * @return
     */
    private File getConfigurationPath() {
        File result = null;
        File root = new File(configuration.getContainerConfiguration().getRootDirectoryPath());
        File conf = new File(root, "conf");
        if (!conf.exists()) {
            return result;
        }
        result = new File(conf, CONFIG_FOLDER);
        return result;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.servicepoller.ServicePollerStaticConfiguration#submitJobs()
     */
    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void submitJobs() {
        List<Configuration> config = loadStaticConfiguration();
        for (Configuration configuration : config) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Trying to launch polling on " + configuration);
                }
                String id = servicePoller.start(configuration.toPoll, configuration.inputMessage,
                        configuration.cronExpression, configuration.replyTo, configuration.topic);
                
                log.info("Polling OK with ID : " + id);
            } catch (ServicePollerException e) {
                String message = "Can not start polling for " + configuration;
                if (log.isDebugEnabled()) {
                    log.warning(message, e);
                } else {
                    log.warning(message);
                }
            }
        }
    }
}
