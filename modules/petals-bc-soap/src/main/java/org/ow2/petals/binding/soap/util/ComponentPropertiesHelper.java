/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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
package org.ow2.petals.binding.soap.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTPS_PORT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_ACCEPTORS;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_PORT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MAX;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MIN;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_ENABLED;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_KEYSTORE_FILE;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_KEYSTORE_KEY_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_KEYSTORE_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_KEYSTORE_TYPE;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_PORT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_TRUSTSTORE_FILE;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_TRUSTSTORE_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTPS_TRUSTSTORE_TYPE;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_ACCEPTORS;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_HOSTNAME;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_PORT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_SERVICES_CONTEXT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_SERVICES_LIST;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_SERVICES_MAPPING;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN;


/**
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class ComponentPropertiesHelper {

    private ComponentPropertiesHelper() {
    }

    /**
     * Get the host name
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the host name
     */
    public static String getHttpHostName(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_HOSTNAME);
    }

    /**
     * Get the HTTP port
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the HTTP port
     */
    public static int getHttpPort(Logger logger, ConfigurationExtensions configurationExtensions) {
        return getInt(logger, configurationExtensions, HTTP_PORT, DEFAULT_HTTP_PORT);
    }

    /**
     * Return if the component have to provide the services list
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return true if the component have to provide the services list,
     *         otherwise false
     */
    public static boolean isProvidingServicesList(ConfigurationExtensions configurationExtensions) {
        return Boolean.parseBoolean(configurationExtensions.get(HTTP_SERVICES_LIST));
    }

    /**
     * Get the service context path segment
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the service context path segment
     */
    public static String getServicesContext(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_SERVICES_CONTEXT);
    }

    /**
     * Get the service mapping path segment
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the service mapping path segment
     */
    public static String getServicesMapping(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_SERVICES_MAPPING);
    }

    /**
     * Get the thread max pool size of the Jetty server
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the thread max pool size of the Jetty server
     */
    public static int getHttpThreadMaxPoolSize(Logger logger, ConfigurationExtensions configurationExtensions) {
        return getInt(logger, configurationExtensions, HTTP_THREAD_POOL_SIZE_MAX, DEFAULT_HTTP_THREAD_POOL_SIZE_MAX); 
    }

    /**
     * Get the thread min pool size of the Jetty server
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the thread min pool size of the Jetty server
     */
    public static int getHttpThreadMinPoolSize(Logger logger, ConfigurationExtensions configurationExtensions) {
        return getInt(logger, configurationExtensions, HTTP_THREAD_POOL_SIZE_MIN, DEFAULT_HTTP_THREAD_POOL_SIZE_MIN);
    }

    /**
     * Get the number of acceptors of the Jetty server
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the number of acceptors of the Jetty server
     */
    public static int getHttpAcceptors(Logger logger, ConfigurationExtensions configurationExtensions) {
        return getInt(logger, configurationExtensions, HTTP_ACCEPTORS, DEFAULT_HTTP_ACCEPTORS);
    }

    /**
     * Define if HTTPS is enabled
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return true if HTTPS is enabled, otherwise false
     */
    public static boolean isHttpsEnabled(ConfigurationExtensions configurationExtensions) {
        return Boolean.parseBoolean(configurationExtensions.get(HTTPS_ENABLED));
    }

    /**
     * Get the HTTPS port
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the HTTPS port
     */
    public static int getHttpsPort(Logger logger, ConfigurationExtensions configurationExtensions) {
        return getInt(logger, configurationExtensions, HTTPS_PORT, DEFAULT_HTTPS_PORT);
    }

    /**
     * Get the keystore type (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the keystore type (HTTPS)
     */
    public static String getHttpsKeystoreType(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_KEYSTORE_TYPE);
    }

    /**
     * Get the keystore file (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the keystore file (HTTPS)
     */
    public static String getHttpsKeystoreFile(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_KEYSTORE_FILE);
    }

    /**
     * Get the keystore password (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the keystore password (HTTPS)
     */
    public static String getHttpsKeystorePassword(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_KEYSTORE_PASSWORD);
    }

    /**
     * Get the key password (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the key password (HTTPS)
     */
    public static String getHttpsKeyPassword(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_KEYSTORE_KEY_PASSWORD);
    }

    /**
     * Get the truststore type (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the truststore type (HTTPS)
     */
    public static String getHttpsTruststoreType(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_TRUSTSTORE_TYPE);
    }

    /**
     * Get the truststore file (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the truststore file (HTTPS)
     */
    public static String getHttpsTruststoreFile(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_TRUSTSTORE_FILE);
    }

    /**
     * Get the truststore password (HTTPS)
     * 
     * @param configurationExtensions
     *            the component extensions
     * @return the truststore password (HTTPS)
     */
    public static String getHttpsTruststorePassword(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTPS_TRUSTSTORE_PASSWORD);
    }

    /**
     * Get the integer value for a parameter, use the default if it is not
     * possible to parse the parameter value
     * 
     * @param logger
     *            the logger
     * @param parameter
     *            a parameter name
     * @param defaultValue
     *            the default value if the string does not represent a integer
     * 
     * @return the integer value
     */
    private static final int getInt(Logger logger, ConfigurationExtensions configurationExtensions,
            String parameterName, int defaultValue) {
        int intValue;

        String intStr = configurationExtensions.get(parameterName);
        try {
            intValue = Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            intValue = defaultValue;
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "The value of the parameter " + parameterName
                        + " is not correct, use the default value");
            }
        }

        return intValue;
    }
}
