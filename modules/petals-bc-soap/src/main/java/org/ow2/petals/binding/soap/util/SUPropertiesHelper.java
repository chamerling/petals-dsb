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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import static org.ow2.petals.binding.soap.SoapConstants.SOAP.SOAP_VERSION_12;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ADDRESS;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.BASIC_AUTH_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.BASIC_AUTH_USERNAME;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.CHUNKED_MODE;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.CLEANUP_TRANSPORT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.COMPATIBILITY;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ENABLE_HTTPS_TRANSPORT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ENABLE_HTTP_TRANSPORT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ENABLE_JMS_TRANSPORT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.ENABLE_WSA;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HEADERS_FILTER;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HEADERS_TO_INJECT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTP_SERVICES_REDIRECTION;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.INJECT_HEADERS;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.MODULES;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.PROXY_DOMAIN;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.PROXY_HOST;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.PROXY_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.PROXY_PORT;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.PROXY_USER;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.SERVICE_NAME;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.SERVICE_PARAMETERS;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.SOAP_ACTION;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.SOAP_VERSION;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.COMPATIBILITY.AXIS1;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTPS.KEYSTORE_FILE;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTPS.KEYSTORE_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTPS.TRUSTSTORE_FILE;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.HTTPS.TRUSTSTORE_PASSWORD;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.WSA.FAULT_TO;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.WSA.FROM;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.WSA.REPLY_TO;
import static org.ow2.petals.binding.soap.SoapConstants.ServiceUnit.WSA.TO;

/**
 * Helper for Service Unit properties values
 * 
 * @author chamerling - eBM Websourcing
 */
public class SUPropertiesHelper {

    /**
     * Check if the given parameter is present
     * 
     * @param parameterName the parameter name
     * @param extensions the SU extensions
     * @return
     */
    protected static final boolean checkPresent(final String parameterName,
            final ConfigurationExtensions extensions) {
        final String tmp = extensions.get(parameterName);
        return tmp != null && tmp.trim().length() > 0;
    }

    /**
     * Get the address
     * 
     * @return the address
     * @deprecated use {@link #getWSATo(ConfigurationExtensions)} instead
     */
    @Deprecated
    public static String getAddress(final ConfigurationExtensions extensions) {
        return getString(ADDRESS, extensions);
    }

    /**
     * Get the boolean value of the parameter
     * 
     * @param parameterName the parameter name
     * @param extensions the SU extensions
     * @return the boolean value of the parameter according to the
     *         Boolean.parseBoolean method, true if the parameter has not been
     *         found.
     */
    protected static final boolean getBoolean(final String parameterName,
            final ConfigurationExtensions extensions) {
        boolean result = true;
        final String tmp = extensions.get(parameterName);
        if (tmp != null) {
            result = Boolean.parseBoolean(tmp.trim());
        }
        return result;
    }

    protected static final boolean getBoolean(final String parameterName,
            final ConfigurationExtensions extensions, final boolean defaultValue) {
        boolean result = true;
        final String tmp = extensions.get(parameterName);
        if (tmp == null) {
            result = defaultValue;
        } else {
            result = Boolean.parseBoolean(tmp.trim());
        }
        return result;
    }

    public static final String getHttpRedirection(final ConfigurationExtensions extensions) {
        return getString(HTTP_SERVICES_REDIRECTION, extensions);
    }

    /**
     * Get the service name. This name will be used to create the web service
     * 
     * @param extensions the SU extensions
     * @return
     */
    public static String getServiceName(final ConfigurationExtensions extensions) {
        return getString(SERVICE_NAME, extensions);
    }

    /**
     * Get the parameter value or return null if not found
     * 
     * @param parameterName
     * @param extensions the SU extensions
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
     * @param parameterName
     * @param defaultValue
     * @param extensions the SU extensions
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
     * @param extensions the SU extensions
     * @return
     */
    public static String getWSAFaultTo(final ConfigurationExtensions extensions) {
        return getString(FAULT_TO, extensions);
    }

    /**
     * @param extensions the SU extensions
     * @return
     */
    public static String getWSAFrom(final ConfigurationExtensions extensions) {
        return getString(FROM, extensions);
    }

    /**
     * @param extensions the SU extensions
     * @return
     */
    public static String getWSAReplyTo(final ConfigurationExtensions extensions) {
        return getString(REPLY_TO, extensions);
    }

    /**
     * Get the wsa-to
     * 
     * @return the wsa-to
     */
    public static String getWSATo(final ConfigurationExtensions extensions) {
        return getString(TO, extensions);
    }

    /**
     * Get the flag enabling the HTTP transport layer
     * 
     * @param extensions the SU extensions
     * @return
     */
    public static boolean isHttpsTransportEnabled(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_HTTPS_TRANSPORT, extensions, false);
    }

    /**
     * Get the flag enabling the HTTP transport layer
     * 
     * @param extensions the SU extensions
     * @return
     */
    public static boolean isHttpTransportEnabled(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_HTTP_TRANSPORT, extensions);
    }

    /**
     * Get the flag enabling the JMS transport layer
     * 
     * @param extensions the SU extensions
     * @return
     */
    public static boolean isJmsTransportEnabled(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_JMS_TRANSPORT, extensions, false);
    }

    /**
     * @param extensions the SU extensions
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
     * @param extensions the SU extensions
     * @return
     */
    public static boolean retrieveInjectHeader(final ConfigurationExtensions extensions) {
        return getBoolean(INJECT_HEADERS, extensions);
    }

    /**
     * Creates a new instance if {@link SUPropertiesHelper}
     */
    protected SUPropertiesHelper() {
        super();
    }

    public static List<DocumentFragment> retrieveHeaderToInject(
            final ConfigurationExtensions extensions) {

        String toInject = getString(HEADERS_TO_INJECT, extensions);
        if (toInject == null)
            return null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            // The list of headers to inject is not necessarily an XML tree
            // eg. <header1/><header2/>
            // So, add a root node...
            doc = factory.newDocumentBuilder().parse(
                    new InputSource(new StringReader("<R>" + toInject + "</R>")));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        Element root = doc.getDocumentElement();
        List<DocumentFragment> result = null;
        while (root.hasChildNodes()) { // root element ignored (added above...)
            DocumentFragment docfrag = doc.createDocumentFragment();
            docfrag.appendChild(root.removeChild(root.getFirstChild()));
            if (result == null)
                result = new ArrayList<DocumentFragment>();
            result.add(docfrag);
        }
        return result;
    }

    /**
     * Is Axis 1 compatibility enabled
     * 
     * @param extensions the SU extensions
     * @return true if the Axis 1 compatibility is enabled, otherwise false
     */
    public static boolean isAxis1CompatibilityEnabled(final ConfigurationExtensions extensions) {
        String compatibility = extensions.get(COMPATIBILITY);
        return compatibility != null && compatibility.equals(AXIS1);
    }

    /**
     * Get the keystore file location (HTTPS)
     * 
     * @param extensions the SU extensions
     * @return the keystore file location
     */
    public static String getKeystoreFile(final ConfigurationExtensions extensions) {
        return extensions.get(KEYSTORE_FILE);
    }

    /**
     * Get the keystore password (HTTPS)
     * 
     * @param extensions the SU extensions
     *            the SU extensions
     * @return the keystore password
     */
    public static String getKeystorePassword(final ConfigurationExtensions extensions) {
        return extensions.get(KEYSTORE_PASSWORD);
    }

    /**
     * Get the truststore file location (HTTPS)
     * 
     * @param extensions the SU extensions
     * @return the truststore file location
     */
    public static String getTruststoreFile(final ConfigurationExtensions extensions) {
        return extensions.get(TRUSTSTORE_FILE);
    }

    /**
     * Get the truststore password (HTTPS)
     * 
     * @param extensions the SU extensions
     * @return the truststore password
     */
    public static String getTruststorePassword(final ConfigurationExtensions extensions) {
        return extensions.get(TRUSTSTORE_PASSWORD);
    }

    /**
     * Return if the WSA-Addressing is enabled
     * 
     * @param extensions the SU extensions
     * @return true if the WSA-Addressing is enabled, otherwise false
     */
    public static boolean isWSAEnabled(final ConfigurationExtensions extensions) {
        return getBoolean(ENABLE_WSA, extensions, false);
    }
    
    /**
     * Return the Basic Authentication username
     * 
     * @param extensions the SU parameters
     * @return the Basic Authentication username
     */
    public static String getBasicAuthUser(final ConfigurationExtensions extensions) {
        return extensions.get(BASIC_AUTH_USERNAME);
    }   
    
    /**
     * Return the Basic Authentication password
     * 
     * @param extensions the SU parameters
     * @return the Basic Authentication password
     */
    public static String getBasicAuthPwd(final ConfigurationExtensions extensions) {
        return extensions.get(BASIC_AUTH_PASSWORD);
    }
    
    /**
     * Get the modules
     * 
     * @param extensions the SU parameters
     * @return the list of modules
     */
    public static final List<String> getModules(final ConfigurationExtensions extensions) {
        final List<String> result = new ArrayList<String>();

        // get modules from extension
        final String token = extensions.get(MODULES);

        if (token != null) {
            // get individual modules values
            final StringTokenizer st = new StringTokenizer(token, ",");
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
            
            if(!result.contains(Constants.MODULE_ADDRESSING)) {
                result.add(Constants.MODULE_ADDRESSING);
            }
        }
        
        return result;
    }
    
    /**
     * Get the service parameters for the associated service
     * 
     * @param extensions the SU parameters
     * @return the services parameters (XML)
     */
    public static final String getServiceParameters(final ConfigurationExtensions extensions) {
        return extensions.get(SERVICE_PARAMETERS);
    }
    
    /**
     * Retrieve, in the jbi.xml descriptor, the SOAP version to use for
     * SoapEnvelopeNamespaceURI Search a <em>soap-version</em> tag. <br>
     * Legal values are <b>1.1</b> and <b>1.2</b>. 1.1 is used if no tag is
     * found.
     * 
     * @param extensions the SU parameters
     * @return the Axis2 soapEnvelopeNamespaceURI constant.
     * @see org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI
     * @see org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
     */
    public static final String retrieveSOAPEnvelopeNamespaceURI(
            final ConfigurationExtensions extensions) {
        String soapEnvelopeNamespaceURI = SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        final String soapVersion = extensions.get(SOAP_VERSION);

        if (SOAP_VERSION_12.equals(soapVersion)) {
            soapEnvelopeNamespaceURI = SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        }
        
        return soapEnvelopeNamespaceURI;
    }
    
    /**
     * Get the cleanup-transport property value (true/false).
     * 
     * @param extensions the SU parameters
     * 
     * @return the value of the cleanup-transport property (true if the property is not present)
     */
    public  static final boolean retrieveCleanupTransport(final ConfigurationExtensions extensions) {
        final String clean = extensions.get(CLEANUP_TRANSPORT);
        
        if (clean == null) {
            return true;
        } else {
            return Boolean.valueOf(clean);
        }
    }
    
    /**
     * Retrieve the chunked mode property value
     * 
     * @param extensions the SU parameters
     * 
     * @return the chunked mode property value
     */
    public final static String retrieveChunkedMode(final ConfigurationExtensions extensions) {
        return extensions.get(CHUNKED_MODE);
    }



    /**
     * Retrieve the default SOAP action from the extensions
     * 
     * @param extensions the SU parameters
     * 
     * @return the default SOAP action
     */
    public final static String retrieveDefaultSOAPAction(final ConfigurationExtensions extensions) {
        final String soapAction = extensions.get(SOAP_ACTION);
        return soapAction;
    }
    
    /**
     * Retrieve the proxy settings from the extensions. The proxy-host value is
     * required. The other ones will be set to default values by Axis if they
     * have not been setted by the SU.
     * 
     * @param extensions the SU parameters
     * @return the proxy settings if they are present in the extensions, null
     *         otherwise.
     */
    public static final HttpTransportProperties.ProxyProperties retrieveProxySettings(
            final ConfigurationExtensions extensions) {

        HttpTransportProperties.ProxyProperties proxyProperties = null;
        if (extensions.get(PROXY_HOST) != null) {
            // proxy host is required, if it is not set the proxy mode is not
            // activated

            proxyProperties = new HttpTransportProperties.ProxyProperties();

            final String domain = extensions.get(PROXY_DOMAIN);
            final String password = extensions.get(PROXY_PASSWORD);
            final String proxyHost = extensions.get(PROXY_HOST);

            int proxyPort = -1;
            final String tmp = extensions.get(PROXY_PORT);
            if (tmp != null) {
                try {
                    proxyPort = Integer.parseInt(tmp);
                } catch (final NumberFormatException e) {
                }
            }

            final String proxyUserName = extensions.get(PROXY_USER);

            proxyProperties.setDomain(domain);
            proxyProperties.setPassWord(password);
            proxyProperties.setProxyName(proxyHost);
            proxyProperties.setProxyPort(proxyPort);
            proxyProperties.setUserName(proxyUserName);
        }

        return proxyProperties;
    }
    
    /**
     * Set the basic authentication parameters for the outgoing message
     * request.
     * 
     * @param extensions the SU parameters
     * @param options the Axis2 option
     */
    @SuppressWarnings("unchecked")
    public static final void setBasicAuthentication(
            final ConfigurationExtensions extensions, final Options options) {
        HttpTransportProperties.Authenticator basicAuthentication = null;
        final String username = SUPropertiesHelper.getBasicAuthUser(extensions);
        String password = SUPropertiesHelper.getBasicAuthPwd(extensions);

        if (username != null) {
            if (password == null) {
                password = "";
            }

            basicAuthentication = new HttpTransportProperties.Authenticator();
            basicAuthentication.setUsername(username);
            basicAuthentication.setPassword(password);
            basicAuthentication.setPreemptiveAuthentication(true);
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
                    basicAuthentication);
        }
    }
}
