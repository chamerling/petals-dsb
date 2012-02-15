/**
 * 
 */
package org.petalslink.dsb.cloud.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.registry.api.Endpoint;
import org.ow2.petals.registry.api.Query;
import org.ow2.petals.registry.api.exception.RegistryException;
import org.ow2.petals.registry.client.api.RegistryClient;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * A registry proxy which just cache remote information things for a certain
 * amount of time... Local endpoints are stored in memory until they are garbage
 * collected...
 * 
 * @author chamerling
 * 
 */
public class CloudRegistryClient implements RegistryClient {

    private TopologyService topologyService;

    /**
     * Cache the registry client web service clients
     */
    Map<String, RegistryClient> clients;

    // local caches
    Multimap<String, Endpoint> byItf;

    Multimap<String, Endpoint> bySrvItf;

    Multimap<String, Endpoint> bySrvItfEp;

    // remote caches
    LoadingCache<String, List<Endpoint>> remoteItf;

    LoadingCache<String, List<Endpoint>> remoteSrvItf;

    LoadingCache<String, List<Endpoint>> remoteSrvItfEp;

    long ttl = 30l;

    /**
     * 
     */
    public CloudRegistryClient() {
        clients = Maps.newHashMap();
        this.byItf = ArrayListMultimap.create();
        this.bySrvItf = ArrayListMultimap.create();
        this.bySrvItfEp = ArrayListMultimap.create();

        remoteItf = CacheBuilder.newBuilder().expireAfterWrite(this.ttl, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Endpoint>>() {

                    @Override
                    public List<Endpoint> load(String key) throws Exception {
                        return null;
                        // use the clients to retrieve data from remotes...
                    }
                });

        remoteSrvItf = CacheBuilder.newBuilder().expireAfterWrite(this.ttl, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Endpoint>>() {

                    @Override
                    public List<Endpoint> load(String key) throws Exception {
                        return null;
                        // use the clients to retrieve data from remotes...
                    }
                });

        remoteSrvItfEp = CacheBuilder.newBuilder().expireAfterWrite(this.ttl, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Endpoint>>() {

                    @Override
                    public List<Endpoint> load(String key) throws Exception {
                        return null;
                        // use the clients to retrieve data from remotes...
                    }
                });
    }

    protected synchronized RegistryClient getClient(ContainerConfiguration configuration) {
        // TODO
        String key = "http://" + configuration.getHost() + ":7600/kernel/ws";
        if (clients.get(key) == null) {
            RegistryClient client = null;
            clients.put(key, client);
        }
        return clients.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.registry.client.api.RegistryClient#put(java.lang.String,
     * org.ow2.petals.registry.api.Endpoint, boolean)
     */
    public boolean put(String path, Endpoint endpoint, boolean propagate) throws RegistryException {
        // put only locally
        this.byItf.put(getItfKey(endpoint), endpoint);
        this.bySrvItf.put(getItfSrvKey(endpoint), endpoint);
        this.bySrvItfEp.put(getItfSrvEpKey(endpoint), endpoint);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.registry.client.api.RegistryClient#get(java.lang.String,
     * boolean)
     */
    public Endpoint get(String path, boolean searchRemote) throws RegistryException {
        // get local and merge with remotes

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.registry.client.api.RegistryClient#getAll(java.lang.String
     * , boolean)
     */
    public List<Endpoint> getAll(String path, boolean searchRemote) throws RegistryException {
        // get local and merge with remotes

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.registry.client.api.RegistryClient#delete(java.lang.String
     * , boolean)
     */
    public void delete(String path, boolean propagate) throws RegistryException {
        // delete local

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.registry.client.api.RegistryClient#lookup(org.ow2.petals
     * .registry.api.Query, boolean)
     */
    public List<Endpoint> lookup(Query query, boolean searchRemote) throws RegistryException {
        // get local and remotes

        return null;
    }

    String getItfKey(Endpoint endpoint) {
        return "ITF=" + endpoint.getInterface().toString();
    }

    String getItfSrvKey(Endpoint endpoint) {
        return getItfKey(endpoint) + "SRV=" + endpoint.getService().toString();
    }

    String getItfSrvEpKey(Endpoint endpoint) {
        return getItfSrvKey(endpoint) + "EP=" + endpoint.getName();
    }

    /**
     * @return the topologyService
     */
    public TopologyService getTopologyService() {
        return topologyService;
    }

    /**
     * @param topologyService
     *            the topologyService to set
     */
    public void setTopologyService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

}
