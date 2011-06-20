/**
 * 
 */
package org.petalslink.dsb.kernel.io.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.service.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * A Fractal & Petals {@link DSBServiceServerFactory} implementation.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = DSBServiceServerFactory.class) })
public class DSBServiceServerFactoryImpl implements DSBServiceServerFactory {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "router", signature = RouterService.class)
    private RouterService router;

    private Set<DSBServiceServer> servers;

    private Set<ComponentContext> contexts;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        log = new LoggingUtil(logger);
        log.start();
        this.servers = new HashSet<DSBServiceServer>();
        this.contexts = new HashSet<ComponentContext>();
        ServerFactoryRegistry.setFactory(this);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        log.end();
        // stop all the registered servers, ie the user does not have to deal
        // with that
        for (DSBServiceServer server : servers) {
            server.stop();
        }
        // let's remove the component contexts
        for (ComponentContext context : contexts) {
            try {
                this.router.removeComponent(context);
            } catch (RoutingException e) {
                e.printStackTrace();
            }
        }
    }

    public DSBServiceServer getServiceServer() {
        // TODO = Do not build once built... One instance per node!

        // create a fake context to attach to this service server...
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
                        return null;
                    }

                    public QName[] getInterfaces() {
                        return null;
                    }

                    public QName getServiceName() {
                        return null;
                    }

                    public EndpointType getType() {
                        return EndpointType.INTERNAL;
                    }

                    public void setType(EndpointType type) {

                    }

                    public List<QName> getInterfacesName() {
                        return null;
                    }

                    public Document getDescription() {
                        return null;
                    }

                    public Location getLocation() {
                        return new Location(configurationService.getContainerConfiguration()
                                .getSubdomainName(), configurationService
                                .getContainerConfiguration().getName(),
                                Constants.KERNEL_SERVICE_COMPONENT);
                    }

                    @Override
                    public String toString() {
                        return "SERVICE EP : " + getEndpointName() + " @ " + getLocation();
                    }
                };
            }
        };
        ComponentContext context = new FakeComponentContext(componentContextCommunication);
        DSBServiceServer server = new DSBServiceServerImpl(context);

        if (log.isDebugEnabled()) {
            log.debug("Kernel init for service and component " + context.getComponentName());
        }
        try {
            this.router.addComponent(context);
            this.contexts.add(context);
        } catch (RoutingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.servers.add(server);

        return server;
    }

    class FakeComponentContext extends ComponentContextImpl {

        public FakeComponentContext(ComponentContextCommunication componentContextCommunication) {
            super(componentContextCommunication);
        }

        @Override
        public String getComponentName() {
            return Constants.KERNEL_SERVICE_COMPONENT;
        }
    }

}
