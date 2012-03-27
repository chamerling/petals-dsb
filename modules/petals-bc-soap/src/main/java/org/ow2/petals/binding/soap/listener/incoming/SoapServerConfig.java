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

package org.ow2.petals.binding.soap.listener.incoming;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.ow2.petals.binding.soap.util.NetworkUtil;

import static org.apache.axis2.transport.TransportListener.HOST_ADDRESS;
import static org.apache.axis2.transport.TransportListener.PARAM_PORT;

import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_ACCEPTORS;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_CONTEXT;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_SERVICES_MAPPING;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MAX;
import static org.ow2.petals.binding.soap.SoapConstants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MIN;

/**
 * This class is used to store only the SOAP server related configuration
 * values.
 * 
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class SoapServerConfig {

    /**
     * The port used for incoming SOAP requests
     */
    private int httpPort;

    /**
     * The host
     */
    private String host;

    /**
     * The host IP
     */
    private InetAddress hostIP;

    private String servicesMapping;

    private String servicesContext;

    /**
     * Restrict access to a particular network interface
     */
    private boolean restricted;

    /**
     * Define if HTTPS is enabled
     */
    private boolean isHttpsEnabled;

    /**
     * The port used for incoming SOAP requests
     */
    private int httpsPort;

    /**
     * The type of the keystore (JKS / PKCS12)
     */
    private String httpsKeystoreType;

    /**
     * The absolute path of the keystore
     */
    private String httpsKeytoreFile;

    /**
     * The password of the keystore
     */
    private String httpsKeytorePassword;

    /**
     * The password of the key
     */
    private String httpsKeytoreKeyPassword;

    /**
     * The type of the truststore (JKS / PKCS12)
     */
    private String httpsTruststoreType;

    /**
     * The absolute path of the truststore
     */
    private String httpsTruststoreFile;

    /**
     * The password of the truststore
     */
    private String httpsTruststorePassword;

    /**
     * The server provides/!provides the services list
     */
    private boolean providesList;

    private HashMap<String, String> redirects;

    /**
     * The thread pool maximum size
     */
    protected int jettyThreadMaxPoolSize;

    /**
     * The thread pool minimum size
     */
    protected int jettyThreadMinPoolSize;

    /**
     * The number of acceptors
     */
    protected int jettyAcceptors;

    /**
     * isHostValid
     */
    private boolean isValidHostName;

    /**
     * Creates a new instance of {@link SoapServerConfig}
     * 
     */
    public SoapServerConfig(final Logger logger, String host, int httpPort) {
        this.setHost(logger, host);
        this.httpPort = httpPort;

        this.isHttpsEnabled = false;
        this.servicesContext = DEFAULT_HTTP_SERVICES_CONTEXT;
        this.servicesMapping = DEFAULT_HTTP_SERVICES_MAPPING;
        this.jettyAcceptors = DEFAULT_HTTP_ACCEPTORS;
        this.jettyThreadMinPoolSize = DEFAULT_HTTP_THREAD_POOL_SIZE_MIN;
        this.jettyThreadMaxPoolSize = DEFAULT_HTTP_THREAD_POOL_SIZE_MAX;
        this.servicesContext = DEFAULT_HTTP_SERVICES_CONTEXT;
        this.servicesMapping = DEFAULT_HTTP_SERVICES_MAPPING;
    }

    /**
     * Validate the specified host and add it to the server configuration
     * 
     * @param config
     *            the server configuration
     * 
     * @throws JBIException
     *             the host is not valid
     */
    private void setHost(final Logger logger, final String host) {

        Set<Inet4Address> localIPv4Addresses;

        try {

            localIPv4Addresses = NetworkUtil.getAllLocalIPv4InetAddresses();

            if (host == null || host.length() == 0 || host.equals("null")) {
                // no host specified, do not restrict and use all addresses
                try {
                    this.hostIP = getIPv4AddressForHost(localIPv4Addresses);
                    this.isValidHostName = true;
                    this.restricted = false;
                } catch (UnknownHostException uhe) {
                    this.isValidHostName = false;
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, uhe.getMessage(), uhe);
                    }
                }

            } else {
                // a host is specified, check if is valid

                // get the IP address from the host name
                InetAddress address = null;
                try {
                    address = InetAddress.getByName(host);
                } catch (final UnknownHostException uhe) {
                    this.isValidHostName = false;
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, uhe.getMessage(), uhe);
                    }
                }

                // the IP address exists, check if it is a local one
                if (localIPv4Addresses.contains(address)) {
                    this.hostIP = address;
                    this.isValidHostName = true;
                    this.host = host;
                    this.restricted = true;
                } else {
                    this.isValidHostName = false;
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, host + " does not correspond to a local host");
                    }
                }
            }

        } catch (SocketException se) {
            this.isValidHostName = false;
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, se.getMessage(), se);
            }
        }
    }

    private static final InetAddress getIPv4AddressForHost(Set<Inet4Address> localIPv4Addresses)
            throws UnknownHostException {
        InetAddress ipAddress = null;

        // take the first IP v4 address which is not the loopback one
        InetAddress hostIPv4Address = null;
        if (localIPv4Addresses != null && localIPv4Addresses.size() > 0) {
            final Iterator<Inet4Address> localIPv4AddressesIter = localIPv4Addresses.iterator();
            while (localIPv4AddressesIter.hasNext() && hostIPv4Address == null) {
                final InetAddress ipv4Addressr = localIPv4AddressesIter.next();
                if (!ipv4Addressr.isLoopbackAddress()) {
                    hostIPv4Address = ipv4Addressr;
                }
            }
        }

        // if no IP v4 found, use localhost
        if (hostIPv4Address == null) {
            ipAddress = InetAddress.getLocalHost();
        } else {
            ipAddress = hostIPv4Address;
        }

        return ipAddress;
    }

    /**
     * Return if the host is valid
     * 
     * @return true if the host is valid, otherwise false
     */
    public boolean isValidHostName() {
        return this.isValidHostName;
    }

    /**
     * Return if the HTTP/HTTPS server is configured to listen to only one
     * interface or not
     * 
     * @return true if the HTTP/HTTPS server listens to only one interface,
     *         otherwise false
     */
    public boolean isRestricted() {
        return this.restricted;
    }

    /**
     * Return the HTTP/HTTPS host name
     * 
     * @return the HTTP/HTTPS host name
     */
    public String getHostAddress() {
        return this.hostIP.getHostAddress();
    }

    /**
     * Return the HTTP/HTTPS host names (to display)
     * 
     * @return the HTTP/HTTPS host names (to display)
     */
    public String getHostToDisplay() {
        String hostToDisplay;

        if (!this.isValidHostName) {
            hostToDisplay = "Invalid Host Name";
        } else if (this.isRestricted()) {
            String ipAddress = this.hostIP.getHostAddress();
            if (ipAddress.equals(host)) {
                hostToDisplay = "Host : " + ipAddress;
            } else {
                String hostName = this.hostIP.getHostName();
                hostToDisplay = "Host : " + ipAddress + " (" + hostName + ")";
            }
        } else {
            try {
                Set<Inet4Address> localIPv4Addresses = NetworkUtil.getAllLocalIPv4InetAddresses();

                Iterator<Inet4Address> localIPv4AddressesIterator = localIPv4Addresses.iterator();
                StringBuffer hostList = new StringBuffer("Hosts : ");
                while (localIPv4AddressesIterator.hasNext()) {
                    Inet4Address localIPv4Address = localIPv4AddressesIterator.next();
                    String hostName = localIPv4Address.getHostName();
                    String ipAddress = localIPv4Address.getHostAddress();
                    if (hostName.equals(ipAddress)) {
                        hostList.append(ipAddress);
                    } else {
                        hostList.append(ipAddress + " (" + hostName + ")");
                    }

                    if (localIPv4AddressesIterator.hasNext()) {
                        hostList.append(" / ");
                    }
                }
                hostToDisplay = hostList.toString();
            } catch (SocketException e) {
                hostToDisplay = "Host : Unable to display the host names";
                e.printStackTrace();
            }
        }

        return hostToDisplay;
    }

    /**
     * @return the jettyAcceptors
     */
    public int getJettyAcceptors() {
        return this.jettyAcceptors;
    }

    /**
     * @return the jettyThreadMaxPoolSize
     */
    public int getJettyThreadMaxPoolSize() {
        return this.jettyThreadMaxPoolSize;
    }

    /**
     * @return the jettyThreadMinPoolSize
     */
    public int getJettyThreadMinPoolSize() {
        return this.jettyThreadMinPoolSize;
    }

    /**
     * @return the HTTP port
     */
    public int getHttpPort() {
        return this.httpPort;
    }

    /**
     * @return the HTTPS port
     */
    public int getHttpsPort() {
        return this.httpsPort;
    }

    /**
     * @return the servicesContext
     */
    public String getServicesContext() {
        return this.servicesContext;
    }

    /**
     * @return the servicesMapping
     */
    public String getServicesMapping() {
        return this.servicesMapping;
    }

    /**
     * @return the providesList
     */
    public boolean isProvidesList() {
        return providesList;
    }

    /**
     * Return if HTTPS is enabled
     * 
     * @return true if HTTPS is enabled, otherwise false
     */
    public boolean isHttpsEnabled() {
        return this.isHttpsEnabled;
    }

    /**
     * Get the keystore type (JKS / PKCS12)
     * 
     * @return the keystore type (JKS / PKCS12)
     */
    public String getHttpsKeystoreType() {
        return this.httpsKeystoreType;
    }

    /**
     * Get the keystore file path
     * 
     * @return the keystore file path
     */
    public String getHttpsKeystoreFile() {
        return this.httpsKeytoreFile;
    }

    /**
     * Get the keystore password
     * 
     * @return the keystore password
     */
    public String getHttpsKeystorePassword() {
        return this.httpsKeytorePassword;
    }

    /**
     * Get the key password
     * 
     * @return the key password
     */
    public String getHttpsKeystoreKeyPassword() {
        return this.httpsKeytoreKeyPassword;
    }

    /**
     * Get the truststore type (JKS / PKCS12)
     * 
     * @return the truststore type (JKS / PKCS12)
     */
    public String getHttpsTruststoreType() {
        return this.httpsTruststoreType;
    }

    /**
     * Get the truststore file path
     * 
     * @return the truststore file path
     */
    public String getHttpsTruststoreFile() {
        return this.httpsTruststoreFile;
    }

    /**
     * Get the truststore password
     * 
     * @return the truststore password
     */
    public String getHttpsTruststorePassword() {
        return this.httpsTruststorePassword;
    }

    /**
     * @param jettyAcceptors
     *            the jettyAcceptors to set
     */
    public void setJettyAcceptors(final int jettyAcceptors) {
        this.jettyAcceptors = jettyAcceptors;
    }

    /**
     * @param jettyThreadMaxPoolSize
     *            the jettyThreadMaxPoolSize to set
     */
    public void setJettyThreadMaxPoolSize(final int jettyThreadMaxPoolSize) {
        this.jettyThreadMaxPoolSize = jettyThreadMaxPoolSize;
    }

    /**
     * @param jettyThreadMinPoolSize
     *            the jettyThreadMinPoolSize to set
     */
    public void setJettyThreadMinPoolSize(final int jettyThreadMinPoolSize) {
        this.jettyThreadMinPoolSize = jettyThreadMinPoolSize;
    }

    /**
     * @param HTTP
     *            port the HTTP port to set
     */
    public void setHttpPort(final int port) {
        this.httpPort = port;
    }

    /**
     * @param HTTPS
     *            port the HTTPS port to set
     */
    public void setHttpsPort(final int port) {
        this.httpsPort = port;
    }

    /**
     * @param providesList
     *            the providesList to set
     */
    public void setProvidesList(final boolean providesList) {
        this.providesList = providesList;
    }

    /**
     * @param restrict
     *            the restrict to set
     */
    public void setRestrict(final boolean restricted) {
        this.restricted = restricted;
    }

    /**
     * @param servicesContext
     *            the servicesContext to set
     */
    public void setServicesContext(final String servicesContext) {
        this.servicesContext = servicesContext;
    }

    /**
     * @param servicesMapping
     *            the servicesMapping to set
     */
    public void setServicesMapping(final String servicesMapping) {
        this.servicesMapping = servicesMapping;
    }

    /**
     * Define if HTTPS is enabled
     * 
     * @param isHttpsEnabled
     *            a flag set to true if HTTPS is enabled, otherwise false
     */
    public void setHttpsEnabled(boolean isHttpsEnabled) {
        this.isHttpsEnabled = isHttpsEnabled;
    }

    /**
     * Set the type of the keystore (JKS / PKCS12)
     * 
     * @param httpsKeystoreType
     *            the type of the keystore (JKS / PKCS12)
     */
    public void setHttpsKeytoreType(String httpsKeystoreType) {
        this.httpsKeystoreType = httpsKeystoreType;
    }

    /**
     * Set the keystore absolute file path
     * 
     * @param httpsKeytoreFile
     *            the keystore absolute file path
     */
    public void setHttpsKeytoreFile(String httpsKeytoreFile) {
        this.httpsKeytoreFile = httpsKeytoreFile;
    }

    /**
     * Set the keystore password
     * 
     * @param httpsKeytorePassword
     *            the keystore password
     */
    public void setHttpsKeytorePassword(String httpsKeytorePassword) {
        this.httpsKeytorePassword = httpsKeytorePassword;
    }

    /**
     * Set the key password
     * 
     * @param httpsKeytoreKeyPassword
     *            the key password
     */
    public void setHttpsKeyPassword(String httpsKeytoreKeyPassword) {
        this.httpsKeytoreKeyPassword = httpsKeytoreKeyPassword;
    }

    /**
     * Set the type of the truststore (JKS / PKCS12)
     * 
     * @param httpsTruststoreType
     *            the type of the truststore (JKS / PKCS12)
     */
    public void setHttpsTruststoreType(String httpsTruststoreType) {
        this.httpsTruststoreType = httpsTruststoreType;
    }

    /**
     * Set the truststore absolute file path
     * 
     * @param httpsTruststoreFile
     *            the truststore absolute file path
     */
    public void setHttpsTruststoreFile(String httpsTruststoreFile) {
        this.httpsTruststoreFile = httpsTruststoreFile;
    }

    /**
     * Set the truststore password
     * 
     * @param httpsTruststorePassword
     *            the truststore password
     */
    public void setHttpsTruststorePassword(String httpsTruststorePassword) {
        this.httpsTruststorePassword = httpsTruststorePassword;
    }

    /**
     * Get the EPRs for the specified Axis service name and protocol
     * 
     * @param serviceName
     *            the Axis service name
     * @param protocol
     *            the protocol
     * @param axisConfigurationContext
     *            the Axis 2 configuration
     * 
     * @return the endpoint reference for the specified Axis service and
     *         protocol
     */
    public EndpointReference[] getEPRsForAxisService(String serviceName, String transport)
            throws AxisFault {
        return new EndpointReference[] { new EndpointReference(
                getServiceURL(serviceName, transport)) };
    }

    /**
     * Get the URL of the service for the specified service name and protocol
     * 
     * @param serviceName
     *            the Axis service name
     * @param transport
     *            the transport
     * 
     * @return the URL of the service
     */
    public String getServiceURL(String serviceName, String transport) {
        String servicesURL = getServicesURL(transport);
        return servicesURL + serviceName;
    }

    /**
     * Get the URL of the base URL for the services
     * 
     * @param transport
     *            the transport
     * 
     * @return
     */
    public String getServicesURL(String transport) {
        String baseURL = getBaseURL(transport);
        String servicesURL = baseURL + this.servicesContext + "/" + this.servicesMapping + "/";
        return servicesURL;
    }

    /**
     * Initialize the transport description for Axis
     * 
     * @param transprtInDesc
     *            the transport description
     * @param port
     *            the port to use to initialize the transport description
     * 
     * @throws AxisFault
     */
    public void initTransportListenerForAxis(TransportInDescription transprtInDesc, String transport)
            throws AxisFault {
        // These values are defined by the component and not by the Axis2
        // configuration file so we set them if Axis2 do something with them...
        Parameter portParam = transprtInDesc.getParameter(PARAM_PORT);
        if (portParam == null) {
            int port = getPortForTransport(transport);
            portParam = new Parameter(PARAM_PORT, Integer.toString(port));
        }
        transprtInDesc.addParameter(portParam);

        Parameter hostParam = transprtInDesc.getParameter(HOST_ADDRESS);
        if (hostParam == null) {
            hostParam = new Parameter(HOST_ADDRESS, this.getHostAddress());
        }
        transprtInDesc.addParameter(hostParam);
    }

    /**
     * Define a HTTP redirection (to customize URLs)
     * 
     * @param from
     *            Original URI
     * @param to
     *            Destination URI
     */
    public void addRedirect(String from, String to) {
        if (this.redirects == null)
            this.redirects = new HashMap<String, String>();
        this.redirects.put(from, to);
    }

    public String getRedirect(String from) {
        if (this.redirects == null)
            return null;
        else
            return this.redirects.get(from);
    }

    public void removeRedirect(String from) {
        if (this.redirects != null)
            this.redirects.remove(from);
    }

    public String getBaseURL() {
        return getBaseURL(Constants.TRANSPORT_HTTP);
    }

    public String getBaseURL(String transport) {
        int port = getPortForTransport(transport);

        String baseURL;
        if (host == null || host.length() == 0 || host.equals("null")) {
            baseURL = transport + "://" + this.getHostAddress() + ":" + port + "/";
        } else {
            baseURL = transport + "://" + this.host + ":" + port + "/";
        }

        return baseURL;
        // int port = getPortForTransport(transport);
        //
        // String baseURL = transport + "://" + this.getHostAddress() + ":" +
        // port + "/";
        //
        // return baseURL;
    }

    private int getPortForTransport(String transport) {
        int port;

        if (transport.equals(Constants.TRANSPORT_HTTP)) {
            port = this.httpPort;
        } else if (transport.equals(Constants.TRANSPORT_HTTPS)) {
            port = this.httpsPort;
        } else {
            throw new IllegalArgumentException("Transport not supported by the SOAP server.");
        }

        return port;
    }

    public String buildServiceAddress(String transport, String serviceName) {
        final StringBuffer sb = new StringBuffer(getBaseURL(transport));
        sb.append(getServicesContext());
        sb.append('/');
        sb.append(getServicesMapping());
        sb.append('/');
        sb.append(serviceName);
        return sb.toString();
    }
}
