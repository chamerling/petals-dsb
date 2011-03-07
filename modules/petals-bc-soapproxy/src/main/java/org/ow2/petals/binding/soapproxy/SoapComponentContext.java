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

package org.ow2.petals.binding.soapproxy;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.ow2.petals.binding.soapproxy.listener.outgoing.ServiceClient;
import org.ow2.petals.binding.soapproxy.listener.outgoing.ServiceClientPoolObjectFactory;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.jbidescriptor.generated.Component;

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

    private ConfigurationContext axis2ConfigurationContext;

    /**
     * The pools of service clients used to call external web services. Key is a
     * ServiceClientKey object, value is a pool of service clientstring
     * containing parameters.
     */
    private final Map<ServiceClientKey, ObjectPool> serviceClientPools;

    /**
     * The component configuration information
     */
    private Component componentConfiguration;

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

        // Service client pools creation
        this.serviceClientPools = new ConcurrentHashMap<ServiceClientKey, ObjectPool>();
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
            final String soapAction, final URI mep) throws MessagingException {

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
            ObjectPool pool = this.serviceClientPools.get(key);
            if (pool == null) {
                // TODO: The pool max size should be limited by the JBI worker
                // number
                pool = new GenericObjectPool(
                // object factory
                        new ServiceClientPoolObjectFactory(address, operation, mep, this,
                                this.logger,
                                soapAction),

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
                                || MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep) ? 300000l
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
            if (operation != null) {
                resolvedOp = operation.toString();
            } else if (soapAction != null) {
                resolvedOp = soapAction;
            } else {
                throw new MessagingException(
                        "Unable to resolve the operation. Set it in the Jbi exchange or SoapAction.");
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
        return this.axis2ConfigurationContext;
    }

    /**
     * @param axis2ConfigurationContext
     *            the axis2ConfigurationContext to set
     */
    public void setAxis2ConfigurationContext(ConfigurationContext axis2ConfigurationContext) {
        this.axis2ConfigurationContext = axis2ConfigurationContext;
    }
}
