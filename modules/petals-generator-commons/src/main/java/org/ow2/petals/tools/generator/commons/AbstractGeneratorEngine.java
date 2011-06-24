/**
 * 
 */
package org.ow2.petals.tools.generator.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationEngine;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SaBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBeanForSa;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiXmlGenerator;

/**
 * @author chamerling
 * 
 */
public abstract class AbstractGeneratorEngine implements JBIGenerationEngine {

    private final Log logger = LogFactory.getLog(AbstractGeneratorEngine.class);

    protected File inputFolder;

    protected File outputFolder;

    protected SaBean saBean;

    protected List<SuBeanForSa> suBeans;

    protected String componentVersion;

    protected org.ow2.petals.tools.generator.commons.Creator creator;

    protected Map<String, String> extensions;

    /**
     * 
     */
    public AbstractGeneratorEngine(File inputFolder, File outputFolder, String componentVersion,
            Map<String, String> extensions) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.componentVersion = componentVersion;
        if (extensions != null) {
            this.extensions = extensions;
        } else {
            this.extensions = new HashMap<String, String>();
        }

        this.saBean = new SaBean();
        this.suBeans = new ArrayList<SuBeanForSa>(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.generator.jbi.api.JBIGenerationEngine#generate()
     */
    public File generate() throws JBIGenerationException {
        if (this.componentVersion == null) {
            throw new JBIGenerationException("Component version is null");
        }

        // lets get the creator before all...
        creator = CreatorFactory.getInstance().getCreator(this.componentVersion,
                getCreatorClassName());
        if (creator == null) {
            throw new JBIGenerationException(
                    "Can not find a valid creator in the classpath for component version "
                            + this.componentVersion);
        }

        // check input
        if (notNullInputFolder() && inputFolder == null) {
            throw new JBIGenerationException("Input folder is required and is null!");
        }

        if (notNullInputFolder() && !inputFolder.isDirectory()) {
            throw new JBIGenerationException(
                    "Input folder is required and seems to not be a folder...");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Input folder : " + this.inputFolder.getAbsolutePath());
            File[] files = this.inputFolder.listFiles();
            for (File file : files) {
                logger.debug("  - File " + file.getAbsolutePath());
            }
        }

        // check output
        if (this.outputFolder != null) {
            if (!this.outputFolder.exists()) {
                this.outputFolder.mkdirs();
            }
        } else {
            this.outputFolder = new File(".");
        }

        File tmpDir = null;
        try {
            tmpDir = File.createTempFile("petalstmpdir", "txt").getParentFile();
        } catch (IOException e1) {
            throw new JBIGenerationException(e1);
        }

        File workDirectory = new File(tmpDir, "PETALS-JBIGEN4-" + creator.getComponentName() + "v"
                + creator.getComponentVersion() + "-at-" + System.currentTimeMillis());
        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        File saDirectory = new File(workDirectory, "sa");
        if (!saDirectory.exists()) {
            saDirectory.mkdirs();
        }

        this.logger.info("The JBI artefact file will be generated in "
                + this.outputFolder.getAbsolutePath());

        this.saBean.setDescription(getSADescription());
        this.saBean.setSaName(getSAName());

        // Now we need to generate SU things at the implementation level based
        // on inputs and parameters...
        List<SU> SUs = doGenerate();

        // now that we have all filled, let's build things...
        List<File> suZipFiles = new ArrayList<File>(SUs.size());
        for (SU su : SUs) {
            String jbiXmlForSu = JbiXmlGenerator.getInstance().generateJbiXmlFileForSu(su.bean);

            File suFileProvide = null;
            String suName = getSUName(su);
            try {
                suFileProvide = JBIUtils.createSUZipFile(null, su.imports, suZipFiles, suName,
                        jbiXmlForSu, workDirectory, this.outputFolder);
            } catch (GeneratorException e1) {
                throw new JBIGenerationException(e1);
            }

            SuBeanForSa suBeanForSaP = new SuBeanForSa(su.bean);
            suBeanForSaP.setZipArtifact(suFileProvide.getName());
            suBeanForSaP.setSuName(suName);
            suBeanForSaP.setComponentName(creator.getComponentName());
            this.suBeans.add(suBeanForSaP);
        }

        // create the SA from the SUs
        this.saBean = new SaBean();
        this.saBean.setSus(this.suBeans);
        this.saBean.setSaName(getSAName());
        this.saBean.setDescription(getSADescription());
        String jbiXmlForSa = JbiXmlGenerator.getInstance().generateJbiXmlFileForSa(this.saBean);

        File jbiSa = new File(saDirectory, "jbi.xml");
        File saZipFile;
        try {
            saZipFile = JBIUtils.createSAZipFile(suZipFiles, getSAName(), jbiXmlForSa,
                    this.outputFolder, saDirectory);
        } catch (GeneratorException e) {
            throw new JBIGenerationException("Can not generate SA", e);
        } finally {
            // delete temp files
            jbiSa.delete();
            for (File suFile : suZipFiles) {
                if (suFile.exists()) {
                    suFile.delete();
                }
            }
        }
        return saZipFile;
    }

    /**
     * Clean all generated things
     */
    protected void clean() {

    }

    /**
     * @param su
     * @return
     */
    protected abstract String getSUName(SU su);

    /**
     * @return
     */
    protected abstract String getCreatorClassName();

    /**
     * 
     * @return
     */
    protected abstract String getSAName();

    /**
     * 
     * @return
     */
    protected abstract String getSADescription();

    /**
     * To be overrided if the inputfolder is needed
     * 
     * @return true is input folder is required
     */
    protected abstract boolean notNullInputFolder();

    /**
     * @return
     */
    protected abstract List<SU> doGenerate() throws JBIGenerationException;

    public class SU {
        public SuBean bean;

        public List<File> imports;
    }

}
