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
package org.ow2.petals.messaging.framework.servicebus.impl;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.servicebus.Service;
import org.ow2.petals.messaging.framework.servicebus.ServiceBus;
import org.ow2.petals.messaging.framework.servicebus.ServiceExposer;
import org.ow2.petals.messaging.framework.servicebus.TransportManager;
import org.ow2.petals.messaging.framework.servicebus.service.Registry;
import org.ow2.petals.messaging.framework.servicebus.service.Router;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DefaultServiceBus implements ServiceBus {

    public static void main(String[] args) {
        Engine engine = EngineFactory.getEngine();
        DefaultServiceBus bus = new DefaultServiceBus();
        bus.setEngine(engine);

        TransportManager transportManager = new TransporterManagerImpl();

        try {
            // add the components...
            Registry registry = new LocalRegistryImpl();
            engine.addComponent(Registry.class, registry);
            // TODO : expose component if needed...
            Router router = new RouterImpl();
            engine.addComponent(Router.class, router);
            engine.addComponent(TransportManager.class, transportManager);
        } catch (EngineException e) {
            e.printStackTrace();
        }

    }

    @Resource
    private Engine engine;

    /**
     * 
     */
    public DefaultServiceBus() {
    }

    /**
     * {@inheritDoc}
     */
    public STATE getState() {
        return this.engine.getState();
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws LifeCycleException {
        this.engine.init();

    }

    /**
     * {@inheritDoc}
     */
    public void start() throws LifeCycleException {
        this.engine.start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() throws LifeCycleException {
        this.engine.stop();
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public <T> void registerService(final java.lang.Class<T> clazz, final T service,
            java.net.URL serviceURL) {
        // register the component in the engine for future retrieval...
        try {
            this.engine.addComponent(clazz, service);
            if (serviceURL != null) {
                // expose the service or put it for future exposition when all
                // the services will be there...
                ServiceExposer exposer = this.engine.getComponent(ServiceExposer.class);
                if (exposer != null) {
                    exposer.expose(new Service() {
                        public Object getServiceInstance() {
                            return service;
                        }

                        public Class<?> getServiceClass() {
                            return clazz;
                        }

                        public QName getName() {
                            return QName.valueOf(clazz.getClass().getCanonicalName());
                        }
                    }, serviceURL);
                }
            }
        } catch (EngineException e) {
            e.printStackTrace();
        }

    }

}
