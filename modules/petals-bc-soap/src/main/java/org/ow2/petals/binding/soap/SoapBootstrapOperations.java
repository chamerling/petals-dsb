/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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

package org.ow2.petals.binding.soap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.jbi.JBIException;
import javax.jbi.component.InstallationContext;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.AXIS2_XML;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.MODULES_PATH;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.MODULE_ARCHIVE_EXTENSION;

/**
 * The operations which are exposed during bootstrap phase
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class SoapBootstrapOperations {

    /**
     * The Axis2 installation context
     */
    private final InstallationContext installContext;

    /**
     * Creates a new instance of {@link SoapBootstrapOperations}
     * 
     * @param installContext
     */
    public SoapBootstrapOperations(final InstallationContext installContext) {
        this.installContext = installContext;
    }

    /**
     * Set a SOAP parameter.
     * 
     * @param name
     * @param value
     * @param jbi
     */
    public void setParam(String name, String value, Jbi jbi) {
        for (Element element : jbi.getComponent().getAny()) {
            if (element.getLocalName().equals(name)) {
                element.setTextContent(value);
            }
        }
    }

    /**
     * Get a SOAP parameter.
     * 
     * @param name
     * @param value
     * @param jbi
     */
    public String getParam(String name, Jbi jbi) {
        for (Element element : jbi.getComponent().getAny()) {
            if (element.getLocalName().equals(name)) {
                return element.getTextContent();
            }
        }
        
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.petals.binding.soap.SoapConfigurationMBean#addModule(java
     * .lang.String)
     */
    public String addModule(final String str) throws JBIException {
        String result = "Can not add module '" + str + "' to Axis2";

        File file = null;
        try {
            new URL(str);
            final URI uri = new URI(str);
            file = new File(uri);
        } catch (final URISyntaxException e1) {
            throw new JBIException("Bad URI : " + str);
        } catch (final MalformedURLException e) {
            throw new JBIException("Malformed URL : " + str);
        }

        if (file.exists() && !file.isDirectory()) {
            try {
                this.addModule(file);
                result = "Module '" + str + "' successfully added to Axis2";
            } catch (final Exception e) {
                result = result + "\n" + e.getMessage();
            }
        }

        return result;
    }

    /**
     * Add module to axis context and to the axis2.xml config file.
     * 
     * @param module
     * @throws Exception
     */
    private void addModule(final File moduleFile) throws Exception {
        if ((moduleFile == null) || !moduleFile.exists()) {
            throw new IllegalArgumentException("Bad module file");
        }
        
        final String workspaceRootDir = this.installContext.getContext().getWorkspaceRoot();
        final File modules = new File(workspaceRootDir, MODULES_PATH);
        final File configFile = new File(workspaceRootDir, AXIS2_XML);

        // get module name from file
        final String moduleName = moduleFile.getName().substring(0,
                moduleFile.getName().lastIndexOf('.'));

        // check if destination file exists
        final File destFile = new File(modules, moduleFile.getName());
        if (destFile.exists()) {
            throw new Exception("Module '" + moduleName + "' already exists in destination folder");
        }

        if (moduleFile.exists()) {
            if (moduleFile.getName().endsWith("." + MODULE_ARCHIVE_EXTENSION)) {

                // copy the module into the modules directory
                FileUtils.copyFile(moduleFile, destFile);

                // add module name to the module section
                final InputStream inputStream = new FileInputStream(configFile);
                final Document document = XMLUtil.loadDocument(inputStream);
                final Element rootElement = document.getDocumentElement();
                final Element module = document.createElement("module");
                module.setAttribute("ref", moduleName);
                rootElement.appendChild(module);

                XMLHelper.writeDocument(document, new FileOutputStream(configFile));

            } else {
                throw new FileNotFoundException("Module file source does not exists");
            }
        } else {
            throw new FileNotFoundException("Source module does not exists");
        }
    }
}
