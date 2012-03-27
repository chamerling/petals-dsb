/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.transport.util.TransportSendContext;

/**
 * @author chamerling
 * 
 */
public class RouterModuleManagerImpl implements RouterModuleManager {

    private Map<String, ManagedSenderModule> senders;

    private Map<String, ManagedReceiverModule> receivers;

    /**
     * 
     */
    public RouterModuleManagerImpl() {
        // to keep backward compatibility, modules are reverse alphabetically
        // ordered ie sender-02 have more priority than sender-01
        this.senders = new TreeMap<String, ManagedSenderModule>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        this.receivers = new TreeMap<String, ManagedReceiverModule>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.SenderModule)
     */
    public void add(SenderModule module) {
        if (module != null && module.getName() != null) {
            this.senders.put(module.getName(), new ManagedSenderModule(module));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.ReceiverModule)
     */
    public void add(ReceiverModule module) {
        if (module != null && module.getName() != null) {
            this.receivers.put(module.getName(), new ManagedReceiverModule(module));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenders
     * ()
     */
    public List<SenderModule> getSenders() {
        List<SenderModule> result = new ArrayList<SenderModule>();
        for (SenderModule senderModule : senders.values()) {
            result.add(senderModule);
        }
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#setSenderState
     * (java.lang.String, boolean)
     */
    public void setSenderState(String name, boolean onoff) {
        ManagedSenderModule module = this.senders.get(name);
        if (module != null) {
            module.state = onoff;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#
     * setReceiverState(java.lang.String, boolean)
     */
    public void setReceiverState(String name, boolean onoff) {
        ManagedReceiverModule module = this.receivers.get(name);
        if (module != null) {
            module.state = onoff;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#
     * getReceiverState(java.lang.String)
     */
    public boolean getReceiverState(String name) {
        ManagedReceiverModule module = this.receivers.get(name);
        if (module == null) {
            return false;
        }
        return module.state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenderState
     * (java.lang.String)
     */
    public boolean getSenderState(String name) {
        ManagedSenderModule module = this.senders.get(name);
        if (module == null) {
            return false;
        }
        return module.state;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getReceivers
     * ()
     */
    public List<ReceiverModule> getReceivers() {
        List<ReceiverModule> result = new ArrayList<ReceiverModule>();
        for (ReceiverModule module : receivers.values()) {
            result.add(module);
        }
        return result;
    }

    class ManagedSenderModule implements SenderModule {

        private SenderModule module;

        boolean state = true;

        /**
         * 
         */
        public ManagedSenderModule(SenderModule module) {
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.ow2.petals.jbi.messaging.routing.module.SenderModule#electEndpoints
         * (java.util.Map,
         * org.ow2.petals.jbi.component.context.ComponentContext,
         * org.ow2.petals.jbi.messaging.exchange.MessageExchange)
         */
        public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
                ComponentContext sourceComponentContext, MessageExchangeWrapper exchange)
                throws RoutingException {
            if (state) {
                this.module.electEndpoints(electedEndpoints, sourceComponentContext, exchange);
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.petalslink.dsb.kernel.messaging.router.SenderModule#getName()
         */
        public String getName() {
            return this.module.getName();
        }

        /* (non-Javadoc)
         * @see org.petalslink.dsb.kernel.messaging.router.SenderModule#getDescription()
         */
        public String getDescription() {
            return this.module.getDescription();
        }

    }

    class ManagedReceiverModule implements ReceiverModule {

        private ReceiverModule receiver;

        private boolean state;

        public ManagedReceiverModule(ReceiverModule receiver) {
            this.receiver = receiver;
            this.state = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ow2.petals.jbi.messaging.routing.module.ReceiverModule#
         * receiveExchange
         * (org.ow2.petals.jbi.messaging.exchange.MessageExchange,
         * org.ow2.petals.jbi.component.context.ComponentContext)
         */
        public boolean receiveExchange(MessageExchangeWrapper exchange,
                ComponentContext sourceComponentContext) throws RoutingException {
            if (this.state) {
                return this.receiver.receiveExchange(exchange, sourceComponentContext);
            }
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.petalslink.dsb.kernel.messaging.router.ReceiverModule#getName()
         */
        public String getName() {
            return this.receiver.getName();
        }
        
        /*
         * (non-Javadoc)
         * @see org.petalslink.dsb.kernel.messaging.router.ReceiverModule#getDescription()
         */
        public String getDescription() {
            return this.receiver.getDescription();
        }
    }
}
