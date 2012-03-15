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

import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

import static org.ow2.petals.binding.soap.Constants.HttpServer.DEFAULT_PROTOCOL;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_ACCEPTORS;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_HOSTNAME;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_PORT;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_SERVICES_CONTEXT;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_SERVICES_LIST;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_SERVICES_MAPPING;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MAX;
import static org.ow2.petals.binding.soap.Constants.HttpServer.HTTP_THREAD_POOL_SIZE_MIN;

/**
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class ComponentPropertiesHelper {
    
    private ComponentPropertiesHelper(){
    }
    
    /**
     * Get the HTTP port
     * 
     * @return
     */
    public static int getHttpPort(ConfigurationExtensions configurationExtensions) {
        return Integer.parseInt(configurationExtensions.get(HTTP_PORT));
    }

    /**
     * Get the Host name
     * 
     * @return
     */
    public static String getHttpHostName(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_HOSTNAME);
    }

    /**
     * Does the component have to provide the services list
     * 
     * @return
     */
    public static boolean isProvidingServicesList(ConfigurationExtensions configurationExtensions) {
        return Boolean.parseBoolean(configurationExtensions.get(HTTP_SERVICES_LIST));
    }

    public static String getServicesContext(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_SERVICES_CONTEXT);
    }

    public static String getServicesMapping(ConfigurationExtensions configurationExtensions) {
        return configurationExtensions.get(HTTP_SERVICES_MAPPING);
    }

    /**
     * Currently support only http protocol
     * 
     * @return
     */
    public static String getProtocol() {
        return DEFAULT_PROTOCOL;
    }

    /**
     * Get the thread max pool size of the Jetty server
     * 
     * @return
     */
    public static int getHttpThreadMaxPoolSize(ConfigurationExtensions configurationExtensions) {
        return Integer.parseInt(configurationExtensions.get(HTTP_THREAD_POOL_SIZE_MAX));
    }

    /**
     * Get the thread min pool size of the Jetty server
     * 
     * @return
     */
    public static int getHttpThreadMinPoolSize(ConfigurationExtensions configurationExtensions) {
        return Integer.parseInt(configurationExtensions.get(HTTP_THREAD_POOL_SIZE_MIN));
    }

    /**
     * Get the number of acceptors of the Jetty server
     * 
     * @return
     */
    public static int getHttpAcceptors(ConfigurationExtensions configurationExtensions) {
        return Integer.parseInt(configurationExtensions.get(HTTP_ACCEPTORS));
    }
}
