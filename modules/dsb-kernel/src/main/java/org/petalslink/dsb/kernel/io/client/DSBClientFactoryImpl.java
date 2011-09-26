/**
 * 
 */
package org.petalslink.dsb.kernel.io.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.container.lifecycle.ComponentContextCommunication;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.component.context.ComponentContextImpl;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.jbi.messaging.endpoint.EndpointPropertiesService;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.kernel.api.service.ServiceEndpoint.EndpointType;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.service.ServiceEndpointImpl;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.jbi.JBISender;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.ClientFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * A client factory implementation which uses the DSB messaging mechanism to
 * send and receive messages. It initialize itself and store itself into the
 * {@link ClientFactoryRegistry}
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ClientFactory.class) })
public class DSBClientFactoryImpl implements ClientFactory {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private AtomicLong counter = new AtomicLong(0);

    private static final String COMPONENT_NAME_PREFIX = "internalclient-";

    /**
     * FIXME : be careful about asynchronous calls... Maybe it is better to
     * create a new client for each call even if it consumes memory and threads
     * at the router level...
     */
    private Map<String, JBISender> cache;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "router", signature = RouterService.class)
    private RouterService router;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        log = new LoggingUtil(logger);
        this.cache = new HashMap<String, JBISender>();
        ClientFactoryRegistry.setFactory(this);
        log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        log.end();
        this.cache = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.ClientFactory#getClient(org.petalslink
     * .dsb.api.ServiceEndpoint)
     */
    public Client getClient(ServiceEndpoint service) throws ClientException {
        if (log.isDebugEnabled()) {
            log.debug("Getting a client for service " + service);
        }

        // look for old client if any to save some resources
        Client client = getOldClient(service);
        if (client != null) {
            return client;
        }
        return createClient(service);
    }

    /**
     * @param service
     * @return
     * @throws ClientException
     */
    protected Client createClient(ServiceEndpoint service) throws ClientException {
        // create a fake component context with some information so that we can
        // get responses from invocations...

        final String componentName = getClientId(service);
        final ServiceEndpointImpl clientServiceEndpoint = new ServiceEndpointImpl();
        clientServiceEndpoint.setType(EndpointType.INTERNAL);
        ServiceEndpoint clientEndpoint = getServiceEndpointClient(service);
        if (clientEndpoint.getEndpointName() != null)
            clientServiceEndpoint.setEndpointName(clientEndpoint.getEndpointName());
        if (clientEndpoint.getInterfaces() != null)
            clientServiceEndpoint.setInterfacesName(Arrays.asList(clientEndpoint.getInterfaces()));
        if (clientEndpoint.getServiceName() != null)
            clientServiceEndpoint.setServiceName(clientEndpoint.getServiceName());
        clientServiceEndpoint.setLocation(getLocation(componentName));
        ComponentContextCommunication componentContextCommunication = new ComponentContextCommunication() {

            public String getWorkspaceRoot() {
                return null;
            }

            public TransactionManager getTransactionManagerService() {
                return null;
            }

            public RouterService getRouterService() {
                return router;
            }

            public Logger getLogger() {
                return logger;
            }

            public Jbi getJBIDescriptor() {
                return null;
            }

            public String getInstallationRoot() {
                return null;
            }

            public InitialContext getInitialContext() {
                return null;
            }

            public EndpointRegistry getEndpointRegistry() {
                return null;
            }

            public EndpointPropertiesService getEndpointPropertiesService() {
                return null;
            }

            public AdminService getAdminService() {
                return null;
            }

            public org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint getAddress() {
                return new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint() {

                    public DocumentFragment getAsReference(QName arg0) {
                        return null;
                    }

                    public String getEndpointName() {
                        return clientServiceEndpoint.getEndpointName();
                    }

                    public QName[] getInterfaces() {
                        return clientServiceEndpoint.getInterfacesName().toArray(
                                new QName[clientServiceEndpoint.getInterfacesName().size()]);
                    }

                    public QName getServiceName() {
                        return clientServiceEndpoint.getServiceName();
                    }

                    public EndpointType getType() {
                        return clientServiceEndpoint.getType();
                    }

                    public void setType(EndpointType type) {

                    }

                    public List<QName> getInterfacesName() {
                        return clientServiceEndpoint.getInterfacesName();
                    }

                    public Document getDescription() {
                        return null;
                    }

                    public Location getLocation() {
                        return clientServiceEndpoint.getLocation();
                    }

                    @Override
                    public String toString() {
                        return "CLIENT EP : " + getEndpointName() + " @ " + getLocation();
                    }
                };
            }
        };

        ComponentContext context = new FakeComponentContext(componentContextCommunication,
                componentName);
        JBISender jbiClient = new JBISender(context, service);

        // TODO
        // Need to call to create required listeners... Need to create a
        // specific component in the context to be able to receive messages from
        // the router (ie delivery channel receive message from router)
        // this.router.addComponent(this.componentContext);
        if (log.isDebugEnabled()) {
            log.debug("Initializing DSB kernel with the newly created client...");
        }
        try {
            this.router.addComponent(context);
        } catch (RoutingException e) {
            throw new ClientException("Can not create client", e);
        }

        // cache it...
        this.cache.put(componentName, jbiClient);

        return jbiClient;
    }

    /**
     * @param service
     * @return
     */
    private Client getOldClient(ServiceEndpoint service) {
        String id = getClientId(service);
        return this.cache.get(id);
    }

    /**
     * @param service
     * @return
     */
    private String getClientId(ServiceEndpoint service) {
        StringBuffer sb = new StringBuffer(COMPONENT_NAME_PREFIX);
        if (service != null) {
            if (service.getEndpointName() != null) {
                sb.append("ep=");
                sb.append(service.getEndpointName());
                sb.append(";");
            }

            if (service.getServiceName() != null) {
                sb.append("srv=");
                sb.append(service.getServiceName().toString());
                sb.append(";");
            }

            if (service.getInterfaces() != null && service.getInterfaces().length > 0) {
                sb.append("itf=");
                sb.append(service.getInterfaces()[0].toString());
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private Location getLocation(String componentName) {
        return new Location(configurationService.getContainerConfiguration().getSubdomainName(),
                configurationService.getContainerConfiguration().getName(), componentName);
    }

    public void release(Client client) throws ClientException {
        // get the client from the cache and retrieve its context...
        if (client == null) {
            return;
        }

        try {
            JBISender sender = cache.get(client.getName());
            if (sender != null) {
                this.router.removeComponent(sender.getComponentContext());
            }
        } catch (RoutingException e) {
            throw new ClientException(e);
        }
    }

    private ServiceEndpoint getServiceEndpointClient(ServiceEndpoint serviceEndpoint) {
        ServiceEndpoint result = new ServiceEndpoint();
        long id = counter.incrementAndGet();
        /*
         * result.setComponentLocation(COMPONENT_NAME_PREFIX + "" +
         * configurationService.getContainerConfiguration().getName());
         * result.setContainerLocation
         * (configurationService.getContainerConfiguration().getName());
         * result.setSubdomainLocation
         * (configurationService.getContainerConfiguration()
         * .getSubdomainName());
         */
        if (serviceEndpoint.getEndpointName() != null) {
            result.setEndpointName(serviceEndpoint.getEndpointName() + "-" + id);
        }

        if (serviceEndpoint.getServiceName() != null) {
            QName serviceName = new QName(serviceEndpoint.getServiceName().getNamespaceURI(),
                    serviceEndpoint.getServiceName().getLocalPart() + "-" + id);
            // result.setServiceName(serviceEndpoint.get);
            result.setServiceName(serviceName);
        }
        return result;
    }

    class FakeComponentContext extends ComponentContextImpl {

        String componentName;

        public FakeComponentContext(ComponentContextCommunication componentContextCommunication,
                String id) {
            super(componentContextCommunication);
            this.componentName = id;
        }

        @Override
        public String getComponentName() {
            return componentName;
        }
    }

}
