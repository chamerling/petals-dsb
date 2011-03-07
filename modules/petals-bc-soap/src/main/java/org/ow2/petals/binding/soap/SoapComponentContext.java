/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.ow2.petals.binding.soap.listener.outgoing.ServiceClient;
import org.ow2.petals.binding.soap.listener.outgoing.ServiceClientPoolObjectFactory;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Component;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.ws.notification.WsnManager;
import org.ow2.petals.ws.notification.WsnPersistance;
import org.ow2.petals.ws.notification.handlers.request.GetCurrentMessageHandler;
import org.ow2.petals.ws.notification.handlers.request.SubscribeHandler;
import org.ow2.petals.ws.notification.handlers.request.UnsubscribeHandler;

/**
 * The SOAP component context. The context is filled by the SU listener (adding
 * modules, service descriptions...) and used by the listeners/workers.
 * 
 * @author Christophe HAMERLING (chamerling) - eBMWebSourcing
 * 
 */
public class SoapComponentContext {

    private class ServiceClientKey {
        protected final String address;

        protected final String operation;

        protected final URI mep;

        /**
         * Creates a new instance of ServiceClientKey
         * 
         * @param address
         * @param operation
         * @param mep
         */
        public ServiceClientKey(final String address, final String operation, final URI mep) {
            this.address = address;
            this.operation = operation;
            this.mep = mep;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            final boolean bRes;

            if (obj instanceof ServiceClientKey) {
                bRes = this.address.equals(((ServiceClientKey) obj).address)
                        && this.operation.equals(((ServiceClientKey) obj).operation)
                        && this.mep.equals(((ServiceClientKey) obj).mep);
            } else {
                bRes = false;
            }

            return bRes;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.address.hashCode() + this.operation.hashCode() + this.mep.hashCode();
        }
    }

    public class ServiceManager<E> {
        private final Map<E, ServiceContext<E>> contexts;

        public ServiceManager() {
            this.contexts = new HashMap<E, ServiceContext<E>>();
        }

        /**
         * 
         * @param consumes
         * @return
         */
        public ServiceContext<E> createServiceContext(final E e) {
            ServiceContext<E> context = new ServiceContext<E>(e);
            this.contexts.put(e, context);
            return context;
        }

        /**
         * Delete the service context
         * 
         * @param consumes
         */
        public ServiceContext<E> deleteServiceContext(final E e) {
            return this.contexts.remove(e);
        }

        /**
         * 
         * @param e
         * @param modulesList
         */
        public void addModules(final E e, final List<String> modulesList) {
            if (e == null) {
                throw new IllegalArgumentException("Block can not be null");
            }
            ServiceContext<E> serviceContext = getServiceContext(e);
            if (serviceContext == null) {
                serviceContext = createServiceContext(e);
            }
            serviceContext.setModules(modulesList);
        }

        /**
         * @param
         * @return
         */
        public ServiceContext<E> getServiceContext(final E e) {
            return this.contexts.get(e);
        }

        /**
         * Get the modules
         * 
         * @param address
         * @return the modules of the given consumes block. Should at least
         *         contains the addressing module
         */
        public List<String> getModules(final E e) {
            ServiceContext<E> ctx = getServiceContext(e);
            if (ctx != null) {
                return ctx.getModules();
            }
            return null;
        }

        /**
         * Set parameters to a serviceAddress
         * 
         * @param serviceAddress
         *            the service address
         * @param parameters
         *            the parameters to set
         */
        public void setServicePamameters(final E e, final String parameters) {
            if (e == null) {
                throw new IllegalArgumentException("Element could not be null");
            }
            ServiceContext<E> ctx = this.getServiceContext(e);
            if (ctx == null) {
                ctx = createServiceContext(e);
            }
            ctx.setServiceParams(parameters);
        }

        /**
         * Get the policy path
         * 
         * @param serviceAddress
         * @return
         */
        public File getPolicyPath(final E e) {
            ServiceContext<E> serviceContext = this.getServiceContext(e);
            if (serviceContext != null) {
                return serviceContext.getPolicyPath();
            }
            return null;
        }

        /**
         * Set the policy path
         * 
         * @param serviceAddress
         * @param absolutePath
         */
        public void setPolicyPath(final E e, final File absolutePath) {
            ServiceContext<E> ctx = this.getServiceContext(e);
            if (ctx == null) {
                ctx = createServiceContext(e);
            }
            ctx.setPolicyPath(absolutePath);
        }

        /**
         * Get the parameters for the given serviceAddress.
         * 
         * @param serviceAddress
         *            the service address
         * @return a string containing the parameters of the given
         *         serviceAddress.
         */
        public String getServiceParameters(final E e) {
            ServiceContext<E> ctx = this.getServiceContext(e);
            if (ctx != null) {
                return ctx.getServiceParams();
            }
            return null;
        }

        /**
         * Set the class loader
         * 
         * @param serviceAddress
         * @param classLoader
         */
        public void setClassLoader(final E e, final ClassLoader classLoader) {
            ServiceContext<E> ctx = this.getServiceContext(e);
            if (ctx == null) {
                ctx = createServiceContext(e);
            }
            ctx.setClassloader(classLoader);
        }
    }

    private final ServiceManager<Consumes> consumersManager;

    private final ServiceManager<Provides> providersManager;

    private ConfigurationContext axis2ConfigurationContext;

    /**
     * The map of JBI descriptors. Key is the service unit name.
     */
    private final Map<String, Jbi> jbiDescriptors;

    /**
     * The map of services.xml files. Key is the service unit name.
     */
    private final Map<String, File> servicesDescriptors;

    /**
     * The pools of service clients used to call external web services. Key is a
     * ServiceClientKey object, value is a pool of service clientstring
     * containing parameters.
     */
    private final Map<ServiceClientKey, ObjectPool> serviceClientPools;

    /**
     * The web service notification manager
     */
    protected WsnManager wsnManager;

    /**
     * The component configuration information
     */
    private Component componentConfiguration;

    /**
     * The JNDI initial factory used by the JMS transport layer.
     */
    private String jmsJndiInitialFactory;

    /**
     * The JNDI provider URL used by the JMS transport layer.
     */
    private String jmsJndiProviderUrl;

    /**
     * The connection factory JNDI name used by the JMS transport layer.
     */
    private String jmsConnectionFactoryName;

    /**
     * The logger
     */
    private final Logger logger;

    /**
     * Creates a new instance of SoapComponentContext
     * 
     * @param context
     * @param componentConfiguration
     * @param logger
     */
    public SoapComponentContext(final ComponentContext context,
            final Component componentConfiguration, final Logger logger) {
        this.logger = logger;
        this.componentConfiguration = componentConfiguration;
        this.jbiDescriptors = new HashMap<String, Jbi>();
        this.servicesDescriptors = new HashMap<String, File>();

        // managers
        this.consumersManager = new ServiceManager<Consumes>();
        this.providersManager = new ServiceManager<Provides>();

        // Service client pools creation
        this.serviceClientPools = new ConcurrentHashMap<ServiceClientKey, ObjectPool>();

        this.wsnManager = new WsnManager(this.logger);
        this.wsnManager.setPersistance(new WsnPersistance(new File(context.getInstallRoot(),
                "topics")));

        // add handlers to manager
        final SubscribeHandler subHandler = new SubscribeHandler();
        this.wsnManager.addHandler(subHandler);
        final GetCurrentMessageHandler currentHandler = new GetCurrentMessageHandler();
        this.wsnManager.addHandler(currentHandler);
        final UnsubscribeHandler unsubHandler = new UnsubscribeHandler();
        this.wsnManager.addHandler(unsubHandler);
    }

    /**
     * Add a {@link JBIDescriptor}
     * 
     * @param address
     * @param jbiDescriptor
     */
    public void addJbiDescriptor(final String serviceUnitName, final Jbi jbiDescriptor) {
        if (serviceUnitName == null) {
            throw new IllegalArgumentException("Service unit name could not be null");
        }
        this.jbiDescriptors.put(serviceUnitName, jbiDescriptor);
    }

    /**
     * Get the JBI descriptor for the given address.
     * 
     * @param address
     * @return the {@link JBIDescriptor} if found, else return null
     */
    public Jbi getJbiDescriptor(final String serviceUnitName) {
        return this.jbiDescriptors.get(serviceUnitName);
    }

    /**
     * Remove the {@link JBIDescriptor} for the given address
     * 
     * @param address
     */
    public void removeJbiDescriptor(final String serviceUnitName) {
        if (this.jbiDescriptors != null) {
            this.jbiDescriptors.remove(serviceUnitName);
        }
    }

    /**
     * Add a {@link JBIDescriptor}
     * 
     * @param address
     * @param jbiDescriptor
     */
    public void addServiceDescriptor(final String serviceUnitName, final File serviceDescriptor) {
        if (serviceUnitName == null) {
            throw new IllegalArgumentException("Service unit name could not be null");
        }
        this.servicesDescriptors.put(serviceUnitName, serviceDescriptor);
    }

    /**
     * Get the service descriptor as {@link File} of the given service unit if
     * available.
     * 
     * @param address
     * @return the file (services.xml) or null if no service descriptor is
     *         available
     */
    public File getServiceDescriptor(final String serviceUnitName) {
        return this.servicesDescriptors.get(serviceUnitName);
    }

    /**
     * Remove the {@link JBIDescriptor} for the given service unit
     * 
     * @param address
     */
    public void removeServiceDescriptor(final String serviceUnitName) {
        this.servicesDescriptors.remove(serviceUnitName);
    }

    /**
     * @return the notificationManager
     */
    public WsnManager getWsnManager() {
        return this.wsnManager;
    }

    /**
     * @param wsnManager
     *            the notificationManager to set
     */
    public void setWsnManager(final WsnManager wsnManager) {
        this.wsnManager = wsnManager;
    }

    /**
     * 
     * @return
     */
    public Component getComponentConfiguration() {
        return this.componentConfiguration;
    }

    /**
     * 
     * @param componentConfiguration
     */
    public void setComponentConfiguration(final Component componentConfiguration) {
        this.componentConfiguration = componentConfiguration;
    }

    /**
     * <p>
     * Get a service client associated to an axis service set with the good
     * operation. It is taken from a pool object.
     * </p>
     * <p>
     * <b>This service client must be returned to the pool after usage using
     * API:
     * <code>{@link #returnServiceClient(String, QName, URI, ServiceClient)}</code>
     * .</b>
     * </p>
     * 
     * @param address
     *            the address of the service, mainly used as key to retrieve the
     *            associated SU.
     * @param operation
     *            the target operation QName. Non null
     * @param mep
     *            the message exchange pattern used. Non null
     * @param cdkExtensions
     *            SU extensions used by the service client pool when the
     *            creation of a service client is needed
     * @param provides
     *            the provides block of the endpoint which is creating the
     *            external WS call
     * @param ramprtUserName 
     * @return a ServiceClient. Not null. Must be returned to the pool after
     *         usage using API:
     *         <code>{@link #returnServiceClient(String, QName, URI, ServiceClient)}</code>
     * @throws HandlingException
     */
    public ServiceClient borrowServiceClient(final String address, final QName operation,
            final String soapAction, final URI mep, final ConfigurationExtensions cdkExtensions,
            final Provides provides, String rampartUserName) throws MessagingException {

        try {
            
            String resolvedOp;
            if (operation != null ){
                resolvedOp = operation.toString();
            }else if (soapAction != null){
                resolvedOp = soapAction;
            }else {
                throw new MessagingException("Unable to resolve the operation. Set it in the Jbi exchange or SoapAction.");
            }
            
            
            final ServiceClientKey key = new ServiceClientKey(address, resolvedOp, mep);
            ObjectPool pool = this.serviceClientPools.get(key);
            if (pool == null) {
                // TODO: The pool max size should be limited by the JBI worker
                // number
                pool = new GenericObjectPool(
                // object factory
                        new ServiceClientPoolObjectFactory(address, operation, mep, cdkExtensions,
                                this, provides, this.logger, soapAction, rampartUserName),

                        // max number of borrowed object sized to the number of
                        // JBI message processors
                        this.componentConfiguration.getProcessorPoolSize().getValue(),

                        // getting an object blocks until a new or idle object
                        // is available
                        GenericObjectPool.WHEN_EXHAUSTED_BLOCK,

                        // if getting an object is blocked for at most this
                        // delay, a NoSuchElementException will be thrown. In
                        // case of a synchronous call the delay is sized to the
                        // value of the SU's parameter "synchronous-timeout",
                        // otherwise it sized to 5 minutes.
                        MEPConstants.IN_OUT_PATTERN.equals(mep)
                                || MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep) ? SUPropertiesHelper
                                .retrieveTimeout(cdkExtensions)
                                : 300000l,

                        // max number of idle object in the pool. Sized to the
                        // number of JBI acceptors.
                        this.componentConfiguration.getAcceptorPoolSize().getValue(),

                        // min number of idle object in the pool. Sized to 0
                        // (ie when no activity no object in pool)
                        GenericObjectPool.DEFAULT_MIN_IDLE,

                        // no validation test of the borrowed object
                        false,

                        // no validation test of the returned object
                        false,

                        // how long the eviction thread should sleep before
                        // "runs" of examining idle objects. Sized to 5min.
                        300000l,

                        // the number of objects examined in each run of the
                        // idle object evictor. Size to the default value (ie.
                        // 3)
                        GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN,

                        // the minimum amount of time that an object may sit
                        // idle in the pool before it is eligible for eviction
                        // due to idle time. Sized to 30min
                        GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS,

                        // no validation test of the idle object
                        false,

                        // the minimum amount of time an object may sit idle in
                        // the pool before it is eligible for eviction by the
                        // idle object evictor (if any), with the extra
                        // condition that at least "minIdle" amount of object
                        // remain in the pool.
                        GenericObjectPool.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS,

                        // the pool returns idle objects in last-in-first-out
                        // order
                        true);

                this.serviceClientPools.put(key, pool);
            }

            return (ServiceClient) pool.borrowObject();

        } catch (final Exception e) {
            throw new MessagingException("Can't create get an Axis service client from the pool", e);
        }
    }

    /**
     * Release the service client to the pool
     * 
     * @param address
     * @param operation
     * @param mep
     * @param serviceClient
     * @throws MessagingException
     */
    public void returnServiceClient(final String address, final QName operation, final URI mep,
            final ServiceClient serviceClient, final String soapAction) throws MessagingException {

        try {
            
            String resolvedOp = null;
            if (operation != null){
                resolvedOp = operation.toString();
            }else if (soapAction != null){
                resolvedOp = soapAction;
            }else {
                throw new MessagingException("Unable to resolve the operation. Set it in the Jbi exchange or SoapAction.");
            }
            
            ObjectPool pool = this.serviceClientPools.get(new ServiceClientKey(address, resolvedOp,
                    mep));
            if (pool != null) {
                pool.returnObject(serviceClient);
            }

        } catch (final Exception e) {
            throw new MessagingException("Can't return the Axis service client to the pool", e);
        }
    }

    /**
     * @return the axis2ConfigurationContext
     */
    public ConfigurationContext getAxis2ConfigurationContext() {
        return axis2ConfigurationContext;
    }

    /**
     * @param axis2ConfigurationContext
     *            the axis2ConfigurationContext to set
     */
    public void setAxis2ConfigurationContext(ConfigurationContext axis2ConfigurationContext) {
        this.axis2ConfigurationContext = axis2ConfigurationContext;
    }

    /**
     * @return the consumersManager
     */
    public ServiceManager<Consumes> getConsumersManager() {
        return consumersManager;
    }

    /**
     * @return the providersManager
     */
    public ServiceManager<Provides> getProvidersManager() {
        return providersManager;
    }

    /**
     * The JNDI initial factory used by the JMS transport layer.
     * 
     * @return The JNDI initial factory used by the JMS transport layer.
     */
    public String getJmsJndiInitialFactory() {
        return jmsJndiInitialFactory;
    }

    /**
     * Set the JNDI initial factory used by the JMS transport layer.
     * 
     * @param jmsJndiInitialFactory
     *            The JNDI initial factory used by the JMS transport layer.
     */
    public void setJmsJndiInitialFactory(final String jmsJndiInitialFactory) {
        this.jmsJndiInitialFactory = jmsJndiInitialFactory;
    }

    /**
     * The JNDI provider URL used by the JMS transport layer.
     * 
     * @return The JNDI provider URL used by the JMS transport layer.
     */
    public String getJmsJndiProviderUrl() {
        return this.jmsJndiProviderUrl;
    }

    /**
     * Set the JNDI provider URL used by the JMS transport layer.
     * 
     * @param jmsJndiProviderUrl
     *            The JNDI provider URL used by the JMS transport layer.
     */
    public void setJmsJndiProviderUrl(final String jmsJndiProviderUrl) {
        this.jmsJndiProviderUrl = jmsJndiProviderUrl;
    }

    /**
     * The connection factory JNDI name used by the JMS transport layer.
     * 
     * @return The connection factory JNDI name used by the JMS transport layer.
     */
    public String getJmsConnectionFactoryName() {
        return this.jmsConnectionFactoryName;
    }

    /**
     * Set the connection factory JNDI name used by the JMS transport layer.
     * 
     * @param jmsConnectionFactoryName
     *            The connection factory JNDI name used by the JMS transport
     *            layer.
     */
    public void setJmsConnectionFactoryName(final String jmsConnectionFactoryName) {
        this.jmsConnectionFactoryName = jmsConnectionFactoryName;
    }
}
