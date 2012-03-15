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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.component.framework.DefaultBootstrap;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.AXIS2_XML;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.MODULES_PATH;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.SERVICES_PATH;

/**
 * The bootstrap class. Copy all the configuration files in the good
 * directories.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class SoapBootstrap extends DefaultBootstrap {

    protected SoapBootstrapOperations bootstrapOperations;

    // A logger must be static
    // (otherwise the garbage collector remove it from the memory)
    public static final Logger logger = Logger.getLogger("org.apache.axis2");

    /**
     * Add a module to the Axis2 engine. This method is used by the
     * configuration MBean.
     * 
     * @param url
     * @return
     * @throws JBIException
     */
    public String addModule(final String url) throws Exception {
        return bootstrapOperations.addModule(url);
    }

    /**
     * Copy the needed configuration files to the Axis2 directories. Do not copy
     * them if the files are already present (the install phase has already been
     * done and we are now in the uninstall phase).
     * 
     */
    protected void copyConfigurationFiles() throws JBIException {

        final File installRootFile = new File(getInstallContext().getInstallRoot());

        /* copy required files from META-INF directory */
        final File axis2File = new File(installRootFile, "META-INF" + File.separator + AXIS2_XML);

        // copy axis2.xml file
        if (axis2File.exists()) {
            final String workspaceRootDir = installContext.getContext().getWorkspaceRoot();
            final File destFile = new File(workspaceRootDir, axis2File.getName());
            if (!destFile.exists()) {
                try {
                    FileUtils.copyFile(axis2File, destFile);
                } catch (final IOException e) {
                    throw new JBIException(
                            "Can not copy the axis2 configuration file, axis2 can not be used", e);
                }
            }
        } else {
            throw new JBIException("the axis2 configuration file do not exist at '+ "
                    + axis2File.getAbsolutePath() + "', axis2 can not be used");
        }

        // copy modules from component root path
        final String workspaceRootDir = getInstallContext().getContext().getWorkspaceRoot();
        final File modules = new File(workspaceRootDir, MODULES_PATH);
        final File[] moduleFiles = installRootFile.listFiles(new BootStrapFileFilter());
        for (final File file : moduleFiles) {
            final File destFile = new File(modules, file.getName());
            if (!destFile.exists()) {
                try {
                    FileUtils.copyFile(file, destFile);
                } catch (final IOException e) {
                    getLogger().warning(
                            "The module '" + file.getName()
                                    + "' can not been copied to modules directory");
                }
            }
        }
    }

    /**
     * Create the directories that will be used by axis
     * 
     * @param installPath
     */
    protected void createWorkDirectories() {
        final String workspaceRootDir = installContext.getContext().getWorkspaceRoot();
        final File modules = new File(workspaceRootDir, MODULES_PATH);
        final File services = new File(workspaceRootDir, SERVICES_PATH);
        if (!modules.exists()) {
            if (!modules.mkdirs()) {
                getLogger().warning(
                        "Cannot create modules directory at '" + modules.getAbsolutePath() + "'");
            }
        }
        if (!services.exists()) {
            if (!services.mkdirs()) {
                getLogger().warning(
                        "Cannot create services directory at '" + services.getAbsolutePath() + "'");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.DefaultBootstrap#doInit()
     */
    @Override
    protected void doInit() throws JBIException {
        super.doInit();
        createWorkDirectories();
        copyConfigurationFiles();

        // Disable Axis 2 logging
        logger.setLevel(Level.OFF);

        if (bootstrapOperations == null) {
            bootstrapOperations = new SoapBootstrapOperations(installContext);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.DefaultBootstrap#getAttributeList()
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
        attributes.add("httpsEnabled");
        attributes.add("httpsPort");
        attributes.add("httpsKeystoreType");
        attributes.add("httpsKeystoreFile");
        attributes.add("httpsKeystorePassword");
        attributes.add("httpsKeyPassword");
        attributes.add("httpsTruststoreType");
        attributes.add("httpsTruststoreFile");
        attributes.add("httpsTruststorePassword");        
        attributes.add("javaNamingFactoryInitial");
        attributes.add("javaNamingProviderURL");
        attributes.add("jmsConnectionFactoryJNDIName");

        return attributes;
    }

    /**
     * Get the number of acceptors
     * 
     * @return the number of acceptors
     */
    public int getHttpAcceptors() {
        int httpAcceptors = 0;

        final String httpAcceptorsString = bootstrapOperations.getParam(
                SoapConstants.HttpServer.HTTP_ACCEPTORS, getJbiComponentConfiguration());
        if (httpAcceptorsString != null) {
            httpAcceptors = Integer.parseInt(httpAcceptorsString);
        }

        return httpAcceptors;
    }

    /**
     * Get the HTTP host name
     * 
     * @return the HTTP host name
     */
    public String getHttpHostName() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTP_HOSTNAME,
                getJbiComponentConfiguration());
    }

    /**
     * Get the HTTP port
     * 
     * @return the HTTP port
     */
    public int getHttpPort() {
        int httpPort = 0;
        final String httpPortString = bootstrapOperations.getParam(SoapConstants.HttpServer.HTTP_PORT,
                getJbiComponentConfiguration());
        if (httpPortString != null) {
            httpPort = Integer.parseInt(httpPortString);
        }

        return httpPort;
    }

    /**
     * Get the HTTP service context
     * 
     * @return the HTTP service context
     */
    public String getHttpServicesContext() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTP_SERVICES_CONTEXT,
                getJbiComponentConfiguration());
    }

    /**
     * Return if the service list is available
     * 
     * @return true if the service list is available, otherwise false
     */
    public boolean getHttpServicesList() {
        boolean httpServicesList = true;

        final String httpServicesListString = bootstrapOperations.getParam(
                SoapConstants.HttpServer.HTTP_SERVICES_LIST, getJbiComponentConfiguration());
        if (httpServicesListString != null) {
            httpServicesList = Boolean.parseBoolean(httpServicesListString);
        }

        return httpServicesList;
    }

    /**
     * Get the HTTP service mapping
     * 
     * @return the HTTP service mapping
     */
    public String getHttpServicesMapping() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTP_SERVICES_MAPPING,
                getJbiComponentConfiguration());
    }

    /**
     * Get the HTTP thread pool maximum size
     * 
     * @return the HTTP thread pool maximum size
     */
    public int getHttpThreadPoolSizeMax() {
        int httpThreadPoolSizeMax = 0;

        final String httpThreadPoolSizeMaxString = bootstrapOperations.getParam(
                SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX, getJbiComponentConfiguration());
        if (httpThreadPoolSizeMaxString != null) {
            httpThreadPoolSizeMax = Integer.parseInt(httpThreadPoolSizeMaxString);
        }

        return httpThreadPoolSizeMax;
    }

    /**
     * Get the HTTP thread pool minimum size
     * 
     * @return the HTTP thread pool minimum size
     */
    public int getHttpThreadPoolSizeMin() {
        int httpThreadPoolSizeMin = 0;

        final String httpThreadPoolSizeMinString = bootstrapOperations.getParam(
                SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN, getJbiComponentConfiguration());
        if (httpThreadPoolSizeMinString != null) {
            httpThreadPoolSizeMin = Integer.parseInt(httpThreadPoolSizeMinString);
        }

        return httpThreadPoolSizeMin;
    }

    /**
     * Get the Java initial factory naming
     * 
     * @return the Java initial factory naming
     */
    public String getJavaNamingFactoryInitial() {
        return bootstrapOperations.getParam(SoapConstants.JmsTransportLayer.JNDI_INITIAL_FACTORY,
                getJbiComponentConfiguration());
    }

    /**
     * Get the Java URL provider naming
     * 
     * @return the Java URL provider naming
     */
    public String getJavaNamingProviderURL() {
        return bootstrapOperations.getParam(SoapConstants.JmsTransportLayer.JNDI_PROVIDER_URL,
                getJbiComponentConfiguration());
    }

    /**
     * Get the JMS connection factory JNDI name
     * 
     * @return the JMS connection factory JNDI name
     */
    public String getJMSConnectionFactoryJNDIName() {
        return bootstrapOperations.getParam(SoapConstants.JmsTransportLayer.CONFAC_JNDINAME,
                getJbiComponentConfiguration());
    }

    /**
     * Return if HTTPS is enabled
     * 
     * @return true if HTTPS is enabled, otherwise false
     */
    public boolean isHttpsEnabled() {
        boolean isHttpsEnabled = false;

        final String isHttpsEnabledString = bootstrapOperations.getParam(
                SoapConstants.HttpServer.HTTPS_ENABLED, getJbiComponentConfiguration());
        if (isHttpsEnabledString != null) {
            isHttpsEnabled = Boolean.parseBoolean(isHttpsEnabledString);
        }

        return isHttpsEnabled;
    }

    /**
     * Get the keystore type (JKS / PKCS12)
     * 
     * @return the keystore type (JKS / PKCS12)
     */
    public String getHttpsKeystoreType() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_TYPE,
                getJbiComponentConfiguration());
    }
    
    /**
     * Get HTTPS port.
     * 
     * @return the HTTPS port
     */
    public int getHttpsPort() {
        int httpsPort = 0;
        final String httpsPortString = bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_PORT,
                getJbiComponentConfiguration());
        if (httpsPortString != null) {
            httpsPort = Integer.parseInt(httpsPortString);
        }

        return httpsPort;
    }
    
    /**
     * Get the keystore file path
     * 
     * @return the keystore file path
     */
    public String getHttpsKeystoreFile() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_FILE,
                getJbiComponentConfiguration());
    }

    /**
     * Get the keystore password
     * 
     * @return the keystore password
     */
    public String getHttpsKeystorePassword() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_PASSWORD,
                getJbiComponentConfiguration());
    }

    /**
     * Get the key password
     * 
     * @return the key password
     */
    public String getHttpsKeyPassword() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_KEY_PASSWORD,
                getJbiComponentConfiguration());
    }
    
    /**
     * Get the truststore type (JKS / PKCS12)
     * 
     * @return the truststore type (JKS / PKCS12)
     */
    public String getHttpsTruststoreType() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_TYPE,
                getJbiComponentConfiguration());
    }

    /**
     * Get the truststore file path
     * 
     * @return the truststore file path
     */
    public String getHttpsTruststoreFile() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_FILE,
                getJbiComponentConfiguration());
    }

    /**
     * Get the truststore password
     * 
     * @return the truststore password
     */
    public String getHttpsTruststorePassword() {
        return bootstrapOperations.getParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_PASSWORD,
                getJbiComponentConfiguration());
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

    /**
     * Set HTTP acceptor number
     * 
     * @param httpAcceptors the number of HTTP acceptor
     */
    public void setHttpAcceptors(final int httpAcceptors) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_ACCEPTORS, Integer
                .toString(httpAcceptors), getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP host name
     * 
     * @param httpHostName the HTTP host name
     */
    public void setHttpHostName(final String httpHostName) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_HOSTNAME, httpHostName,
                getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP port
     * 
     * @param httpPort the HTTP port
     */
    public void setHttpPort(final int httpPort) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_PORT, Integer.toString(httpPort),
                getJbiComponentConfiguration());
    }

    /**
     * Set HTTP service context
     * 
     * @param httpServicesContext the HTTP service context
     */
    public void setHttpServicesContext(final String httpServicesContext) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_SERVICES_CONTEXT,
                httpServicesContext, getJbiComponentConfiguration());
    }

    /**
     * Define if the service list is available
     * 
     * @param httpServicesList true if the service list is available, otherwise false
     */
    public void setHttpServicesList(final boolean httpServicesList) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_SERVICES_LIST, Boolean
                .toString(httpServicesList), getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP service mapping
     * 
     * @param httpServicesMapping the HTTP service mapping
     */
    public void setHttpServicesMapping(final String httpServicesMapping) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_SERVICES_MAPPING,
                httpServicesMapping, getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP thread pool maximum size
     * 
     * @param httpThreadPoolSizeMax the HTTP thread pool maximum size
     */
    public void setHttpThreadPoolSizeMax(final int httpThreadPoolSizeMax) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX, Integer
                .toString(httpThreadPoolSizeMax), getJbiComponentConfiguration());
    }

    /**
     * Set the HTTP thread pool minimum size
     * 
     * @param httpThreadPoolSizeMin the HTTP thread pool minimum size
     */
    public void setHttpThreadPoolSizeMin(final int httpThreadPoolSizeMin) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN, Integer
                .toString(httpThreadPoolSizeMin), getJbiComponentConfiguration());
    }

    /**
     * Set the Java initial factory naming
     * 
     * @param javaNamingFactoryInitial the Java initial factory naming
     */
    public void setJavaNamingFactoryInitial(final String javaNamingFactoryInitial) {
        bootstrapOperations.setParam(SoapConstants.JmsTransportLayer.JNDI_INITIAL_FACTORY,
                javaNamingFactoryInitial, getJbiComponentConfiguration());
    }

    /**
     * Set the Java URL provider naming
     * 
     * @param javaNamingProviderURL the Java URL provider naming
     */
    public void setJavaNamingProviderURL(final String javaNamingProviderURL) {
        bootstrapOperations.setParam(SoapConstants.JmsTransportLayer.JNDI_PROVIDER_URL,
                javaNamingProviderURL, getJbiComponentConfiguration());
    }

    /**
     * Set the JMS connection factory JNDI name
     * 
     * @param jmsConnectionFactoryJNDIName the JMS connection factory JNDI name
     */
    public void setJMSConnectionFactoryJNDIName(final String jmsConnectionFactoryJNDIName) {
        bootstrapOperations.setParam(SoapConstants.JmsTransportLayer.CONFAC_JNDINAME,
                jmsConnectionFactoryJNDIName, getJbiComponentConfiguration());
    }
    
    /**
     * Define if HTTPS is enabled
     * 
     * @param isHttpsEnabled a flag set to true if HTTPS is enabled, otherwise false
     */
    public void setHttpsEnabled(boolean isHttpsEnabled) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_ENABLED,
                Boolean.toString(isHttpsEnabled), getJbiComponentConfiguration());
    }

    /**
     * Set the HTTPS port
     * 
     * @param httpPort the HTTPS port
     */
    public void setHttpsPort(final int httpsPort) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_PORT, Integer.toString(httpsPort),
                getJbiComponentConfiguration());
    }
    
    /**
     * Set the type of the keystore (JKS / PKCS12)
     * 
     * @param httpsKeystoreType the type of the keystore (JKS / PKCS12)
     */
    public void setHttpsKeystoreType(String httpsKeystoreType) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_TYPE,
                httpsKeystoreType, getJbiComponentConfiguration());
    }
    
    /**
     * Set the keystore absolute file path
     * 
     * @param httpsKeytoreFile the keystore absolute file path
     */
    public void setHttpsKeystoreFile(String httpsKeytoreFile) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_FILE,
                httpsKeytoreFile, getJbiComponentConfiguration());
    }

    /**
     * Set the keystore password
     * 
     * @param httpsKeytorePassword the keystore password
     */
    public void setHttpsKeystorePassword(String httpsKeytorePassword) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_PASSWORD,
                httpsKeytorePassword, getJbiComponentConfiguration());
    }

    /**
     * Set the key password
     * 
     * @param httpsKeytoreKeyPassword the key password
     */
    public void setHttpsKeyPassword(String httpsKeytoreKeyPassword) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_KEYSTORE_KEY_PASSWORD,
                httpsKeytoreKeyPassword, getJbiComponentConfiguration());
    }
    
    /**
     * Set the type of the truststore (JKS / PKCS12)
     * 
     * @param httpsTruststoreType the type of the truststore (JKS / PKCS12)
     */
    public void setHttpsTruststoreType(String httpsTruststoreType) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_TYPE,
                httpsTruststoreType, getJbiComponentConfiguration());
    }    
    
    /**
     * Set the truststore absolute file path
     * 
     * @param httpsTruststoreFile the truststore absolute file path
     */
    public void setHttpsTruststoreFile(String httpsTruststoreFile) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_FILE,
                httpsTruststoreFile, getJbiComponentConfiguration());
    }

    /**
     * Set the truststore password
     * 
     * @param httpsTruststorePassword the truststore password
     */
    public void setHttpsTruststorePassword(String httpsTruststorePassword) {
        bootstrapOperations.setParam(SoapConstants.HttpServer.HTTPS_TRUSTSTORE_PASSWORD,
                httpsTruststorePassword, getJbiComponentConfiguration());
    }   
}
