/**
 * 
 */
package org.petalslink.dsb.tools.generator.poller2jbi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.commons.Creator;
import org.ow2.petals.tools.generator.commons.CreatorFactory;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationEngine;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.w3c.dom.Document;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SaBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBeanForSa;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiXmlGenerator;
import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiZipper;

/**
 * @author chamerling
 * 
 */
public class Poller2Jbi implements JBIGenerationEngine {

    private static final String CREATOR_CLASS_NAME = "org.petalslink.dsb.tools.generator.poller2jbi.Creator";

    private static final String SA_PREFIX = "SA-SERVICE_POLLER";

    private final Log logger = LogFactory.getLog(Poller2Jbi.class);

    private File outputDir;

    private String endpointName;

    private QName interfaceName;

    private QName serviceName;

    private Document inputDocument;

    private String responseEndpointName;

    private QName responseInterfaceName;

    private QName responseServiceName;

    private String cronExpression;

    private SaBean saBean;

    private ArrayList<SuBeanForSa> suBeans;

    private Map<String, String> extensions;

    private QName operation;

    private QName responseOperation;

    public Poller2Jbi(String endpointName, QName interfaceName, QName serviceName, QName operation,
            Document inputDocument, String responseEndpointName, QName responseInterfaceName,
            QName responseServiceName, QName responseOperation, String cronExpression,
            Map<String, String> extensions) {
        this.endpointName = endpointName;
        this.interfaceName = interfaceName;
        this.serviceName = serviceName;
        this.inputDocument = inputDocument;
        this.responseEndpointName = responseEndpointName;
        this.responseInterfaceName = responseInterfaceName;
        this.responseServiceName = responseServiceName;
        this.extensions = extensions;
        this.cronExpression = cronExpression;
        this.responseOperation = responseOperation;
        this.operation = operation;

        this.saBean = new SaBean();
        this.suBeans = new ArrayList<SuBeanForSa>();
    }

    public File generate() throws JBIGenerationException {

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

        // create the input file with the given Document as content
        File inputFile = new File(workDirectory, "payload.xml");
        Source source = new DOMSource(inputDocument);
        Result result = new StreamResult(inputFile);
        try {
            TransformerFactory.newInstance().newTransformer().transform(source, result);
        } catch (Exception e1) {
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
        this.saBean.setDescription("SA generated for the Service Poller");
        List<File> suZipFiles = new ArrayList<File>();

        String suName = "su-servicepoller-" + this.endpointName + "-Poller2JBI";
        Map<String, String> options = new HashMap<String, String>();
        // CDK and SU related things
        options.put(Creator.INTERFACE, interfaceName.getLocalPart());
        options.put(Creator.INTERFACE_NS, interfaceName.getNamespaceURI());

        options.put(Creator.SERVICE, serviceName.getLocalPart());
        options.put(Creator.SERVICE_NS, serviceName.getNamespaceURI());

        options.put(Creator.ENDPOINT, this.endpointName);
        // options.put(Creator.ENDPOINT_NS, this.endpointName);

        options.put(Constants.COMPONENT_VERSION, componentVersion);
        options.put(Creator.LINK_TYPE, "");
        options.put(Creator.SU_TYPE, "");
        options.put(Creator.TIMEOUT, "60000");
        options.put(Creator.WSDLFILE, null);
        options.put(Creator.OPERATION, this.operation.getLocalPart());
        // specific elements
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.RESPONSE_ENDPOINT,
                responseEndpointName);
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.RESPONSE_INTERFACE,
                responseInterfaceName.getLocalPart());
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.RESPONSE_SERVICE,
                responseServiceName.getLocalPart());
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.RESPONSE_OPERATION,
                responseOperation.getLocalPart());
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.CRON_EXPRESSION,
                cronExpression);
        options.put(org.petalslink.dsb.tools.generator.poller2jbi.Constants.INPUT_FILE,
                inputFile.getName());

        SuBean suProvide = c.createSUConsume(options);

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
            rootFiles.add(inputFile);
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
