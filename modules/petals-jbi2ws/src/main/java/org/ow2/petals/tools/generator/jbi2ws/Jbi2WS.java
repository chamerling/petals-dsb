/*
 * Copyright (c) 2009 EBM Websourcing, http://www.ebmwebsourcing.com/
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
 */
package org.ow2.petals.tools.generator.jbi2ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.tools.generator.commons.CreatorFactory;
import org.ow2.petals.tools.generator.commons.GeneratorException;
import org.ow2.petals.tools.generator.commons.JBIUtils;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationEngine;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SaBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBeanForSa;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiXmlGenerator;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiZipper;

/**
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class Jbi2WS implements JBIGenerationEngine {

    private static final String CREATOR_CLASS_NAME = "org.ow2.petals.tools.generator.jbi.wscommons.Creator";

    private final Map<String, String> extensions;

    private final SaBean saBean;

    private final List<SuBeanForSa> suBeans;

    private File outputDir;

    private final String endpoint;

    private final QName service;

    private final QName itf;

    private final Log logger = LogFactory.getLog(Jbi2WS.class);

    public Jbi2WS(String endpoint, QName service, QName itf, Map<String, String> extensions) {
        this.endpoint = endpoint;
        this.service = service;
        this.itf = itf;
        this.extensions = extensions;

        this.saBean = new SaBean();
        this.suBeans = new ArrayList<SuBeanForSa>();
    }

    public File generate() throws JBIGenerationException {
        this.logger.info("Trying to JBI WS artefact for endpoint " + this.endpoint + " / "
                + this.service + " / " + this.itf);

        String componentVersion = this.extensions != null ? this.extensions
                .get(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION) : null;

        // TODO : Need to update API...
        if (componentVersion == null) {
            throw new JBIGenerationException("Component version is null");
        }

        // lets get the creator before all...
        org.ow2.petals.tools.generator.commons.Creator c = CreatorFactory.getInstance().getCreator(
                componentVersion, CREATOR_CLASS_NAME);
        if (c == null) {
            throw new JBIGenerationException(
                    "Can not find a valid creator in the classpath for component version "
                            + componentVersion);
        }

        if ((this.extensions != null)
                && (this.extensions
                        .get(org.ow2.petals.tools.generator.commons.Constants.OUTPUT_DIR) != null)) {
            this.outputDir = new File(this.extensions
                    .get(org.ow2.petals.tools.generator.commons.Constants.OUTPUT_DIR));
            if (!this.outputDir.exists()) {
                this.outputDir.mkdirs();
            }
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

        File workDirectory = new File(tmpDir, "PETALS-JBI2WS-" + System.currentTimeMillis());
        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        File saDirectory = new File(workDirectory, "sa");
        if (!saDirectory.exists()) {
            saDirectory.mkdirs();
        }

        String saName = Constants.SA_PREFIX + System.currentTimeMillis();
        if ((this.extensions != null)
                && (this.extensions.get(org.ow2.petals.tools.generator.commons.Constants.SA_NAME) != null)) {
            saName = Constants.SA_PREFIX
                    + this.extensions.get(org.ow2.petals.tools.generator.commons.Constants.SA_NAME);
        }

        this.saBean.setSaName(saName);
        this.saBean.setDescription("SA generated for the endpoint " + this.endpoint);
        List<File> suZipFiles = new ArrayList<File>();
        QName serviceName = this.service;
        String suConsumerName = "su-consumer-" + this.endpoint + "-JBI2WS";

        this.logger.info("Generating provider for service " + serviceName);

        String itfName = null;
        String itfNS = null;
        if (this.itf != null) {
            itfName = this.itf.getLocalPart();
            itfNS = this.itf.getNamespaceURI();
        }
        String srvName = null;
        String srvNS = null;
        if (this.service != null) {
            srvName = this.service.getLocalPart();
            srvNS = this.service.getNamespaceURI();
        }

        String soapServiceName = ((this.extensions != null) && (this.extensions
                .get(org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_ADDRESS) != null)) ? this.extensions
                .get(org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_ADDRESS)
                .toString()
                + "Service"
                : this.endpoint + "Service";

        Map<String, String> elements = new HashMap<String, String>();
        // SOAP related parameters
        elements.put(org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_ADDRESS,
                soapServiceName);

        // CDK and SU related parameters
        elements.put(org.ow2.petals.tools.generator.commons.Creator.INTERFACE, itfName);
        elements.put(org.ow2.petals.tools.generator.commons.Creator.INTERFACE_NS, itfNS);
        elements.put(org.ow2.petals.tools.generator.commons.Creator.SERVICE, srvName);
        elements.put(org.ow2.petals.tools.generator.commons.Creator.SERVICE_NS, srvNS);
        elements.put(org.ow2.petals.tools.generator.commons.Creator.ENDPOINT, this.endpoint);
        elements.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                componentVersion);
        elements.put(org.ow2.petals.tools.generator.commons.Creator.LINK_TYPE, "");
        elements.put(org.ow2.petals.tools.generator.commons.Creator.SU_TYPE, "");
        elements.put(org.ow2.petals.tools.generator.commons.Creator.TIMEOUT, "60000");

        SuBean suProvide = c.createSUConsume(elements);

        String jbiXmlForSuProvide = JbiXmlGenerator.getInstance()
                .generateJbiXmlFileForSu(suProvide);

        File suFileProvide = null;
        try {
            suFileProvide = JBIUtils.createSUZipFile(null, null, suZipFiles, suConsumerName,
                    jbiXmlForSuProvide, workDirectory, this.outputDir);
        } catch (GeneratorException e1) {
            throw new JBIGenerationException(e1);
        }

        SuBeanForSa suBeanForSaP = new SuBeanForSa(suProvide);
        suBeanForSaP.setZipArtifact(suFileProvide.getName());
        suBeanForSaP.setSuName(suConsumerName);
        suBeanForSaP.setComponentName(c.getComponentName());

        this.suBeans.add(suBeanForSaP);

        // create the SA from the SUs
        this.saBean.setSus(this.suBeans);
        String jbiXmlForSa = JbiXmlGenerator.getInstance().generateJbiXmlFileForSa(this.saBean);
        File jbiSa = new File(saDirectory, "jbi.xml");
        File saZipFile = null;
        try {
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
        for (File suFile : suZipFiles) {
            if (suFile.exists()) {
                suFile.delete();
            }
        }
        return saZipFile;
    }

}
