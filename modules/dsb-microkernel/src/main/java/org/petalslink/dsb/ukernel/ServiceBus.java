/**
 * 
 */
package org.petalslink.dsb.ukernel;

import java.util.List;

import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.kernel.api.node.Node;
import org.petalslink.dsb.kernel.api.node.NodeManager;
import org.petalslink.dsb.kernel.api.plugin.PluginManager;
import org.petalslink.dsb.kernel.api.registry.Registry;
import org.petalslink.dsb.kernel.api.router.Router;
import org.petalslink.dsb.transport.api.Context;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.Transporter;

/**
 * @author chamerling
 * 
 */
public class ServiceBus extends PluginManagerImpl {

    private Node me;

    /**
     * 
     */
    public ServiceBus(String name, String host, long port) {
        super();
        me = new Node();
        me.setHost(host);
        me.setName(name);
        me.setPort(port);
        try {
            this.addComponent(ServiceBus.class, this);
        } catch (DSBException e) {
        }
    }

    /**
     * 
     */
    @LifeCycleListener(phase = Phase.INIT)
    public void initBus() {
        System.out.println("Init bus");
        NodeManager nodeManager = getComponent(NodeManager.class);
        if (nodeManager != null) {
            try {
                nodeManager.add(me);
            } catch (DSBException e) {
                e.printStackTrace();
            }
        }
    }

    @LifeCycleListener(phase = Phase.STOP)
    public void stop() {

    }

    /**
     * @throws DSBException
     * 
     */
    public void invoke(ServiceEndpoint serviceEndpoint, MessageExchange messageExchange)
            throws DSBException {

        Router router = getComponent(Router.class);
        if (router == null) {
            throw new DSBException("Can not find a router");
        }
        List<ServiceEndpoint> endpoints = router.route(messageExchange);

        // depending on some policy, we choose a valid endpoint from the list
        // returned by the router...
        if (endpoints == null || endpoints.size() == 0) {
            throw new DSBException("Can not find any valid endpoint to send the message to...");
        }

        // for now let's choose the first one, create the context and assign to
        // the message exchange
        ServiceEndpoint endpoint = endpoints.get(0);

        Context context = new Context();
        context.componentName = endpoint.getComponentLocation();
        context.containerName = endpoint.getContainerLocation();
        context.subdomainName = endpoint.getSubdomainLocation();
        NodeManager nodeManager = getComponent(NodeManager.class);
        if (nodeManager == null) {
            throw new DSBException("No node manager found...");
        }
        Node node = nodeManager.get(endpoint.getContainerLocation());
        if (node == null) {
            throw new DSBException("Can not find a node to send the message to...");
        }
        context.port = node.getPort();
        context.hostName = node.getHost();
        messageExchange.setEndpoint(endpoint);

        Transporter transporter = getComponent(Transporter.class);
        if (transporter == null) {
            throw new DSBException("No transporter found...");
        }
        try {
            transporter.sendAsync(messageExchange, context, new Receiver() {

                public void onMessage(MessageExchange message) {
                    System.out.println("Got a response...");
                }
            });
        } catch (TransportException e) {
            e.printStackTrace();
        }
    }
}
