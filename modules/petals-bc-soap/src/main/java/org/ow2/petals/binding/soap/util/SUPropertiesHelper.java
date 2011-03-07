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

package org.ow2.petals.binding.soap.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

import static org.ow2.petals.binding.soap.Constants.Component.NS_PREFIX;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.ADDRESS;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.DEFAULT_MODE;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.ENABLE_HTTP_TRANSPORT;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.ENABLE_JMS_TRANSPORT;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.HEADERS_FILTER;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.INJECT_HEADERS;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.POLICY_PATH;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.REMOVE_ROOT;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.REST_ADD_NS_PREFIX;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.REST_ADD_NS_URI;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.REST_REMOVE_NS_PREFIX_RESP;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.SERVICE_NAME;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.TIMEOUT;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.TOPIC_NAME;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.WSA_USE;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.JSON;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.REST;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.SOAP;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.TOPIC;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.WSA.FAULT_TO;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.WSA.FROM;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.WSA.REPLY_TO;
import static org.ow2.petals.binding.soap.Constants.ServiceUnit.WSA.TO;

/**
 * Helper for Service Unit properties values
 * 
 * @author chamerling - eBM Websourcing
 * 
 */
public class SUPropertiesHelper {

    /**
     * Creates a new instance if {@link SUPropertiesHelper}
     */
    protected SUPropertiesHelper() {
        super();
    }

    /**
     * Retrieve the timeout extensions value.
     * 
     * @param extensions
     * @return
     */
    public final static long retrieveTimeout(final ConfigurationExtensions extensions) {
        return getLong(TIMEOUT, extensions);
    }

    /**
     * Retrieve the remove root element extension value
     * 
     * @param extensions
     * @return
     */
    public final static boolean retrieveRemoveRootElement(final ConfigurationExtensions extensions) {
        // do not remove root element if element is not set!!!
        return getBoolean(REMOVE_ROOT, extensions, false);
    }

    /**
     * Get the URI which has to be added to the message payload
     * 
     * @param props
     * @return
     */
    public static String retrieveRESTAddNSURIOnRequest(final ConfigurationExtensions extensions) {
        return extensions.get(REST_ADD_NS_URI);
    }

    /**
     * Get the prefix to be used
     * 
     * @param props
     * @return
     */
    public static String retrieveRESTAddNSPrefixOnRequest(final ConfigurationExtensions extensions) {
        return getString(REST_ADD_NS_PREFIX, NS_PREFIX, extensions);
    }

    /**
     * Get all the prefix which has to be removed on REST response.
     * 
     * @param props
     * @return a list of prefixes to remove or null if the option has not been
     *         set
     */
    public static List<String> retrieveRESTRemovePrefixOnResponse(
            final ConfigurationExtensions extensions) {
        List<String> result = null;
        final String tmp = extensions.get(REST_REMOVE_NS_PREFIX_RESP);
        if (tmp != null) {
            result = new ArrayList<String>();
            final StringTokenizer st = new StringTokenizer(tmp, ",");
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
        }
        return result;
    }

    /**
     * 
     * @param extensions
     * @return
     */
    public static List<String> retrieveHeaderList(final ConfigurationExtensions extensions) {
        List<String> result = null;
        final String tmp = extensions.get(HEADERS_FILTER);
        if (tmp != null) {
            result = new ArrayList<String>();
            final StringTokenizer st = new StringTokenizer(tmp, ",");
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
        }
        return result;
    }

    /**
     * We inject the headers by default
     * 
     * @param extensions
     * @return
     */
    public static boolean retrieveInjectHeader(final ConfigurationExtensions extensions) {
        return getBoolean(INJECT_HEADERS, extensions);
    }

    /**
     * Get the mode. SOAP is default when not found.
     * 
     * @param extensions
     * @return
     */
    public static String getMode(final ConfigurationExtensions extensions) {
        if (extensions == null) {
            return DEFAULT_MODE;
        }
        return getString(MODE, DEFAULT_MODE, extensions);
    }

    /**
     * Get the service name. This name will be used to create the web service
     * 
     * @param extensions
     * @return
     */
    public static String getServiceName(final ConfigurationExtensions extensions) {
        String result = null;
        String address = getAddress(extensions);
        String serviceName = getString(SERVICE_NAME, extensions);
        // service-name parameter is the high level parameter, address is used
        // of service-name has not been found...
        if (serviceName != null) {
            result = serviceName;
        } else if (address != null) {
            result = address;
        }

        return result;
    }

    /**
     * Retrieve the topic name
     * 
     * @param extensions
     * @return
     */
    public static String getTopicName(final ConfigurationExtensions extensions) {
        String result = null;
        String address = getAddress(extensions);
        String topicName = getString(TOPIC_NAME, extensions);
        // topic-name parameter is the high level parameter, address is used
        // of service-name has not been found...
        if (topicName != null) {
            result = topicName;
        } else if (address != null) {
            result = address;
        }
        return result;
    }

    /**
     * The policy is enabled if the policy path has been defined
     * 
     * @param extensions
     * @return
     */
    public static boolean isPolicyEnabled(final ConfigurationExtensions extensions) {
        return checkPresent(POLICY_PATH, extensions);
    }

    /**
     * 
     * @return
     */
    public static String getWSATo(final ConfigurationExtensions extensions) {
        String result = null;
        String address = getAddress(extensions);
        String to = getString(TO, extensions);
        // wsa-to parameter is the high level parameter, address is used
        // if wsa-to has not been found...
        if (to != null) {
            result = to;
        } else if (address != null) {
            result = address;
        }

        return result;
    }

    /**
     * Get the address parameter value
     * 
     * @param extensions
     * @return
     */
    public static String getAddress(final ConfigurationExtensions extensions) {
        return getString(ADDRESS, extensions);
    }

    /**
     * 
     * @param extensions
     * @return
     */
    public static String getWSAFrom(final ConfigurationExtensions extensions) {
        return getString(FROM, extensions);
    }

    /**
     * 
     * @param extensions
     * @return
     */
    public static String getWSAReplyTo(final ConfigurationExtensions extensions) {
        return getString(REPLY_TO, extensions);
    }

    /**
     * 
     * @param extensions
     * @return
     */
    public static String getWSAFaultTo(final ConfigurationExtensions extensions) {
        return getString(FAULT_TO, extensions);
    }

    /**
     * Use the WSA fields from the exchange
     * 
     * @param extensions
     * @return
     */
    public static boolean isWSAEnabled(final ConfigurationExtensions extensions) {
        return checkPresent(WSA_USE, extensions);
    }

    /**
     * Check if the given parameter is present
     * 
     * @param parameterName
     * @param extensions
     * @return
     */
    protected static final boolean checkPresent(final String parameterName,
            final ConfigurationExtensions extensions) {
        String tmp = extensions.get(parameterName);
        return ((tmp != null) && (tmp.trim().length() > 0));
    }

    /**
     * The mode is SOAP ?
     * 
     * @param extensions
     * @return
     */
    public static final boolean isSOAPMode(ConfigurationExtensions extensions) {
        return ((SUPropertiesHelper.getMode(extensions) != null) && SUPropertiesHelper.getMode(
                extensions).equalsIgnoreCase(SOAP));
    }

    /**
     * The mode is REST ?
     * 
     * @param extensions
     * @return
     */
    public static final boolean isRESTMode(ConfigurationExtensions extensions) {
        return ((SUPropertiesHelper.getMode(extensions) != null) && SUPropertiesHelper.getMode(
                extensions).equalsIgnoreCase(REST));
    }

    /**
     * Returns true if the mode is service compatible : SOAP, REST or JSON
     * 
     * @param extensions
     * @return
     */
    public static final boolean isServiceMode(ConfigurationExtensions extensions) {
        return ((SUPropertiesHelper.getMode(extensions) != null) && (SUPropertiesHelper.getMode(
                extensions).equalsIgnoreCase(SOAP)
                || SUPropertiesHelper.getMode(extensions).equalsIgnoreCase(REST) || SUPropertiesHelper
                .getMode(extensions).equalsIgnoreCase(JSON)));
    }

    /**
     * The mode is TOPIC ?
     * 
     * @param extensions
     * @return
     */
    public static final boolean isTopicMode(ConfigurationExtensions extensions) {
        return ((SUPropertiesHelper.getMode(extensions) != null) && SUPropertiesHelper.getMode(
                extensions).equalsIgnoreCase(TOPIC));
    }

    /**
     * Get the flag enabling the HTTP transport layer.
     * 
     * @param extensions
     * @return
     */
    public static boolean isHttpTransportEnable(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_HTTP_TRANSPORT, extensions);
    }

    /**
     * Get the flag enabling the JMS transport layer.
     * 
     * @param extensions
     * @return
     */
    public static boolean isJmsTransportEnable(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_JMS_TRANSPORT, extensions);
    }

    /**
     * Get the boolean value of the parameter
     * 
     * @param parameterName
     * @param extensions
     * @return the boolean value of the parameter according to the
     *         Boolean.parseBoolean method, true if the parameter has not been
     *         found.
     */
    protected static final boolean getBoolean(final String parameterName,
            final ConfigurationExtensions extensions) {
        boolean result = true;
        String tmp = extensions.get(parameterName);
        if (tmp != null) {
            result = Boolean.parseBoolean(tmp.trim());
        }
        return result;
    }

    protected static final boolean getBoolean(final String parameterName,
            final ConfigurationExtensions extensions, boolean defaultValue) {
        boolean result = true;
        String tmp = extensions.get(parameterName);
        if (tmp == null) {
            result = defaultValue;
        } else {
            result = Boolean.parseBoolean(tmp.trim());
        }
        return result;
    }

    /**
     * Get the parameter value or return null if not found
     * 
     * @param parameterName
     * @param extensions
     * @return
     */
    protected static final String getString(final String parameterName,
            final ConfigurationExtensions extensions) {
        String result = extensions.get(parameterName);
        if (result != null) {
            result = result.trim();
        }
        return result;
    }

    /**
     * 
     * @param parameterName
     * @param defaultValue
     * @param extensions
     * @return
     */
    protected static final String getString(final String parameterName, final String defaultValue,
            final ConfigurationExtensions extensions) {
        String result = getString(parameterName, extensions);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Get the parameter value as long or return -1L if not found
     * 
     * @param parameterName
     * @param extensions
     * @return
     */
    protected static final long getLong(final String parameterName,
            final ConfigurationExtensions extensions) {
        long timeout = -1L;
        if (extensions.get(parameterName) != null) {
            timeout = Long.parseLong(extensions.get(parameterName));
        }
        return timeout;
    }
}
