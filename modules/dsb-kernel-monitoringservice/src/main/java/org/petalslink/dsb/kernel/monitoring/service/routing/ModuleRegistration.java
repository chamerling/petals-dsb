/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service.routing;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Cardinality;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.kernel.messaging.router.RouterModuleManager;

/**
 * Hack while wating to detect modules in RouterModuleManager
 * 
 * @author chamerling
 * 
 */
@FractalComponent
public class ModuleRegistration {

    private static final String RECEIVERMODULE_FRACTAL_PREFIX = "receivermodule";

    private static final String SENDERMODULE_FRACTAL_PREFIX = "sendermodule";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "routermodulemanager", signature = RouterModuleManager.class)
    protected RouterModuleManager routerModuleManager;

    @Requires(name = RECEIVERMODULE_FRACTAL_PREFIX, signature = ReceiverModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> receivers = new Hashtable<String, Object>();

    @Requires(name = SENDERMODULE_FRACTAL_PREFIX, signature = SenderModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> senders = new Hashtable<String, Object>();

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener
    public void loadModules() {
        System.out.println("LOAD");
        if (log.isDebugEnabled()) {
            log.debug("Ack to add modules to manager");
        }
        for (String key : senders.keySet()) {
            Object o = senders.get(key);
            if (o != null && o instanceof SenderModule) {
                addModule(key, (SenderModule) o);
            }
        }
        for (String key : receivers.keySet()) {
            Object o = receivers.get(key);
            if (o != null && o instanceof ReceiverModule) {
                addModule(key, (ReceiverModule) o);
            }
        }
    }

    void addModule(final String name, final SenderModule sender) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding sender module %s to manager", name));
        }
        this.routerModuleManager.add(new org.petalslink.dsb.kernel.messaging.router.SenderModule() {
            public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
                    ComponentContext sourceComponentContext,
                    org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange)
                    throws RoutingException {
                sender.electEndpoints(electedEndpoints, sourceComponentContext, exchange);
            }

            public String getName() {
                return name;
            }

            public String getDescription() {
                return sender.getClass().getName();
            }
        });
    }

    void addModule(final String name, final ReceiverModule receiver) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding receiver module %s to manager", name));
        }
        this.routerModuleManager
                .add(new org.petalslink.dsb.kernel.messaging.router.ReceiverModule() {

                    public boolean receiveExchange(
                            org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange,
                            ComponentContext sourceComponentContext) throws RoutingException {
                        return receiver.receiveExchange(exchange, sourceComponentContext);
                    }

                    public String getName() {
                        return name;
                    }
                    
                    public String getDescription() {
                        return receiver.getClass().getName();
                    }
                });
    }

}
