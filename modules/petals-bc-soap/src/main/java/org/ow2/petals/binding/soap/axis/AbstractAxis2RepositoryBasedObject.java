
package org.ow2.petals.binding.soap.axis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

abstract class AbstractAxis2RepositoryBasedObject extends AbstractAxis2DirBasedObject {

    private final File configDir;

    private final File configFile;

    private final Axis2Config config;

    private final Axis2Repository repository;

    private ConfigurationContext configurationContext;

    public AbstractAxis2RepositoryBasedObject(File baseDir, Axis2Config config) {
        super(baseDir);
        this.configDir = new File(baseDir, Constants.AXIS2_CONF_DIR_NAME);
        this.configFile = new File(configDir, Constants.AXIS2_CONF_XML_FILE_NAME);
        this.config = config;
        this.repository = new Axis2Repository(
                new File(baseDir, Constants.AXIS2_REPOSITORY_DIR_NAME));
        this.configurationContext = null;
    }

    @Override
    protected void specificSetUp() {
        createDirIfNeeded(configDir);
        FileWriter fw;
        try {
            fw = new FileWriter(configFile);
            config.dump(fw);
            fw.close();
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        repository.setUp();

    }

    protected final ConfigurationContext getConfigurationContext() {
        if (configurationContext == null) {
            try {
                configurationContext = ConfigurationContextFactory
                        .createConfigurationContextFromFileSystem(repository.getBaseDir()
                                .getAbsolutePath(), configFile.getAbsolutePath());
            } catch (AxisFault e) {
                throw new UncheckedException(e);
            }
        }
        return configurationContext;
    }

    protected final Axis2Repository getRepository() {
        return repository;
    }

}
