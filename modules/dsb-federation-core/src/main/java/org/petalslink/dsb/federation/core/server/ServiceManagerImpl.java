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
package org.petalslink.dsb.federation.core.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.core.api.ServiceManager;

/**
 * The service manager manages service lifecycles based on their priority. These
 * services are needed by the federation server to work...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ServiceManagerImpl implements ServiceManager {

    private final Map<String, Service> services;

    private static Log logger = LogFactory.getLog(ServiceManagerImpl.class);

    /**
     * 
     */
    public ServiceManagerImpl() {
        this.services = new ConcurrentHashMap<String, Service>();
    }

    /**
     * {@inheritDoc}
     */
    public Service getService(String name) {
        return this.services.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public List<Service> getServices() {
        return new ArrayList<Service>(this.services.values());
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        // first start the internal services...
        if (logger.isDebugEnabled()) {
            logger.debug("Starting internal services");
        }
        PriorityQueue<Service> queue = new PriorityQueue<Service>(this.services.size(),
                new Comparator());
        for (String name : this.services.keySet()) {
            if ((this.services.get(name) != null)
                    && (this.services.get(name).getType() == Service.TYPE.INTERNAL)) {
                queue.add(this.services.get(name));
            }
        }
        Service s = null;
        while ((s = queue.poll()) != null) {
            this.start(s.getName());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Starting *bound services");
        }
        queue = new PriorityQueue<Service>(this.services.size(), new Comparator());
        for (String name : this.services.keySet()) {
            if ((this.services.get(name) != null)
                    && (this.services.get(name).getType() != Service.TYPE.INTERNAL)) {
                queue.add(this.services.get(name));
            }
        }
        while ((s = queue.poll()) != null) {
            this.start(s.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start(String serviceName) {
        Service s = this.services.get(serviceName);
        if (s != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Starting " + s.getType() + " service " + s.getName()
                        + " with priority " + s.getPriority());
            }
            s.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        for (String name : this.services.keySet()) {
            this.stop(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(String serviceName) {
        Service s = this.services.get(serviceName);
        if (s != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping service " + s.getName());
            }
            s.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addService(Service service) {
        // TODO : do it when the framework is not started

        if ((service != null) && (service.getName() != null)) {
            if (this.services.get(service.getName()) == null) {
                this.services.put(service.getName(), service);
            } else {
                // already in...
            }
        } else {

        }
    }

    class Comparator implements java.util.Comparator<Service> {

        public int compare(Service s1, Service s2) {
            int result = 0;
            if (s1.getPriority() < s2.getPriority()) {
                result = 1;
            } else if (s1.getPriority() == s2.getPriority()) {
                result = 0;
            } else {
                result = -1;
            }
            return result;
        }
    }

}
