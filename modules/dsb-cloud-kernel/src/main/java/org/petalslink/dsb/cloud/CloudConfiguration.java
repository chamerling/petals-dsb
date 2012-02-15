/**
 * 
 */
package org.petalslink.dsb.cloud;

import org.ow2.petals.edelweiss.api.Configuration;

/**
 * @author chamerling
 * 
 */
public class CloudConfiguration {

    private static CloudConfiguration instance;

    private Configuration configuration;
    
    private CloudConfiguration() {
    }

    public synchronized static CloudConfiguration get() {
        if (instance != null) {
            return instance;
        }

        // try to get from classpath first...
        Configuration configuration = org.ow2.petals.edelweiss.core.Configuration.Loader
                .loadResource(Constants.CONFIGURATION_FILE);
        if (configuration != null) {
            instance = new CloudConfiguration();
            instance.configuration = configuration;
            return instance;
        } else {
            System.out
                    .println("Can not find Cloud configuration in classpath, let's look in $HOME");
        }

        configuration = org.ow2.petals.edelweiss.core.Configuration.Loader
                .loadHome(Constants.CONFIGURATION_FILE);
        if (configuration != null) {
            instance = new CloudConfiguration();
            instance.configuration = configuration;
            return instance;
        } else {
            System.out.println("Can not find Cloud configuration in $HOME");
        }
        throw new RuntimeException("Can not find any Cloud configuration file");
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public String getGroupName() {
        return this.configuration.get("cloud-group", "default");

    }

}
