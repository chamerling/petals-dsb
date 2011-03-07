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
package org.petalslink.dsb.federation.core.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.PropagationStrategy;

/**
 * Lookup will return the results from the first response. Invoke contains all
 * that it is needed to invoke the service.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DefaultPropagationStrategy implements PropagationStrategy {

    private final FederationServer federationServer;

    /**
     * The correspondance between message ID and client. On message response,
     * get the initial client from the map and send it back the message.
     */
    private final Map<String, String> idClientMap;

    /**
     * The intial client which did the request. The key is the message ID, the
     * value is the initial client.
     */
    private final Map<String, String> initialClientMap;

    private final Map<String, Set<ServiceEndpoint>> endpoints;

    private final Map<String, AtomicLong> latches;

    // private final ExecutorService executorService;

    private static Log logger = LogFactory.getLog(DefaultPropagationStrategy.class);

    private final ExecutorService executorService;

    /**
	 * 
	 */
    public DefaultPropagationStrategy(final FederationServer federationServer) {
        this.federationServer = federationServer;
        this.idClientMap = new ConcurrentHashMap<String, String>(100);
        this.endpoints = new ConcurrentHashMap<String, Set<ServiceEndpoint>>(100);
        this.latches = new ConcurrentHashMap<String, AtomicLong>(100);
        this.initialClientMap = new ConcurrentHashMap<String, String>(100);

        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return DefaultPropagationStrategy.class.getCanonicalName();
    }

    /**
     * The clientId is the final client to invoke and not the source one!
     * {@inheritDoc}
     * 
     * @throws FederationException
     */
    public void invoke(MessageExchange message, String clientId, String id)
            throws FederationException {

        // store the message ID so that we can callback the client when the
        // response is there...
        // if (this.idClientMap.get(id) != null) {
        if ((this.initialClientMap.get(message.getId()) != null)
                && message.getStatus().equals("Done")) {
            // this is a response... So get the initial client and send it back
            // the response
            String initialClient = this.initialClientMap.remove(message.getId());
            if (logger.isInfoEnabled()) {
                logger.info("This is a response by " + clientId + " for initial client "
                        + initialClient);
            }

            if (initialClient != null) {
                // send the response to the client
                this.invoke2(message, initialClient, id);
            } else {
                logger.warn("Can not find a valid initial client ID");
            }
        } else {
            // this is a call...
            // store the messageID and the initial clientID
            this.idClientMap.put(id, clientId);
            this.initialClientMap.put(message.getId(), clientId);
            String clientIdToInvoke = this.getClientToInvoke(message, clientId);
            if (logger.isInfoEnabled()) {
                logger.info("Found the client '" + clientIdToInvoke + "' to forward message to");
            }
            if (clientIdToInvoke == null) {
                throw new FederationException(
                        "Can not define the client to invoke from the initial message");
            }
            // TODO Fire and Forget for all calls!
            this.invoke2(message, clientIdToInvoke, id);
        }
    }

    private void invoke2(MessageExchange message, String clientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Got an invoke call for client = '" + clientId + "'");
        }
        // get the federation node and container to reach...
        org.petalslink.dsb.federation.core.api.FederationClient client = this.federationServer
                .getClient(clientId);

        if (client == null) {
            String msg = "Can not find a valid client from clientID='" + clientId + "'";
            logger.warn(msg);
            throw new FederationException(msg);
        }

        try {
            this.federationServer.getClientManager().getClient(client.getCallbackURL()).invoke(
                    message, this.federationServer.getName(), id);
        } catch (FederationException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Got lookup call from client '" + clientId + "'");
        }

        // FIXME : If there are not clients... What to do?
        // get all the connected clients
        Set<org.petalslink.dsb.federation.core.api.FederationClient> clients = this.federationServer
                .getClients();

        // store the number of requests (all minus the client)...
        AtomicLong counter = new AtomicLong(clients.size() - 1);

        if (counter.get() <= 0) {
            // one way call, if there are not enough client, we must call reply
            // ourselves
            this.submitEmptyReply(clientId, id);
        } else {
            // TODO : Define a global timeout !
            this.idClientMap.put(id, clientId);
            this.latches.put(id, counter);
            this.endpoints.put(id, new HashSet<ServiceEndpoint>());

            // dummy implementation, call all then aggregate all the results...
            for (org.petalslink.dsb.federation.core.api.FederationClient federationClient : clients) {
                // submit lookup in separate threads
                if (!clientId.equals(federationClient.getName())) {
                    this.submitLookup(query, id, counter, federationClient);
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("Do not call the client which did the request");
                    }
                }
            }
        }
    }

    /**
     * @param query
     * @param id
     * @param counter
     * @param federationClient
     */
    private void submitLookup(final EndpointQuery query, final String id, final AtomicLong counter,
            final org.petalslink.dsb.federation.core.api.FederationClient federationClient) {
        Thread t = new Thread() {

            @Override
            public void run() {
                if (logger.isDebugEnabled()) {
                    logger.debug("Client " + federationClient.getName() + " with callback "
                            + federationClient.getCallbackURL());
                }
                try {
                    // TODO : Only the federation server can talk to this
                    // client!
                    DefaultPropagationStrategy.this.federationServer.getClientManager().getClient(
                            federationClient.getCallbackURL()).lookup(query,
                            DefaultPropagationStrategy.this.federationServer.getName(), id);
                } catch (FederationException e) {
                    counter.decrementAndGet();
                }
            }
        };
        this.executorService.submit(t);
    }

    /**
     * @param clientId
     * @param id
     */
    private void submitEmptyReply(final String clientId, final String id) {
        if (logger.isInfoEnabled()) {
            logger.info("Submit empty reply");
        }
        Thread t = new Thread() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    DefaultPropagationStrategy.this.lookupReply2(new HashSet<ServiceEndpoint>(),
                            clientId, id);
                } catch (FederationException e) {
                    e.printStackTrace();
                }
            }
        };
        this.executorService.submit(t);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Got a lookupReply call from client = '" + clientId + "'");
        }

        // aggregate response...
        if (endpoints != null) {
            // update the endpoints location...
            for (ServiceEndpoint serviceEndpoint : endpoints) {
                // add the current client ID from response to the domain path so
                // that the call to the endpoint can be routed to the right
                // federation client.
                serviceEndpoint.setSubdomainLocation(clientId + "/"
                        + serviceEndpoint.getSubdomainLocation());
            }

            Set<ServiceEndpoint> endpointsBuffer = this.endpoints.get(id);
            if (endpointsBuffer != null) {
                endpointsBuffer.addAll(endpoints);
            }
        }

        AtomicLong counter = this.latches.get(id);
        long remain = counter.decrementAndGet();
        if (logger.isInfoEnabled()) {
            logger.info("Waiting for " + remain + " more response");
        }

        if (remain <= 0) {
            this.latches.remove(id);

            if (this.idClientMap.get(id) != null) {
                // this is a response... So get the initial client and send it
                // back
                // the response
                String initialClient = this.idClientMap.remove(id);
                if (logger.isInfoEnabled()) {
                    logger.info("This is a response by " + clientId + " for initial client "
                            + initialClient);
                }
                // send the response to the client
                this.lookupReply2(this.endpoints.remove(id), initialClient, id);
            } else {
                // Failure, this is a one way call!
                if (logger.isInfoEnabled()) {
                    logger.info("Failure, can not find a client...");
                }
            }
        }
    }

    /**
     * Invoke reply on client
     * 
     * @param endpoints
     * @param id
     * @throws FederationException
     */
    private void lookupReply2(Set<ServiceEndpoint> endpoints, String initialClientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Send back the loopup reply to the initial client");
        }

        // get the federation node and container to reach...
        org.petalslink.dsb.federation.core.api.FederationClient client = this.federationServer
                .getClient(initialClientId);

        if (client == null) {
            throw new FederationException("Can not find a valid client stub from clientID='"
                    + initialClientId + "'");
        }
        try {
            this.federationServer.getClientManager().getClient(client.getCallbackURL())
                    .lookupReply(endpoints, this.federationServer.getName(), id);
        } catch (FederationException e) {
            e.printStackTrace();
        }
    }

    /**
     * The client to invoke is described within the message exhange. This
     * message exchange has been normally filled by a process which get a
     * service endpoint reference from the federation.
     * 
     * @param message
     * @param clientId
     * @return
     */
    private String getClientToInvoke(MessageExchange message, String clientId) {
        if (logger.isInfoEnabled()) {
            logger.info("Client ID : " + clientId);
            logger.info("Message = " + message);
        }
        String result = null;
        if ((message != null) && (message.getEndpoint() != null)) {
            // get the next peer from the domain location
            result = message.getEndpoint().getSubdomainLocation().substring(0,
                    message.getEndpoint().getSubdomainLocation().indexOf('/'));
        }
        return result;
    }
}
