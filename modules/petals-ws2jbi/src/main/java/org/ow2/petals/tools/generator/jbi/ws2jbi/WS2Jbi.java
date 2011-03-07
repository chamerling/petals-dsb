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
package org.ow2.petals.tools.generator.jbi.ws2jbi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.easywsdl.wsdl.api.WSDLWriter;
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
public class WS2Jbi implements JBIGenerationEngine {

    private static final String CREATOR_CLASS_NAME = "org.ow2.petals.tools.generator.jbi.wscommons.Creator";

    private final Log logger = LogFactory.getLog(WS2Jbi.class);

    private final URI wsdlURI;

    private File outputDir;

    private final Map<String, String> extensions;

    private final SaBean saBean;

    private final List<SuBeanForSa> suBeans;

    public WS2Jbi(URI wsdlURI, Map<String, String> extensions) {
        this.wsdlURI = wsdlURI;
        this.extensions = extensions;

        this.saBean = new SaBean();
        this.suBeans = new ArrayList<SuBeanForSa>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.ws2jbi.Convert2JBI#convert()
     */
    public File generate() throws JBIGenerationException {
        if (this.wsdlURI == null) {
            throw new JBIGenerationException("WSDL URI is null");
        }

        // get the component version
        String componentVersion = this.extensions
                .get(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION);
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

        this.logger.info("Trying to generate JBI artefact for " + this.wsdlURI.toString());

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

        File workDirectory = new File(tmpDir, "PETALS-WS2JBI-" + System.currentTimeMillis());
        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        File saDirectory = new File(workDirectory, "sa");
        if (!saDirectory.exists()) {
            saDirectory.mkdirs();
        }

        Description desc = this.readWSDL();

        // write the WSDL to local file
        File wsdlFile = null;
        try {
            WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
            String wsdlString = writer.writeWSDL(desc);
            wsdlFile = new File(workDirectory, "Service.wsdl");
            if (!wsdlFile.exists()) {
                wsdlFile.createNewFile();
            }
            FileWriter fw = new FileWriter(wsdlFile);
            fw.write(wsdlString);
            fw.flush();
            fw.close();
        } catch (WSDLException e) {
            throw new JBIGenerationException(e);
        } catch (IOException e) {
            throw new JBIGenerationException(e);
        }

        String saName = Constants.SA_PREFIX + System.currentTimeMillis();
        if ((this.extensions != null) && (this.extensions.get(Constants.SA_NAME) != null)) {
            saName = Constants.SA_PREFIX + this.extensions.get(Constants.SA_NAME);
        }

        this.saBean.setSaName(saName);
        this.saBean.setDescription("SA generated for the WSDL file " + this.wsdlURI.toString());
        List<File> suZipFiles = new ArrayList<File>();

        // TODO : Do it for a component version...
        List<Service> services = desc.getServices();
        for (Service service : services) {
            QName serviceName = service.getQName();
            this.logger.info("Generating for service " + serviceName);

            List<Endpoint> endpoints = service.getEndpoints();
            for (Endpoint endpoint : endpoints) {
                String suName = "su-soap-" + endpoint.getName() + "-WS2JBI";
                this.logger.info("Working on endpoint " + endpoint.getName() + ", address is "
                        + endpoint.getAddress());

                String endpointPrefix = this.extensions
                        .get(org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_PREFIX) != null ? this.extensions
                        .get(
                                org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_PREFIX)
                        .toString()
                        : "";
                String endpointName = endpointPrefix + endpoint.getName();

                Map<String, String> options = new HashMap<String, String>();
                // SOAP Component things
                options
                        .put(
                                org.ow2.petals.tools.generator.jbi.wscommons.Constants.SOAP_ENDPOINT_ADDRESS,
                                endpoint.getAddress());

                // CDK and SU related things
                options.put(Creator.INTERFACE, endpoint.getBinding().getInterface().getQName()
                        .getLocalPart());
                options.put(Creator.INTERFACE_NS, endpoint.getBinding().getInterface().getQName()
                        .getNamespaceURI());
                options.put(Creator.SERVICE, serviceName.getLocalPart());
                options.put(Creator.SERVICE_NS, serviceName.getNamespaceURI());
                options.put(Creator.ENDPOINT, endpointName);
                options.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                        componentVersion);
                options.put(Creator.LINK_TYPE, "");
                options.put(Creator.SU_TYPE, "");
                options.put(Creator.TIMEOUT, "60000");
                options.put(Creator.WSDLFILE, wsdlFile.getName());

                SuBean suProvide = c.createSUProvide(options);

                // generate the jbi.xml content
                String jbiXmlForSu = JbiXmlGenerator.getInstance().generateJbiXmlFileForSu(
                        suProvide);

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
                    rootFiles.add(wsdlFile);
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
            }
        }

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
        for (File suFile : suZipFiles) {
            if (suFile.exists()) {
                suFile.delete();
            }
        }

        return saZipFile;
    }

    /**
     * Read the WSDL
     * 
     * @return
     * @throws JBIGenerationException
     */
    public Description readWSDL() throws JBIGenerationException {
        Description description = null;
        try {
            WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
            description = wsdlReader.read(this.wsdlURI.toURL());
        } catch (WSDLException e) {
            throw new JBIGenerationException(e);
        } catch (MalformedURLException e) {
            throw new JBIGenerationException(e);
        } catch (IOException e) {
            throw new JBIGenerationException(e);
        } catch (URISyntaxException e) {
            throw new JBIGenerationException(e);
        }
        return description;

    }
}
