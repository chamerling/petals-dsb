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
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.ow2.petals.binding.soap.listener.outgoing.PetalsServiceClient;
import org.ow2.petals.binding.soap.listener.outgoing.ServiceClientPoolObjectFactory;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Component;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;

/**
 * The SOAP component context. The context is filled by the SU listener (adding
 * modules, service descriptions...) and used by the listeners/workers.
 * 
 * @author Christophe HAMERLING (chamerling) - eBMWebSourcing
 * 
 */
public class SoapComponentContext {

    private static class ServiceClientKey {
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
                bRes = address.equals(((ServiceClientKey) obj).address)
                        && operation.equals(((ServiceClientKey) obj).operation)
                        && mep.equals(((ServiceClientKey) obj).mep);
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
            return address.hashCode() + operation.hashCode() + mep.hashCode();
        }
    }

    public static class ServiceManager<E> {
        private final Map<E, ServiceContext<E>> contexts;

        public ServiceManager() {
            this.contexts = new HashMap<E, ServiceContext<E>>();
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
         * 
         * @param consumes
         * @return
         */
        public ServiceContext<E> createServiceContext(final E e) {
            final ServiceContext<E> context = new ServiceContext<E>(e);
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
         * Get the modules
         * 
         * @param address
         * @return the modules of the given consumes block. Should at least
         *         contains the addressing module
         */
        public List<String> getModules(final E e) {
            final ServiceContext<E> ctx = getServiceContext(e);
            if (ctx != null) {
                return ctx.getModules();
            }
            return null;
        }

        /**
         * @param
         * @return
         */
        public ServiceContext<E> getServiceContext(final E e) {
            return this.contexts.get(e);
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

        /**
         * Add the service parameters to the specified Axis 2 service
         * 
         * @param e
         *            consume or provide
         * @param axisService
         *            the Axis 2 service
         * @throws XMLStreamException
         * @throws DeploymentException
         * @throws AxisFault
         */
        public void addServiceParameters(E e, AxisService axisService) throws XMLStreamException,
                DeploymentException, AxisFault {

            final OMElement parametersElements = getServiceParameters(e);

            if (parametersElements != null) {
                // get an iterator on all <parameter> children
                @SuppressWarnings("unchecked")
				final Iterator<OMElement> itr = parametersElements.getChildrenWithName(new QName(
                        DeploymentConstants.TAG_PARAMETER));

                // iterate on parameters and set them to the associated
                // axisService
                while (itr.hasNext()) {
                    final OMElement parameterElement = itr.next();

                    if (DeploymentConstants.TAG_PARAMETER.equalsIgnoreCase(parameterElement
                            .getLocalName())) {
                        axisService.addParameter(getParameter(parameterElement));
                    }
                }
            }
        }

        /**
         * Get the parameters for the given serviceAddress.
         * 
         * @param serviceAddress
         *            the service address
         * @return a string containing the parameters of the given
         *         serviceAddress.
         * @throws XMLStreamException
         */
        private OMElement getServiceParameters(final E e) throws XMLStreamException {
            OMElement serviceParameters = null;

            final ServiceContext<E> ctx = this.getServiceContext(e);

            if (ctx != null) {
                String serviceParams = ctx.getServiceParams();

                if (serviceParams != null) {
                    serviceParameters = buildParametersOM(serviceParams);
                }
            }

            return serviceParameters;
        }

        /**
         * Creates the OMElement corresponding to the parameters String,
         * included in parameters tags.
         * 
         * @param parameters
         *            the parameters
         * @return Returns the service parameters node
         * @throws XMLStreamException
         */
        private static final OMElement buildParametersOM(String parameters)
                throws XMLStreamException {
            OMElement element = null;
            if (parameters != null) {
                parameters = "<parameters>" + parameters + "</parameters>";
                element = (OMElement) XMLUtils.toOM(new StringReader(parameters));
                if (element != null) {
                    element.build();
                }
            }
            return element;
        }

        /**
         * Process the parameterElement object from the OM, and returns the
         * corresponding Parameter.
         * 
         * @param parameterElement
         *            <code>OMElement</code>
         * @return the Parameter parsed
         * @throws DeploymentException
         *             if bad paramName
         */
        private static Parameter getParameter(final OMElement parameterElement)
                throws DeploymentException {
            final Parameter parameter = new Parameter();

            // setting parameterElement
            parameter.setParameterElement(parameterElement);

            // setting parameter Name
            final OMAttribute paramName = parameterElement.getAttribute(new QName(
                    DeploymentConstants.ATTRIBUTE_NAME));

            if (paramName == null) {
                throw new DeploymentException(Messages.getMessage(
                        DeploymentErrorMsgs.BAD_PARAMETER_ARGUMENT, parameterElement.toString()));
            }
            parameter.setName(paramName.getAttributeValue());

            // setting parameter Value (the child element of the parameter)
            final OMElement paramValue = parameterElement.getFirstElement();
            if (paramValue != null) {
                parameter.setValue(paramValue);
                parameter.setParameterType(Parameter.OM_PARAMETER);
            } else {
                final String paratextValue = parameterElement.getText();
                parameter.setValue(paratextValue);
                parameter.setParameterType(Parameter.TEXT_PARAMETER);
            }

            return parameter;
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
     * A map used to link the provides instance to the pools which use it
     */
    private final Map<Provides, Set<ServiceClientKey>> providesServiceClientPools;

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
    	assert componentConfiguration != null;
    	assert componentConfiguration.getProcessorPoolSize() != null;
    	assert componentConfiguration.getAcceptorPoolSize() != null;
    	
        this.logger = logger;
        this.componentConfiguration = componentConfiguration;
        jbiDescriptors = new HashMap<String, Jbi>();
        servicesDescriptors = new HashMap<String, File>();

        // managers
        consumersManager = new ServiceManager<Consumes>();
        providersManager = new ServiceManager<Provides>();

        // Service client pools creation
        serviceClientPools = new HashMap<ServiceClientKey, ObjectPool>();
        providesServiceClientPools = new HashMap<Provides, Set<ServiceClientKey>>();
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
        jbiDescriptors.put(serviceUnitName, jbiDescriptor);
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
        servicesDescriptors.put(serviceUnitName, serviceDescriptor);
    }

    /**
     * <p>
     * Get a service client associated to an axis service set with the good
     * operation. It is taken from a pool object.
     * </p>
     * <p>
     * <b>This service client must be returned to the pool after usage using
     * API:
     * <code>{@link #returnServiceClient(String, QName, URI, PetalsServiceClient)}</code>
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
     * @return a ServiceClient. Not null. Must be returned to the pool after
     *         usage using API:
     *         <code>{@link #returnServiceClient(String, QName, URI, PetalsServiceClient)}</code>
     * @throws HandlingException
     */
    public PetalsServiceClient borrowServiceClient(final String address, final QName operation,
            final String soapAction, final URI mep, final ConfigurationExtensions cdkExtensions,
            final Provides provides) throws MessagingException {    	
        try {
            String resolvedOp;
            if (operation != null) {
                resolvedOp = operation.toString();
            } else if (soapAction != null) {
                resolvedOp = soapAction;
            } else {
                throw new MessagingException(
                        "Unable to resolve the operation. Set it in the Jbi exchange or SoapAction.");
            }

            final ServiceClientKey key = new ServiceClientKey(address, resolvedOp, mep);
            ObjectPool pool = serviceClientPools.get(key);
            if (pool == null) {

                long maxWait;
                Long timeout = null;
                if (provides != null) {
                	timeout = provides.getTimeout();
                }
                if((MEPConstants.IN_OUT_PATTERN.equals(mep)  || MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep))
                		&& timeout != null) {
                	maxWait = timeout;
                } else {
                	maxWait = 300000l;
                }

                // TODO: The pool max size should be limited by the JBI worker
                // number
				pool = new GenericObjectPool(
                // object factory
                        new ServiceClientPoolObjectFactory(address, operation, mep, cdkExtensions,
                                this, provides, logger, soapAction),

                        // max number of borrowed object sized to the number of
                        // JBI message processors
                        componentConfiguration.getProcessorPoolSize().getValue().intValue(),

                        // getting an object blocks until a new or idle object
                        // is available
                        GenericObjectPool.WHEN_EXHAUSTED_BLOCK,

                        // if getting an object is blocked for at most this
                        // delay, a NoSuchElementException will be thrown. In
                        // case of a synchronous call the delay is sized to the
                        // value of the SU's parameter "timeout",
                        // otherwise it sized to 5 minutes.
                        maxWait,

                        // max number of idle object in the pool. Sized to the
                        // number of JBI acceptors.
                        componentConfiguration.getAcceptorPoolSize().getValue().intValue(),

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

                synchronized (serviceClientPools) {
                    serviceClientPools.put(key, pool);
                    Set<ServiceClientKey> serviceClientKeysSet;
                    if (providesServiceClientPools.containsKey(provides)) {
                        serviceClientKeysSet = providesServiceClientPools.get(provides);
                    } else {
                        serviceClientKeysSet = new HashSet<ServiceClientKey>();
                    }
                    serviceClientKeysSet.add(key);
                    providesServiceClientPools.put(provides, serviceClientKeysSet);
                }
            }

            return (PetalsServiceClient) pool.borrowObject();

        } catch (final Exception e) {
            throw new MessagingException("Cannot create or get an Axis service client from the pool", e);
        }
    }

    /**
     * @return the axis2ConfigurationContext
     */
    public ConfigurationContext getAxis2ConfigurationContext() {
        return axis2ConfigurationContext;
    }

    /**
     * @return the consumersManager
     */
    public ServiceManager<Consumes> getConsumersManager() {
        return consumersManager;
    }

    /**
     * Get the JBI descriptor for the given address.
     * 
     * @param address
     * @return the {@link JBIDescriptor} if found, else return null
     */
    public Jbi getJbiDescriptor(final String serviceUnitName) {
        return jbiDescriptors.get(serviceUnitName);
    }

    /**
     * The connection factory JNDI name used by the JMS transport layer.
     * 
     * @return The connection factory JNDI name used by the JMS transport layer.
     */
    public String getJmsConnectionFactoryName() {
        return jmsConnectionFactoryName;
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
     * The JNDI provider URL used by the JMS transport layer.
     * 
     * @return The JNDI provider URL used by the JMS transport layer.
     */
    public String getJmsJndiProviderUrl() {
        return jmsJndiProviderUrl;
    }

    /**
     * @return the providersManager
     */
    public ServiceManager<Provides> getProvidersManager() {
        return providersManager;
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
        return servicesDescriptors.get(serviceUnitName);
    }

    /**
     * Remove the {@link JBIDescriptor} for the given address
     * 
     * @param address
     */
    public void removeJbiDescriptor(final String serviceUnitName) {
        if (jbiDescriptors != null) {
            jbiDescriptors.remove(serviceUnitName);
        }
    }

    /**
     * Remove the {@link JBIDescriptor} for the given service unit
     * 
     * @param address
     */
    public void removeServiceDescriptor(final String serviceUnitName) {
        servicesDescriptors.remove(serviceUnitName);
    }

    /**
     * Delete all the service client pools which used the provides instance
     * 
     * @param provides
     */
    public void deleteServiceClientPools(final Provides provides) {
        synchronized (serviceClientPools) {
            Set<ServiceClientKey> serviceClientKeysSet = providesServiceClientPools
                    .remove(provides);
            // if at least a SOAP request has been done for the SU provide
            if (serviceClientKeysSet != null) {
                for (ServiceClientKey key : serviceClientKeysSet) {
                    serviceClientPools.remove(key);
                }
            }
        }
    }

    /**
     * Release the service client to the pool
     * 
     * @param address
     * @param operation
     * @param mep
     * @param petalsServiceClient
     * @throws MessagingException
     */
    public void returnServiceClient(final String address, final QName operation, final URI mep,
            final PetalsServiceClient petalsServiceClient, final String soapAction)
            throws MessagingException {

        try {

            String resolvedOp = null;
            if (operation != null) {
                resolvedOp = operation.toString();
            } else if (soapAction != null) {
                resolvedOp = soapAction;
            } else {
                throw new MessagingException(
                        "Unable to resolve the operation. Set it in the Jbi exchange or SoapAction.");
            }

            final ObjectPool pool = serviceClientPools.get(new ServiceClientKey(address,
                    resolvedOp, mep));
            if (pool != null) {
                pool.returnObject(petalsServiceClient);
            }

        } catch (final Exception e) {
            throw new MessagingException("Can't return the Axis service client to the pool", e);
        }
    }

    /**
     * @param axis2ConfigurationContext
     *            the axis2ConfigurationContext to set
     */
    public void setAxis2ConfigurationContext(final ConfigurationContext axis2ConfigurationContext) {
        this.axis2ConfigurationContext = axis2ConfigurationContext;
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
     * Set the JNDI provider URL used by the JMS transport layer.
     * 
     * @param jmsJndiProviderUrl
     *            The JNDI provider URL used by the JMS transport layer.
     */
    public void setJmsJndiProviderUrl(final String jmsJndiProviderUrl) {
        this.jmsJndiProviderUrl = jmsJndiProviderUrl;
    }
}
