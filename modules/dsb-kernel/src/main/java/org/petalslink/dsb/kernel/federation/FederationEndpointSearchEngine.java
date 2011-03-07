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
package org.petalslink.dsb.kernel.federation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.kernel.messaging.EndpointSearchEngine;


/**
 * The search engine which query the federation ie this is the federation facade
 * search engine; the implementation get a client to contact the federation and
 * sends lookup queries to the fedetation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = EndpointSearchEngine.class) })
public class FederationEndpointSearchEngine implements EndpointSearchEngine {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "federationengine", signature = FederationEngine.class)
    private FederationEngine federationEngine;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint getTargetedEndpointFromGivenEndpoint(ServiceEndpoint givenEndpoint,
            String linkType) throws RoutingException {
        EndpointQuery query = new EndpointQuery();
        query.setEndpoint(givenEndpoint.getEndpointName());
        if ((givenEndpoint.getInterfaces() != null) && (givenEndpoint.getInterfaces().length > 0)) {
            query.setInterface(givenEndpoint.getInterfaces()[0]);
        }
        query.setService(givenEndpoint.getServiceName());
        query.setType(givenEndpoint.getType().toString());
        // query.setStrategy(givenEndpoint.get);

        ServiceEndpoint result = null;
        Set<org.petalslink.dsb.api.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.federationEngine.getFederationClient().lookup(query);
        } catch (FederationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if ((endpoints != null) && (endpoints.size() > 0)) {
            result = JBIFederationMessageAdapter.transform(endpoints
                    .toArray(new org.petalslink.dsb.api.ServiceEndpoint[endpoints.size()])[0]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getTargetedEndpointFromGivenInterfaceName(
            QName givenInterfaceName, String strategy, String linkType) throws RoutingException {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        EndpointQuery query = new EndpointQuery();
        query.setInterface(givenInterfaceName);
        query.setStrategy(strategy);
        query.setLinkType(linkType);

        Set<org.petalslink.dsb.api.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.federationEngine.getFederationClient().lookup(query);
        } catch (FederationException e) {
            e.printStackTrace();
        }
        if (endpoints != null) {
            result.addAll(JBIFederationMessageAdapter.transform(endpoints));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getTargetedEndpointFromGivenServiceName(QName givenServiceName,
            String strategy, String linkType) throws RoutingException {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        EndpointQuery query = new EndpointQuery();
        query.setService(givenServiceName);
        query.setStrategy(strategy);
        query.setLinkType(linkType);

        Set<org.petalslink.dsb.api.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.federationEngine.getFederationClient().lookup(query);
        } catch (FederationException e) {
            e.printStackTrace();
        }
        if (endpoints != null) {
            result.addAll(JBIFederationMessageAdapter.transform(endpoints));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getAll() {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        EndpointQuery query = new EndpointQuery();

        Set<org.petalslink.dsb.api.ServiceEndpoint> endpoints = null;
        try {
            endpoints = this.federationEngine.getFederationClient().lookup(query);
        } catch (FederationException e) {
            e.printStackTrace();
        }
        if (endpoints != null) {
            result.addAll(JBIFederationMessageAdapter.transform(endpoints));
        }
        return result;
    }
}
