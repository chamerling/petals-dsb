/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

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
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationConfiguration.class) })
public class FileNotificationConfigurationImpl implements NotificationConfiguration {

    public static final String FILE_CFG = "notification.cfg";

    public static final String TOPICS_NS_FILE = "kernel-topicns-rpupdate.xml";

    public static final String TOPICSSET_FILE = "kernel-topicset.xml";

    private static final String ENDPOINT_NAME = "endpoint";

    private static final String INTERFACE_NAME = "interface";

    private static final String SERVICE_NAME = "service";

    private static final String SUPPORTED_TOPICS = "supported-topics";

    @Requires(name = "configuration", signature = ConfigurationService.class)
    protected ConfigurationService configurationService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private List<String> supportedTopics;

    private QName serviceName;

    private QName interfaceName;

    private String endpointName;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.supportedTopics = new ArrayList<String>();
        loadConfig();
    }

    /**
     * 
     */
    private void loadConfig() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File file = new File(configPath, FILE_CFG);
        if (file.exists() && file.isFile()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(file));
                this.endpointName = props.getProperty(ENDPOINT_NAME);
                this.interfaceName = QName.valueOf(props.getProperty(INTERFACE_NAME));
                this.serviceName = QName.valueOf(props.getProperty(SERVICE_NAME));

                String tmp = props.getProperty(SUPPORTED_TOPICS);
                if (tmp != null) {
                    tmp = tmp.trim();
                    String[] topics = tmp.split(",");
                    if (topics != null) {
                        for (String string : topics) {
                            if (string.trim().length() > 0) {
                                this.supportedTopics.add(string.trim());
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getTopicNamespaces()
     */
    public URL getTopicNamespaces() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File topicPath = new File(configPath, "topics");

        File result = new File(topicPath, TOPICS_NS_FILE);
        try {
            return result.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getSupportedTopics()
     */
    public List<String> getSupportedTopics() {
        return this.supportedTopics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getServiceName()
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getInterfaceName()
     */
    public QName getInterfaceName() {
        return this.interfaceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getEndpointName()
     */
    public String getEndpointName() {
        return this.endpointName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationConfiguration#
     * getTopicSet()
     */
    public URL getTopicSet() {
        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        File topicPath = new File(configPath, "topics");
        File result = new File(topicPath, TOPICSSET_FILE);
        try {
            return result.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
