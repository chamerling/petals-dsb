/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.federation.core.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.api.ClientManager;

/**
 * The default federation federationServiceClient implementation. This client is
 * used to contact federation clients ie from federation server to federation
 * client.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationClientImpl implements FederationClientWithCallback {

    private String name;

    private String callbackURL;

    AtomicLong counter = new AtomicLong(0);

    private static Log logger = LogFactory.getLog(FederationClientImpl.class);

    /**
     * The current node service callback which is used to receive messages from
     * the federation server. This is the implementation and not the exposed
     * service!
     */
    private org.petalslink.dsb.federation.api.client.FederationService serviceImplementation;

    private ServiceManager serviceManager;

    private ClientManager clientManager;

    private final Map<String, CountDownLatch> latches;

    private final Map<String, Set<ServiceEndpoint>> endpoints;

    private final String serverURL;

    private final String serverURLMgmt;

    /**
     * 
     */
    public FederationClientImpl(String name, String callbackURL, String serverURL,
            String serverURLMgmt) {
        this.name = name;
        this.callbackURL = callbackURL;
        this.serverURL = serverURL;
        this.serverURLMgmt = serverURLMgmt;
        this.latches = new ConcurrentHashMap<String, CountDownLatch>(100);
        this.endpoints = new ConcurrentHashMap<String, Set<ServiceEndpoint>>(100);
        // instanciate federationServiceClient from serverURL
    }

    /**
     * @param serviceManager
     *            the serviceManager to set
     */
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * @return the serviceManager
     */
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * Add the callback so that the federation server can communicate with the
     * current node
     */
    public void setServiceImplementation(
            org.petalslink.dsb.federation.api.client.FederationService federationService) {
        this.serviceImplementation = federationService;
    }

    public void stop() {
        if (this.getServiceManager() != null) {
            this.getServiceManager().stop();
        }
    }

    /**
     * 
     */
    public void start() {
        FederationClientServiceWrapperImpl service = this.createServiceWrapper();

        if (this.getServiceManager() != null) {
            this.getServiceManager().expose(this.callbackURL, service);
        }
    }

    /**
     * 
     */
    private FederationClientServiceWrapperImpl createServiceWrapper() {
        FederationClientServiceWrapperImpl service = new FederationClientServiceWrapperImpl(this);
        service.setCallback(this);
        service.setClientService(this.serviceImplementation);
        service.setFederationServiceClient(this.getClientManager().getClient(this.serverURL));
        return service;
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Client invoke");
        }
        this.getClientManager().getClient(this.serverURL).invoke(message, this.name,
                this.getNewUUID());
    }

    /**
     * {@inheritDoc}
     */
    public void join() throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Client join federation");
        }
        this.getClientManager().getManagementClient(this.serverURLMgmt).join(this.name,
                this.callbackURL);
    }

    /**
     * {@inheritDoc}
     */
    public void leave() throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Client leave federation");
        }
        this.getClientManager().getManagementClient(this.serverURLMgmt).leave(this.name);
    }

    /**
     * Remote lookup {@inheritDoc}
     */
    public Set<ServiceEndpoint> lookup(EndpointQuery query) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Client lookup");
        }
        Set<ServiceEndpoint> result = new HashSet<ServiceEndpoint>();
        String id = this.getNewUUID();
        CountDownLatch latch = new CountDownLatch(1);
        this.latches.put(id, latch);
        this.getClientManager().getClient(this.serverURL).lookup(query, this.name, id);
        // wait for a response from the callback service... @see
        // onLookupResponse
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Waiting for response, id is " + id);
            }
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Can not get a response within 30 seconds...");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Got a response for id " + id);
        }
        Set<ServiceEndpoint> endpoints = this.endpoints.remove(id);
        if (endpoints != null) {
            result.addAll(endpoints);
        }

        return result;
    }

    public void onLookupResponse(Set<ServiceEndpoint> endpoints, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Client onLookupResponse for id " + id);
        }
        CountDownLatch latch = this.latches.remove(id);
        if (latch != null) {
            this.endpoints.put(id, endpoints);
            latch.countDown();
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Can not find something to unlock...");
            }
        }
    }

    private String getNewUUID() {
        return "fed-" + this.name + "-" + this.counter.getAndIncrement() + "-"
                + UUID.randomUUID().toString();
    }

    /**
     * @return the clientManager
     */
    public ClientManager getClientManager() {
        return this.clientManager;
    }

    /**
     * @param clientManager
     *            the clientManager to set
     */
    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the callbackURL
     */
    public String getCallbackURL() {
        return this.callbackURL;
    }

    /**
     * @param callbackURL
     *            the callbackURL to set
     */
    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }
}
