
package org.ow2.petals.binding.soap.axis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

public class Axis2Repository extends AbstractAxis2DirBasedObject {

    private final File modulesDir;

    private final File servicesDir;

    public Axis2Repository(File baseDir) {
        super(baseDir);
        this.modulesDir = new File(baseDir, Constants.AXIS2_REPOSITORY_MODULES_DIR_NAME);
        this.servicesDir = new File(baseDir, Constants.AXIS2_REPOSITORY_SERVICES_DIR_NAME);
    }

    public void specificSetUp() {
        createDirIfNeeded(modulesDir);
        createDirIfNeeded(servicesDir);
    }

    public void deployService(ServiceConfig serviceConfig) {
        assert isSetUp() : "Repository must first be set up before deploying a service.";

        File serviceDir = new File(servicesDir, serviceConfig.getName());
        assert !serviceDir.exists() : 
            String.format("Repository already contains a service called '%s'.", serviceConfig.getName());
        createDirIfNeeded(serviceDir);

        File serviceMetaInfDir = new File(serviceDir, Constants.AXIS2_SERVICES_XML_METAINF_DIR_NAME);
        createDirIfNeeded(serviceMetaInfDir);
        File serviceXmlFile = new File(serviceMetaInfDir, Constants.AXIS2_SERVICES_XML_FILE_NAME);
        FileWriter fw;
        try {
            fw = new FileWriter(serviceXmlFile);
            serviceConfig.dump(fw);
            fw.close();
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
    }
}
