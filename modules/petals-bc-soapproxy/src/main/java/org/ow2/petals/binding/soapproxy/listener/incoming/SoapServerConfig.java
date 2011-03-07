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

package org.ow2.petals.binding.soapproxy.listener.incoming;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ow2.petals.binding.soapproxy.util.NetworkUtil;

import static org.ow2.petals.binding.soapproxy.Constants.HttpServer.DEFAULT_HTTP_ACCEPTORS;
import static org.ow2.petals.binding.soapproxy.Constants.HttpServer.DEFAULT_HTTP_HOST;
import static org.ow2.petals.binding.soapproxy.Constants.HttpServer.DEFAULT_HTTP_PORT;
import static org.ow2.petals.binding.soapproxy.Constants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MAX;
import static org.ow2.petals.binding.soapproxy.Constants.HttpServer.DEFAULT_HTTP_THREAD_POOL_SIZE_MIN;

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
    private int port = DEFAULT_HTTP_PORT;

    /**
     * The current interfaces which can be use for incoming listening
     */
    private List<InetAddress> addresses;
    
    /**
     * The host
     */
    private String host;

    /**
     * Restrict access to a particular network interface
     */
    private boolean restrict = false;

    /**
     * The servlet mappping : axis2/<b>services</b>
     */
    private String servicesMapping;

    /**
     * The servlet context : <b>axis2</b>/services
     */
    private String servicesContext;

    /**
     * The protocol used for this server (http / https ...)
     */
    private String protocol;

    /**
     * The server provides/!provides the services list
     */
    private boolean providesList;

    /**
     * Default value is set to 255 (The jetty implementation default)
     */
    protected int jettyThreadMaxPoolSize = DEFAULT_HTTP_THREAD_POOL_SIZE_MAX;

    /**
     * Default value is set to 1 (The jetty implementation default)
     */
    protected int jettyThreadMinPoolSize = DEFAULT_HTTP_THREAD_POOL_SIZE_MIN;

    /**
     * Default value is set to 4
     */
    protected int jettyAcceptors = DEFAULT_HTTP_ACCEPTORS;

    protected String wsnContext = "wsn";

    protected String wsnProducerMapping = "producer";

    protected String baseURL;

    protected String wsnProducerURL;

    protected String servicesURL;

    /**
     * Creates a new instance of {@link SoapServerConfig}
     * 
     */
    public SoapServerConfig() {
        this.addresses = new ArrayList<InetAddress>();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * @return the providesList
     */
    public boolean isProvidesList() {
        return this.providesList;
    }

    /**
     * @param providesList
     *            the providesList to set
     */
    public void setProvidesList(final boolean providesList) {
        this.providesList = providesList;
    }

    // /**
    // * @return the host
    // */
    // public InetAddress getHost() {
    // return this.host;
    // }

    /**
     * @param host
     *            the host to set
     */
    protected void addAddress(final InetAddress ia) {
        this.addresses.add(ia);
    }

    /**
     * Add all the {@link InetAddress}
     * 
     * @param addresses
     */
    protected void addAddresses(final Set<Inet4Address> addresses) {
        this.addresses.addAll(addresses);
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the servicesContext
     */
    public String getServicesContext() {
        return this.servicesContext;
    }

    /**
     * @param servicesContext
     *            the servicesContext to set
     */
    public void setServicesContext(final String servicesContext) {
        this.servicesContext = servicesContext;
    }

    /**
     * @return the servicesMapping
     */
    public String getServicesMapping() {
        return this.servicesMapping;
    }

    /**
     * @param servicesMapping
     *            the servicesMapping to set
     */
    public void setServicesMapping(final String servicesMapping) {
        this.servicesMapping = servicesMapping;
    }

    /**
     * @return the jettyThreadMaxPoolSize
     */
    public int getJettyThreadMaxPoolSize() {
        return this.jettyThreadMaxPoolSize;
    }

    /**
     * @param jettyThreadMaxPoolSize
     *            the jettyThreadMaxPoolSize to set
     */
    public void setJettyThreadMaxPoolSize(final int jettyThreadMaxPoolSize) {
        this.jettyThreadMaxPoolSize = jettyThreadMaxPoolSize;
    }

    /**
     * @return the jettyThreadMinPoolSize
     */
    public int getJettyThreadMinPoolSize() {
        return this.jettyThreadMinPoolSize;
    }

    /**
     * @param jettyThreadMinPoolSize
     *            the jettyThreadMinPoolSize to set
     */
    public void setJettyThreadMinPoolSize(final int jettyThreadMinPoolSize) {
        this.jettyThreadMinPoolSize = jettyThreadMinPoolSize;
    }

    /**
     * @return the jettyAcceptors
     */
    public int getJettyAcceptors() {
        return this.jettyAcceptors;
    }

    /**
     * @param jettyAcceptors
     *            the jettyAcceptors to set
     */
    public void setJettyAcceptors(final int jettyAcceptors) {
        this.jettyAcceptors = jettyAcceptors;
    }

    /**
     * Get the services URL
     * 
     * @return
     */
    public String getServicesURL() {
        if (this.servicesURL == null) {
            this.servicesURL = this.buildServicesURL();
        }
        return this.servicesURL;
    }

    /**
     * Build the services URL from the current configuration properties
     * 
     * @return
     */
    private String buildServicesURL() {
        final StringBuffer sb = new StringBuffer(this.getBaseURL());
        sb.append('/');
        sb.append(this.getServicesContext());
        sb.append('/');
        sb.append(this.getServicesMapping());
        return sb.toString();
    }

    /**
     * 
     * @return
     */
    public String getBaseURL() {
        if (this.baseURL == null) {
            this.baseURL = this.buildBaseURL();
        }
        return baseURL;
    }

    /**
     * Get the root URL
     * 
     * @return
     */
    private String buildBaseURL() {
        final StringBuffer sb = new StringBuffer(this.getProtocol());
        sb.append("://");
        sb.append(this.getHost());
        if (this.getPort() > 0) {
            sb.append(':');
            sb.append(this.getPort());
        }

        return sb.toString();
    }

    /**
     * 
     * @return
     */
    public String getHost() {
        if (this.host == null) {
            this.host = this.buildHost();
        }
        return this.host;
    }
    
    /**
     * Get the host. If restricted, get the first address of the addresses set.
     * If not restricted, try to get an address which is not the local one since
     * it can be used from external callers.
     * 
     * @return
     */
    public String buildHost() {
        InetAddress result = null;

        if (restrict) {
            // get the first host, no matter if it is the loopback since it has
            // been configured by the user to be restricted ! The set should
            // contains only the user address
            if (addresses != null && addresses.size() > 0) {
                result = addresses.get(0);
            }
        } else {
            // get the first inet address which is not the loopback one since it
            // can be called from external clients
            if (addresses != null && addresses.size() > 0) {
                Iterator<InetAddress> iter = addresses.iterator();
                while (iter.hasNext() && result == null) {
                    InetAddress addr = iter.next();
                    if (!NetworkUtil.isLoopbackAddress(addr)) {
                        result = addr;
                    }
                }
            }
        }
        return (result == null ? DEFAULT_HTTP_HOST : result.getHostAddress());
    }

    /**
     * 
     * @return
     */
    public String getWSNProducerURL() {
        if (this.wsnProducerURL == null) {
            this.wsnProducerURL = buildWsnProducerURL();
        }
        return this.wsnProducerURL;
    }

    /**
     * 
     * @return
     */
    private String buildWsnProducerURL() {
        final StringBuffer sb = new StringBuffer(this.getBaseURL());
        sb.append('/');
        sb.append(this.getWsnContext());
        sb.append('/');
        sb.append(this.getWsnProducerMapping());
        return sb.toString();
    }

    /**
     * @return the wsnContext
     */
    public String getWsnContext() {
        return this.wsnContext;
    }

    /**
     * @param wsnContext
     *            the wsnContext to set
     */
    public void setWsnContext(final String wsnContext) {
        this.wsnContext = wsnContext;
    }

    /**
     * @return the wsnMapping
     */
    public String getWsnProducerMapping() {
        return this.wsnProducerMapping;
    }

    /**
     * @param wsnMapping
     *            the wsnMapping to set
     */
    public void setWsnProducerMapping(final String wsnMapping) {
        this.wsnProducerMapping = wsnMapping;
    }

    /**
     * @return the restrict
     */
    public boolean isRestrict() {
        return restrict;
    }

    /**
     * @param restrict
     *            the restrict to set
     */
    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }
}
