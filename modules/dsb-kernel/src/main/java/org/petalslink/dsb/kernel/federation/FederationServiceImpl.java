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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.client.FederationService;
import org.petalslink.dsb.kernel.messaging.EndpointSearchEngine;
import org.petalslink.dsb.transport.api.Receiver;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;


/**
 * The implementation of the federation service which expose local service to
 * the federation ie this is the service which is called by the federation
 * server. This implementation binds all the required internal services. There
 * is NO need to modify it for new protocol implementation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = FederationService.class) })
public class FederationServiceImpl implements FederationService {

    /**
     * The message transporter receiver
     */
    @Requires(name = "receiver", signature = Receiver.class)
    private Receiver receiver;

    /**
     * Local endpoint search engine
     */
    @Requires(name = "endpointsearchengine", signature = EndpointSearchEngine.class)
    private EndpointSearchEngine endpointSearchEngine;

    /**
     * Filter lookup results with this filter
     */
    @Requires(name = "federationfilter", signature = FederationFilterService.class)
    private FederationFilterService federationFilterService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
    public void invoke(MessageExchange message) throws FederationException {
        this.log.info("Just received an invoke message from federation");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Message is " + message);
        }
        try {
            org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange = org.petalslink.dsb.transport.cxf.Adapter
                    .createJBIMessage(message);
            // send to the message receiver (ie the transporter)
            this.receiver.onMessage(exchange);
        } catch (MessagingException e) {
            this.log.warning("Error while transforming message", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<ServiceEndpoint> lookup(EndpointQuery query) throws FederationException {
        this.log.info("Just received a lookup message from federation");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Query is " + query);
        }
        Set<ServiceEndpoint> result = new HashSet<ServiceEndpoint>();

        try {
            if ((query.getEndpoint() != null) && (query.getInterface() != null)
                    && (query.getService() != null)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("getTargetedEndpointFromGivenEndpoint");
                }
                org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint ep = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenEndpoint(this.createJBIEndpoint(query), query
                                .getLinkType());
                if (ep != null) {
                    result.add(JBIFederationMessageAdapter.transform(ep));
                } else {
                    this.log.debug("SE returns null");
                }
            } else if (query.getInterface() != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("getTargetedEndpointFromGivenInterfaceName");
                }
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> eps = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenInterfaceName(query.getInterface(), query
                                .getStrategy(), query.getLinkType());
                if (eps != null) {
                    result.addAll(JBIFederationMessageAdapter.transform(eps));
                } else {
                    this.log.debug("SE returns null");
                }
            } else if (query.getService() != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("getTargetedEndpointFromGivenServiceName");
                }
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> eps = this.endpointSearchEngine
                        .getTargetedEndpointFromGivenServiceName(query.getService(), query
                                .getStrategy(), query.getLinkType());
                if (eps != null) {
                    result.addAll(JBIFederationMessageAdapter.transform(eps));
                } else {
                    this.log.debug("SE returns null");
                }
            } else {
                // This is temporary!
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> eps = this.endpointSearchEngine
                        .getAll();
                if (eps != null) {
                    result.addAll(JBIFederationMessageAdapter.transform(eps));
                } else {
                    this.log.debug("SE returns null");
                }
            }
        } catch (RoutingException e) {
            this.log.warning(e.getMessage());
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Search engine returns " + result.size() + " result(s) before filter");
        }

        Set<ServiceEndpoint> filtered = this.filterEndpoints(result);

        if (this.log.isDebugEnabled()) {
            this.log.debug("Search engine returns " + filtered.size() + " result(s) after filter");
        }
        return filtered;
    }

    /**
     * @param result
     * @return
     */
    private Set<ServiceEndpoint> filterEndpoints(Set<ServiceEndpoint> endpoints) {
        Set<ServiceEndpoint> result = new HashSet<ServiceEndpoint>();
        for (ServiceEndpoint serviceEndpoint : endpoints) {
            // TODO = define the federation name
            if (this.federationFilterService.isVisible("TODO", serviceEndpoint.getEndpointName())) {
                result.add(serviceEndpoint);
            }
        }
        return result;
    }

    /**
     * @param query
     * @return
     */
    private org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint createJBIEndpoint(
            final EndpointQuery query) {
        org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint ep = new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint() {

            public DocumentFragment getAsReference(QName operationName) {
                // TODO Auto-generated method stub
                return null;
            }

            public String getEndpointName() {
                return query.getEndpoint().toString();
            }

            public QName[] getInterfaces() {
                return new QName[] { query.getInterface() };
            }

            public QName getServiceName() {
                return query.getService();
            }

            public Document getDescription() {
                // TODO Auto-generated method stub
                return null;
            }

            public List<QName> getInterfacesName() {
                // TODO Auto-generated method stub
                return null;
            }

            public Location getLocation() {
                // TODO Auto-generated method stub
                return null;
            }

            public EndpointType getType() {
                return EndpointType.valueOf(query.getType());
            }

            public void setType(EndpointType type) {
                // TODO Auto-generated method stub

            }

        };
        return ep;
    }

}
