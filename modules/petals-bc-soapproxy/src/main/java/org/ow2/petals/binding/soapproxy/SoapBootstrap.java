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

package org.ow2.petals.binding.soapproxy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jbi.JBIException;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.component.framework.DefaultBootstrap;

import static org.ow2.petals.binding.soapproxy.Constants.Axis2.AXIS2_XML;
import static org.ow2.petals.binding.soapproxy.Constants.Axis2.MODULES_PATH;
import static org.ow2.petals.binding.soapproxy.Constants.Axis2.MODULE_ARCHIVE_EXTENSION;
import static org.ow2.petals.binding.soapproxy.Constants.Axis2.SERVICES_PATH;

/**
 * The bootstrap class. Copy all the configuration files in the good
 * directories.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class SoapBootstrap extends DefaultBootstrap {

    protected SoapBootstrapOperations bootstrapOperations;

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.DefaultBootstrap#doInit()
     */
    @Override
    protected void doInit() throws JBIException {
        super.doInit();
        this.createWorkDirectories();
        this.copyConfigurationFiles();

        if (this.bootstrapOperations == null) {
            this.bootstrapOperations = new SoapBootstrapOperations(this.installContext);
        }
    }

    /**
     * Create the directories that will be used by axis
     * 
     * @param installPath
     */
    protected void createWorkDirectories() {
        final File modules = new File(this.installContext.getInstallRoot(), MODULES_PATH);
        final File services = new File(this.installContext.getInstallRoot(), SERVICES_PATH);
        modules.mkdirs();
        services.mkdirs();
    }

    /**
     * Copy the needed configuration files to the Axis2 directories. Do not copy
     * them if the files are already present (the install phase has already been
     * done and we are now in the uninstall phase).
     * 
     */
    protected void copyConfigurationFiles() throws JBIException {

        final File installRootFile = new File(this.installContext.getInstallRoot());

        /* copy required files from META-INF directory */
        final File modules = new File(installRootFile, MODULES_PATH);
        final File[] metaInfFiles = new File(installRootFile, "META-INF").listFiles();

        for (final File file : metaInfFiles) {
            // copy axis2.xml file
            if (file.getName().equals(AXIS2_XML)) {
                final File destFile = new File(this.installContext.getInstallRoot(), file.getName());
                if (!destFile.exists()) {
                    try {
                        FileUtils.copyFile(file, destFile);
                    } catch (final IOException e) {
                        throw new JBIException(
                                "Can not copy the axis2 configuration file, axis2 will not start properly");
                    }
                }
            }
        }

        // copy modules from component root path
        final File[] moduleFiles = installRootFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return (pathname != null)
                        && pathname.getName().endsWith("." + MODULE_ARCHIVE_EXTENSION);
            }
        });

        for (final File file : moduleFiles) {
            final File destFile = new File(modules, file.getName());
            if (!destFile.exists()) {
                try {
                    FileUtils.copyFile(file, destFile);
                } catch (final IOException e) {
                    this.getLogger().warning(
                            "The module '" + file.getName()
                                    + "' has not been copied to modules directory");
                    // do nothing
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.DefaultBootstrap#getMethodList()
     */
    @Override
    protected List<String> getMethodList() {
        final List<String> methods = new ArrayList<String>();
        // expose addModule for management
        methods.add("addModule");

        return methods;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.DefaultBootstrap#getAttributeList()
     */
    @Override
    protected List<String> getAttributeList() {
        final List<String> attributes = new ArrayList<String>();
        attributes.add("httpPort");
        attributes.add("httpHostName");
        attributes.add("httpServicesList");
        attributes.add("httpServicesContext");
        attributes.add("httpServicesMapping");
        attributes.add("httpThreadPoolSizeMin");
        attributes.add("httpThreadPoolSizeMax");
        attributes.add("httpAcceptors");

        return attributes;
    }

    /**
     * Add a module to the Axis2 engine. This method is used by the
     * configuration MBean.
     * 
     * @param url
     * @return
     * @throws JBIException
     */
    public String addModule(final String url) throws Exception {
        return this.bootstrapOperations.addModule(url);
    }

    /**
     * Set the HTTP port.
     * 
     * @param httpPort
     */
    public void setHttpPort(final int httpPort) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_PORT, Integer
                .toString(httpPort), this.getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP host name.
     * 
     * @param httpHostName
     */
    public void setHttpHostName(final String httpHostName) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_HOSTNAME, httpHostName, this
                .getJbiComponentConfiguration());
    }

    /**
     * Get Http port.
     * 
     * @return
     */
    public int getHttpPort() {
        int httpPort = 0;
        String httpPortString = this.bootstrapOperations.getParam(Constants.HttpServer.HTTP_PORT,
                this.getJbiComponentConfiguration());
        if (httpPortString != null) {
            httpPort = Integer.parseInt(httpPortString);
        }

        return httpPort;
    }

    /**
     * Get the HTTP host name
     * 
     * @return
     */
    public String getHttpHostName() {
        return this.bootstrapOperations.getParam(Constants.HttpServer.HTTP_HOSTNAME, this
                .getJbiComponentConfiguration());
    }

    /**
     * Set HTTP services list.
     * 
     * @param httpServicesList
     */
    public void setHttpServicesList(final boolean httpServicesList) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_SERVICES_LIST, Boolean
                .toString(httpServicesList), this.getJbiComponentConfiguration());
    }

    /**
     * Get HTTP services list.
     * 
     * @return
     */
    public boolean getHttpServicesList() {
        boolean httpServicesList = true;

        String httpServicesListString = this.bootstrapOperations.getParam(
                Constants.HttpServer.HTTP_SERVICES_LIST, this.getJbiComponentConfiguration());
        if (httpServicesListString != null) {
            httpServicesList = Boolean.parseBoolean(httpServicesListString);
        }

        return httpServicesList;
    }

    /**
     * Set HTTP services context.
     * 
     * @param httpServicesContext
     */
    public void setHttpServicesContext(final String httpServicesContext) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_SERVICES_CONTEXT,
                httpServicesContext, this.getJbiComponentConfiguration());
    }

    /**
     * Get HTTP services context.
     * 
     * @return
     */
    public String getHttpServicesContext() {
        return this.bootstrapOperations.getParam(Constants.HttpServer.HTTP_SERVICES_CONTEXT, this
                .getJbiComponentConfiguration());
    }

    /**
     * Set HTTP services mapping.
     * 
     * @param httpServicesMapping
     */
    public void setHttpServicesMapping(final String httpServicesMapping) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_SERVICES_MAPPING,
                httpServicesMapping, this.getJbiComponentConfiguration());
    }

    /**
     * Get HTTP services Mapping.
     * 
     * @return
     */
    public String getHttpServicesMapping() {
        return this.bootstrapOperations.getParam(Constants.HttpServer.HTTP_SERVICES_MAPPING, this
                .getJbiComponentConfiguration());
    }

    /**
     * Set HTTP thread pool minimum size.
     * 
     * @param httpThreadPoolSizeMin
     */
    public void setHttpThreadPoolSizeMin(final int httpThreadPoolSizeMin) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN, Integer
                .toString(httpThreadPoolSizeMin), this.getJbiComponentConfiguration());
    }

    /**
     * Get HTTP thread pool minimum size
     * 
     * @return
     */
    public int getHttpThreadPoolSizeMin() {
        int httpThreadPoolSizeMin = 0;

        String httpThreadPoolSizeMinString = this.bootstrapOperations
                .getParam(Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN, this
                        .getJbiComponentConfiguration());
        if (httpThreadPoolSizeMinString != null) {
            httpThreadPoolSizeMin = Integer.parseInt(httpThreadPoolSizeMinString);
        }

        return httpThreadPoolSizeMin;
    }

    /**
     * Set HTTP thread pool maximum size.
     * 
     * @param httpThreadPoolSizeMax
     */
    public void setHttpThreadPoolSizeMax(final int httpThreadPoolSizeMax) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX, Integer
                .toString(httpThreadPoolSizeMax), this.getJbiComponentConfiguration());
    }

    /**
     * Get HTTP thread pool maximum size
     * 
     * @return
     */
    public int getHttpThreadPoolSizeMax() {
        int httpThreadPoolSizeMax = 0;

        String httpThreadPoolSizeMaxString = this.bootstrapOperations
                .getParam(Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX, this
                        .getJbiComponentConfiguration());
        if (httpThreadPoolSizeMaxString != null) {
            httpThreadPoolSizeMax = Integer.parseInt(httpThreadPoolSizeMaxString);
        }

        return httpThreadPoolSizeMax;
    }

    /**
     * Set HTTP acceptors.
     * 
     * @param httpAcceptors
     */
    public void setHttpAcceptors(final int httpAcceptors) {
        this.bootstrapOperations.setParam(Constants.HttpServer.HTTP_ACCEPTORS, Integer
                .toString(httpAcceptors), this.getJbiComponentConfiguration());
    }

    /**
     * Get HttpAcceptors.
     * 
     * @return
     */
    public int getHttpAcceptors() {
        int httpAcceptors = 0;

        String httpAcceptorsString = this.bootstrapOperations.getParam(
                Constants.HttpServer.HTTP_ACCEPTORS, this.getJbiComponentConfiguration());
        if (httpAcceptorsString != null) {
            httpAcceptors = Integer.parseInt(httpAcceptorsString);
        }

        return httpAcceptors;
    }

}
