/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.tools.generator.rest2jbi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.commons.Creator;
import org.ow2.petals.tools.generator.commons.CreatorFactory;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationEngine;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SaBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBeanForSa;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiXmlGenerator;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiZipper;

/**
 * Create a Service Assembly to expose a Web Service as JBI Service
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class REST2Jbi implements JBIGenerationEngine {

    private static final String CREATOR_CLASS_NAME = "org.ow2.petals.tools.generator.jbi.restcommons.Creator";

    private static final String SA_PREFIX = "SA-REST2JBI-";

    private final Log logger = LogFactory.getLog(REST2Jbi.class);

    private final URI restURI;

    private File outputDir;

    private final Map<String, String> extensions;

    private final SaBean saBean;

    private final List<SuBeanForSa> suBeans;

    private String endpointName;

    public REST2Jbi(URI restURI, String endpointName, Map<String, String> extensions) {
        this.restURI = restURI;
        this.endpointName = endpointName;
        this.extensions = extensions;

        this.saBean = new SaBean();
        this.suBeans = new ArrayList<SuBeanForSa>();
    }

    public File generate() throws JBIGenerationException {
        if (this.restURI == null) {
            throw new JBIGenerationException("REST URI is null");
        }

        // get the component version
        String componentVersion = this.extensions.get(Constants.COMPONENT_VERSION);
        if (componentVersion == null) {
            throw new JBIGenerationException("Component version is null");
        }

        // lets get the creator before all...
        Creator c = CreatorFactory.getInstance().getCreator(componentVersion, CREATOR_CLASS_NAME);
        if (c == null) {
            throw new JBIGenerationException(
                    "Can not find a valid creator in the classpath for component version "
                            + componentVersion);
        }

        this.logger.info("Trying to generate JBI artefact for REST Service at "
                + this.restURI.toString());

        if ((this.extensions != null) && (this.extensions.get(Constants.OUTPUT_DIR) != null)) {
            this.outputDir = new File(this.extensions.get(Constants.OUTPUT_DIR));
        }
        if (this.outputDir == null) {
            this.outputDir = new File(".");
        }

        this.logger.info("The JBI artefact file will be generated in "
                + this.outputDir.getAbsolutePath());

        // temp resources
        File tmpDir = null;
        try {
            tmpDir = File.createTempFile("petalstmpdir", "txt").getParentFile();
        } catch (IOException e1) {
            throw new JBIGenerationException(e1);
        }

        File workDirectory = new File(tmpDir, "PETALS-REST2JBI-" + System.currentTimeMillis());
        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        File saDirectory = new File(workDirectory, "sa");
        if (!saDirectory.exists()) {
            saDirectory.mkdirs();
        }

        String saName = SA_PREFIX + System.currentTimeMillis();
        if ((this.extensions != null) && (this.extensions.get(Constants.SA_NAME) != null)) {
            saName = SA_PREFIX + this.extensions.get(Constants.SA_NAME);
        }

        this.saBean.setSaName(saName);
        this.saBean.setDescription("SA generated for the REST URL " + this.restURI.toString());
        List<File> suZipFiles = new ArrayList<File>();

        String epName = this.endpointName;
        if (epName == null) {
            epName = this.restURI.toString();
            if (epName.startsWith("http://")) {
                epName = epName.substring("http://".length(), epName.length());
            } else if (epName.startsWith("https://")) {
                epName = epName.substring("https://".length(), epName.length());
            }
            epName = epName.replaceAll("\\.", "");

            epName = epName.replaceAll("\\:", "");
            epName = epName.replaceAll("\\/", "");
            this.endpointName = epName + "Endpoint";
        }

        // add a prefix to endpoint name if specified in extensions...
        String prefix = (this.extensions
                .get(org.ow2.petals.tools.generator.jbi.restcommons.Constants.REST_ENDPOINT_PREFIX) != null ? this.extensions
                .get(org.ow2.petals.tools.generator.jbi.restcommons.Constants.REST_ENDPOINT_PREFIX)
                .toString()
                : "");

        this.endpointName = prefix + this.endpointName;

        String serviceName = epName + "Service";
        String interfaceName = epName + "Interface";
        String namespace = this.restURI.toString();

        String suName = "su-rest-" + this.endpointName + "-REST2JBI";
        Map<String, String> options = new HashMap<String, String>();
        // SOAP Component things
        options.put(org.ow2.petals.tools.generator.jbi.restcommons.Constants.REST_ENDPOINT_ADDRESS,
                this.restURI.toString());

        // CDK and SU related things
        options.put(Creator.INTERFACE, interfaceName);
        options.put(Creator.INTERFACE_NS, namespace);
        options.put(Creator.SERVICE, serviceName);
        options.put(Creator.SERVICE_NS, namespace);
        options.put(Creator.ENDPOINT, this.endpointName);
        options.put(Constants.COMPONENT_VERSION, componentVersion);
        options.put(Creator.LINK_TYPE, "");
        options.put(Creator.SU_TYPE, "");
        options.put(Creator.TIMEOUT, "60000");
        options.put(Creator.WSDLFILE, null);

        SuBean suProvide = c.createSUProvide(options);

        // generate the jbi.xml content
        String jbiXmlForSu = JbiXmlGenerator.getInstance().generateJbiXmlFileForSu(suProvide);

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Generated JBI :");
            this.logger.info(jbiXmlForSu);
        }

        // write jbi descriptor to file
        File suFile = null;
        File jbiSu = null;
        try {
            File tempSuDirectory = new File(workDirectory, suName);
            if (!tempSuDirectory.exists()) {
                tempSuDirectory.mkdirs();
            }

            jbiSu = new File(tempSuDirectory, "jbi.xml");
            if (!jbiSu.exists()) {
                jbiSu.createNewFile();
            }

            FileWriter writer = new FileWriter(jbiSu);
            writer.write(jbiXmlForSu);
            writer.close();

            // create the SU
            JbiZipper jbiZipper = JbiZipper.getInstance();
            List<File> rootFiles = new ArrayList<File>();
            rootFiles.add(jbiSu);
            suFile = new File(this.outputDir, suName + ".zip");
            suZipFiles.add(jbiZipper.createSuZipFile(suFile, rootFiles));
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            throw new JBIGenerationException(e);
        }

        SuBeanForSa suBeanForSa = new SuBeanForSa(suProvide);
        suBeanForSa.setZipArtifact(suFile.getName());
        suBeanForSa.setSuName(suName);
        suBeanForSa.setComponentName(c.getComponentName());
        this.suBeans.add(suBeanForSa);
        jbiSu.delete();

        // create the SA from the SUs
        this.saBean.setSus(this.suBeans);
        String jbiXmlForSa = JbiXmlGenerator.getInstance().generateJbiXmlFileForSa(this.saBean);

        File jbiSa = new File(saDirectory, "jbi.xml");
        File saZipFile = null;
        try {
            // : convert to UTF-8 (date can cause problems ...)
            byte[] utf8 = jbiXmlForSa.getBytes("UTF-8");
            FileWriter writer = new FileWriter(jbiSa);
            writer.write(new String(utf8));
            writer.close();
            JbiZipper jbiZipper = JbiZipper.getInstance();

            // Save the SA zip file in the repository
            File saFile = new File(this.outputDir, saName + ".zip");
            saZipFile = jbiZipper.createSaZipFile(saFile, suZipFiles, jbiSa);
        } catch (IOException e) {
            throw new JBIGenerationException(e);
        }

        // delete temp files
        jbiSa.delete();
        for (File su : suZipFiles) {
            if (su.exists()) {
                su.delete();
            }
        }

        return saZipFile;
    }
}
