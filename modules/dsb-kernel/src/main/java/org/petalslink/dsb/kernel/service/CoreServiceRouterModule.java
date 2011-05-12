/**
 * 
 */
package org.petalslink.dsb.kernel.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jbi.messaging.MessageExchange.Role;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;

import org.apache.cxf.message.Message;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.io.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * In charge of being able to route messages to the right node and to receive
 * messages for kernel services invocation.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = SenderModule.class) })
public class CoreServiceRouterModule implements SenderModule {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "topology", signature = TopologyService.class)
    private TopologyService topologyService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(logger);
        this.log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.end();
    }

    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        // check if it is a kernel service invocation
        NormalizedMessage nm = exchange.getMessage("in");
        if (nm != null) {
            if (nm.getProperty(Constants.MESSAGE_TYPE) != null
                    && nm.getProperty(Constants.MESSAGE_TYPE).equals(Constants.DSB_INVOKE)) {

                if (Role.CONSUMER.equals(exchange.getRole())) {
                    List<ServiceEndpoint> endpoints = this
                            .resolve(sourceComponentContext, exchange);
                    for (ServiceEndpoint endpoint : endpoints) {
                        if (log.isDebugEnabled())
                            log.debug("Found an endpoint for kernel Service : " + endpoint);
                        electedEndpoints.put(endpoint,
                                new TransportSendContext(endpoint.getLocation()));
                    }
                } else {
                    // me...
                    // FIXME : remove, let the standard router module set this
                    // reply endpoint
                    // electedEndpoints.put(exchange.getConsumerEndpoint(), new
                    // TransportSendContext(
                    // exchange.getConsumerEndpoint().getLocation()));
                }
            } else {
                // This is not a kernel invoke
            }
        }
    }

    /**
     * Get a list of service endpoint to send the message to. These endpoints
     * are internal kernel endpoints...
     * 
     * @param sourceComponentContext
     * @param exchange
     * @return
     */
    public List<ServiceEndpoint> resolve(ComponentContext sourceComponentContext,
            MessageExchange exchange) {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        // get the targeted container and set the required data so that the
        // global router knows where to send message
        NormalizedMessage nm = exchange.getMessage("in");
        String endpoint = nm.getProperty(Message.ENDPOINT_ADDRESS) != null ? nm.getProperty(
                Message.ENDPOINT_ADDRESS).toString() : null;

        // Do not deal with the given endpoint but with properties. This is only
        // to keep the standard router module working, if we use the
        // echange.getEndpoint, the standard router will try to resolve it and
        // we do not want that

        final String serviceName = nm.getProperty(Constants.SERVICE_NAME).toString();
        final String endpointName = nm.getProperty(Constants.ENDPOINT_NAME).toString();
        final String interfaceName = nm.getProperty(Constants.ITF_NAME).toString();

        if (log.isDebugEnabled()) {
            log.debug("Found this in the message : ");
            log.debug("ENDOINT = " + endpointName);
            log.debug("SERVICE = " + serviceName);
            log.debug("INTERFACE = " + interfaceName);
        }
        
        String host = EndpointHelper.getInstance().getHost(endpoint);
        Location location = null;
        if (host != null && (location = getLocation(host)) != null) {
            final Location loc = location;

            ServiceEndpoint se = new ServiceEndpoint() {

                public void setType(EndpointType type) {

                }

                public EndpointType getType() {
                    return EndpointType.INTERNAL;
                }

                public Location getLocation() {
                    return loc;
                }

                public List<QName> getInterfacesName() {
                    return Arrays.asList(getInterfaces());
                }

                public Document getDescription() {
                    return null;
                }

                public QName getServiceName() {
                    return QName.valueOf(serviceName);
                }

                public QName[] getInterfaces() {
                    return new QName[] { QName.valueOf(interfaceName) };
                }

                public String getEndpointName() {
                    return QName.valueOf(endpointName).getLocalPart();
                }

                public DocumentFragment getAsReference(QName operationName) {
                    return null;
                }
            };
            result.add(se);
        }
        return result;
    }

    /**
     * Get the location from the container name. For now let's say that the
     * container name is unique and that we are able to deduce the location from
     * the topology file...
     * 
     * @param host
     * @return
     */
    private Location getLocation(String host) {
        // check that this host exists in the topology...
        ContainerConfiguration config = topologyService.getContainerConfiguration(host);
        if (config == null) {
            return null;
        }
        Location result = new Location();
        result.setComponentName(org.petalslink.dsb.kernel.service.Constants.KERNEL_SERVICE_COMPONENT);
        result.setContainerName(host);
        result.setSubdomainName(config.getSubdomainName());
        return result;
    }

}
