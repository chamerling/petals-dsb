/**
 * 
 */
package org.petalslink.dsb.tools.generator.bpel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.petalslink.abslayer.service.api.Interface;
import org.petalslink.dsb.tools.generator.bpel.SOAAddress.MEP;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.easybpel.model.bpel.api.BPELProcess;
import com.ebmwebsourcing.easybpel.model.bpel.api.partnerLink.PartnerLink;
import com.ebmwebsourcing.easybpel.model.bpel.api.wsdlImports.Import;
import com.ebmwebsourcing.easybpel.model.bpel.impl.BPELFactoryImpl;

/**
 * @author chamerling
 * 
 */
public class BPELGenerator extends AbstractGeneratorEngine implements
        org.petalslink.dsb.tools.generator.bpel.Constants {

    private final Log logger = LogFactory.getLog(BPELGenerator.class);

    private static final String CREATOR_CLASS_NAME = "org.petalslink.dsb.tools.generator.bpel.Creator";

    private File bpelFile;

    private Map<String, List<SOAAddress>> bpelDescriptionFiles;

    private Map<SOAAddress, File> wsdlPartners;

    private Map<String, File> wsdls;

    public BPELGenerator(File inputFolder, File outputFolder, String componentVersion, Map<String, String> extensions) {
        super(inputFolder, outputFolder, componentVersion, extensions);
        this.wsdls = new HashMap<String, File>();
        this.bpelDescriptionFiles = new HashMap<String, List<SOAAddress>>();
        this.wsdlPartners = new HashMap<SOAAddress, File>();
    }

    public List<SU> doGenerate() throws JBIGenerationException {
        List<SU> SUs = new ArrayList<SU>();

        File bpelFile = getBPELFile();
        if (bpelFile == null || !bpelFile.isFile()) {
            throw new JBIGenerationException("Can not get any BPEL files from the working folder");
        }
        this.bpelFile = bpelFile;

        if (logger.isDebugEnabled()) {
            logger.debug("BPEL file is " + this.bpelFile.getName());
        }

        Map<String, File> wsdls = this.retrieveWsdls();
        if (wsdls.size() == 0) {
            throw new JBIGenerationException(
                    "Can not get any WSDL nor XSD files from the working folder");
        }
        this.wsdls = wsdls;

        if (logger.isDebugEnabled()) {
            logger.debug("WSDL and XSD files are :");
            for (File file : this.wsdls.values()) {
                logger.debug(" - " + file.getName());
            }
        }

        // we get all files, let's analyze...
        try {
            this.analyzeFiles();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.warn("Problem while analyzing BPEL and related files", e);
            }
            throw new JBIGenerationException("Problem while analyzing BPEL and related files", e);
        }

        // generate SUs based on BPEL and WSDLs
        Iterator<String> bpelWSDLS = bpelDescriptionFiles.keySet().iterator();
        while (bpelWSDLS.hasNext()) {
            String currentWsdlBpel = bpelWSDLS.next();
            List<SOAAddress> providesList = bpelDescriptionFiles.get(currentWsdlBpel);
            for (SOAAddress provide : providesList) {
                Map<String, String> elements = new HashMap<String, String>();
                elements.put(org.ow2.petals.tools.generator.commons.Creator.INTERFACE, provide
                        .getInterface().getLocalPart());
                elements.put(org.ow2.petals.tools.generator.commons.Creator.INTERFACE_NS, provide
                        .getInterface().getNamespaceURI());
                elements.put(org.ow2.petals.tools.generator.commons.Creator.SERVICE, provide
                        .getService().getLocalPart());
                elements.put(org.ow2.petals.tools.generator.commons.Creator.SERVICE_NS, provide
                        .getService().getNamespaceURI());
                elements.put(org.ow2.petals.tools.generator.commons.Creator.ENDPOINT,
                        provide.getEndpoint());
                elements.put(org.ow2.petals.tools.generator.commons.Constants.COMPONENT_VERSION,
                        componentVersion);
                elements.put(org.ow2.petals.tools.generator.commons.Creator.LINK_TYPE, "");
                elements.put(org.ow2.petals.tools.generator.commons.Creator.SU_TYPE, "");
                elements.put(org.ow2.petals.tools.generator.commons.Creator.TIMEOUT, "60000");
                elements.put(BPEL_FILE, this.bpelFile.getName());
                elements.put(org.ow2.petals.tools.generator.commons.Creator.WSDLFILE,
                        currentWsdlBpel);

                List<File> suZipFiles = new ArrayList<File>();
                suZipFiles.add(bpelFile);
                suZipFiles.addAll(this.wsdls.values());
                SuBean suProvide = creator.createSUProvide(elements);

                SU su = new SU();
                su.bean = suProvide;
                su.imports = suZipFiles;
                SUs.add(su);
            }
        }

        return SUs;
    }

    /**
     * @return
     */
    protected File getBPELFile() {
        File[] files = Utils.getBPELFiles(this.inputFolder);
        if (files == null || files.length == 0) {
            return null;
        }

        // get the first one...
        return files[0];
    }

    protected void analyzeFiles() throws Exception {
        BPELProcess bpel = BPELFactoryImpl.getInstance().newBPELReader()
                .readBPEL(this.bpelFile.toURI());

        if (bpel == null) {
            throw new JBIGenerationException("Can not load BPEL process model");
        }

        // Retrieve wsdls
        Iterator<PartnerLink> partnerlinks = bpel.getPartnerLinks().iterator();
        while (partnerlinks.hasNext()) {
            PartnerLink partnerlink = partnerlinks.next();

            // Retrieve MyRole interfaces (wsdls of bpel)
            if (partnerlink.getMyRole() != null) {

                org.petalslink.abslayer.service.api.PartnerLinkType plt = bpel.getImports()
                        .getPartnerLinkType(partnerlink.getPartnerLinkType());

                if (plt != null) {
                    QName partnerIinterface = plt.getRole(partnerlink.getMyRole())
                            .getInterfaceQName();

                    if (logger.isInfoEnabled()) {
                        logger.info("Found a myrole interface (soap consume) " + partnerIinterface);
                    }

                    Iterator<Import> itImports = bpel.getImports().getBPELImports().iterator();
                    while (itImports.hasNext()) {
                        Import currentImport = itImports.next();
                        // TODO Bad workaround ...
                        if (currentImport.getLocation().toString().endsWith("rtifacts.wsdl")) {
                            continue;
                        }
                        if (currentImport.getNamespace().compareTo(
                                URI.create(partnerIinterface.getNamespaceURI())) == 0) {

                            Interface interf = currentImport.getDescription().findInterface(
                                    partnerIinterface);

                            if (interf != null) {
                                org.petalslink.abslayer.service.api.Service service = Utils
                                        .findService(
                                                interf,
                                                (Collection<org.petalslink.abslayer.service.api.Service>) currentImport
                                                        .getDescription().getServices());

                                if (service != null) {
                                    org.petalslink.abslayer.service.api.Endpoint endpoint = Utils
                                            .findEndpoint(interf, service.getEndpoints());

                                    if (endpoint != null) {
                                        SOAAddress adr = new SOAAddress(endpoint.getName(),
                                                service.getQName(), interf.getQName());

                                        adr.setOperation(new QName(interf.getOperations()[0]
                                                .getParentInterface().getQName().getNamespaceURI(),
                                                interf.getOperations()[0].getName()));
                                        MEP mep = MEP.InOnly;
                                        if (interf.getOperations()[0].getOutput() != null) {
                                            mep = MEP.InOut;
                                        }
                                        adr.setMep(mep);
                                        addWsdlSOAAddress(currentImport.getLocation().toString(),
                                                adr);

                                        if (logger.isInfoEnabled()) {
                                            logger.info(partnerIinterface + " ok");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                org.petalslink.abslayer.service.api.PartnerLinkType plt = bpel.getImports()
                        .getPartnerLinkType(partnerlink.getPartnerLinkType());
                if (plt != null) {

                    QName partnerIinterface = plt.getRole(partnerlink.getPartnerRole())
                            .getInterfaceQName();

                    if (logger.isInfoEnabled()) {
                        logger.info("Found a partnerrole interface (may be a soap provide) "
                                + partnerIinterface);
                    }
                    Iterator<Import> itImports = bpel.getImports().getBPELImports().iterator();
                    if (logger.isInfoEnabled()) {
                        for (Import imp : bpel.getImports().getBPELImports()) {
                            logger.info(" - Import list item : " + imp.getLocation());
                        }
                    }

                    while (itImports.hasNext()) {
                        Import currentImport = itImports.next();
                        if (currentImport.getNamespace().compareTo(
                                URI.create(partnerIinterface.getNamespaceURI())) == 0) {
                            if (currentImport.getDescription() != null) {
                                Interface interf = currentImport.getDescription().findInterface(
                                        (QName.valueOf(partnerIinterface.toString())));

                                if (interf != null) {
                                    org.petalslink.abslayer.service.api.Service service = Utils
                                            .findService(
                                                    interf,
                                                    (Collection<org.petalslink.abslayer.service.api.Service>) currentImport
                                                            .getDescription().getServices());

                                    if (service != null) {
                                        org.petalslink.abslayer.service.api.Endpoint endpoint = Utils
                                                .findEndpoint(interf, service.getEndpoints());

                                        if (endpoint != null) {
                                            SOAAddress adr = new SOAAddress(endpoint.getName(),
                                                    service.getQName(), interf.getQName());

                                            adr.setAddress(endpoint.getAddress());

                                            this.wsdlPartners.put(adr, new File(currentImport
                                                    .getLocation().toString()));
                                            if (logger.isInfoEnabled()) {
                                                logger.info(partnerIinterface + " ok");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addWsdlSOAAddress(String wsdl, SOAAddress soa) {
        List<SOAAddress> value = this.bpelDescriptionFiles.get(wsdl);
        if (value != null && !value.contains(soa)) {
            value.add(soa);
        } else {
            List<SOAAddress> temp = new ArrayList<SOAAddress>();
            temp.add(soa);
            this.bpelDescriptionFiles.put(wsdl, temp);
        }
    }

    /**
     * 
     */
    protected Map<String, File> retrieveWsdls() {
        Map<String, File> result = new HashMap<String, File>();
        File[] wsdls = Utils.getWSDLFiles(this.inputFolder);
        if (wsdls != null) {
            for (File file : wsdls) {
                result.put(file.getName(), file);
            }
        }
        File[] xsds = Utils.getXSDFiles(this.inputFolder);
        if (xsds != null) {
            for (File file : xsds) {
                result.put(file.getName(), file);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine#getSUName
     * (org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine.SU)
     */
    @Override
    protected String getSUName(SU su) {
        return "SU-BPEL-provide-" + su.bean.getEndpointName() + "_" + su.bean.getInterfaceName()
                + "_" + su.bean.getServiceName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine#
     * getCreatorClassName()
     */
    @Override
    protected String getCreatorClassName() {
        return CREATOR_CLASS_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine#getSAName
     * ()
     */
    @Override
    protected String getSAName() {
        return "SA-BPELGEN-" + System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine#
     * getSADescription()
     */
    @Override
    protected String getSADescription() {
        return "A SA generated from input files";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractGeneratorEngine#
     * notNullInputFolder()
     */
    @Override
    protected boolean notNullInputFolder() {
        return true;
    }

}
