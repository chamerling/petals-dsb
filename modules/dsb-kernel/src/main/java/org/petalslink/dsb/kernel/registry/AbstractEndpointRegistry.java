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
package org.petalslink.dsb.kernel.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public abstract class AbstractEndpointRegistry implements EndpointRegistry, RegistryListenerManager {

    protected LoggingUtil log;

    /**
     * 
     */
    public AbstractEndpointRegistry() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> getAllEndpoints() {
        final List<Map<String, Object>> allEndpointsMap = this.getAllExternalEndpoints();
        allEndpointsMap.addAll(this.getAllInternalEndpoints());

        return allEndpointsMap;
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> getAllInternalEndpoints() {
        this.log.start();

        final List<Map<String, Object>> internalEndpointsMap = new ArrayList<Map<String, Object>>();
        List<ServiceEndpoint> internalEndpoints = null;
        try {
            internalEndpoints = this.getInternalEndpoints();
        } catch (RegistryException e) {
            this.log.warning(e.getMessage());
            return internalEndpointsMap;
        }

        for (final ServiceEndpoint serviceEndpoint : internalEndpoints) {
            final Map<String, Object> endpointMap = new HashMap<String, Object>();
            endpointMap.put(KEY_COMPONENT_NAME, serviceEndpoint.getLocation().getComponentName());
            endpointMap.put(KEY_CONTAINER_NAME, serviceEndpoint.getLocation().getContainerName());
            endpointMap.put(KEY_ENDPOINT_NAME, serviceEndpoint.getEndpointName());
            endpointMap.put(KEY_SERVICE_NAME, serviceEndpoint.getServiceName().toString());
            final List<String> interfaceNames = interfaceQNameToString(serviceEndpoint);
            endpointMap.put(KEY_INTERFACE_NAMES, interfaceNames.toArray(new String[interfaceNames
                    .size()]));
            /*
             * if (serviceEndpoint instanceof LinkedEndpoint) { final
             * LinkedEndpoint linkEP = (LinkedEndpoint) serviceEndpoint;
             * endpointMap.put(KEY_TYPE, linkEP.getToServiceName().toString() +
             * "@" + linkEP.getToEndpointName()); } else {
             */
            endpointMap.put(KEY_TYPE, TYPE_INTERNAL);
            // }
            internalEndpointsMap.add(endpointMap);
        }

        this.log.end();
        return internalEndpointsMap;
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> getAllExternalEndpoints() {
        this.log.start();

        final List<Map<String, Object>> externalEndpointsMap = new ArrayList<Map<String, Object>>();
        List<ServiceEndpoint> externalEndpoints;
        try {
            externalEndpoints = this.getExternalEndpoints();
        } catch (RegistryException e) {
            this.log.warning(e.getMessage());
            return externalEndpointsMap;
        }

        for (final ServiceEndpoint serviceEndpoint : externalEndpoints) {
            final Map<String, Object> endpointMap = new HashMap<String, Object>();
            endpointMap.put(KEY_COMPONENT_NAME, serviceEndpoint.getLocation().getComponentName());
            endpointMap.put(KEY_CONTAINER_NAME, serviceEndpoint.getLocation().getContainerName());
            endpointMap.put(KEY_ENDPOINT_NAME, serviceEndpoint.getEndpointName());
            endpointMap.put(KEY_SERVICE_NAME, serviceEndpoint.getServiceName());
            final List<String> interfaceNames = interfaceQNameToString(serviceEndpoint);
            endpointMap.put(KEY_INTERFACE_NAMES, interfaceNames.toArray(new String[interfaceNames
                    .size()]));
            endpointMap.put(KEY_TYPE, TYPE_EXTERNAL);
            externalEndpointsMap.add(endpointMap);
        }

        this.log.end();
        return externalEndpointsMap;
    }

    /**
     * Extract interface QName from the given endpoint.
     * 
     * @param abstractEndpoint
     * @return
     */
    protected static final List<String> interfaceQNameToString(ServiceEndpoint abstractEndpoint) {
        final QName[] interfaces = abstractEndpoint.getInterfaces();
        final List<String> interfaceNames = new ArrayList<String>();
        if ((interfaces != null) && (interfaces.length > 0)) {
            for (final QName interfaceName : interfaces) {
                interfaceNames.add(interfaceName.toString());
            }
        } else {
            interfaceNames.add(UNDEFINED);
        }
        return interfaceNames;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getEndpoints() {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        try {
            // FIXME : This means two distributed calls !!!!
            List<ServiceEndpoint> tmp = this.getInternalEndpoints();
            if (tmp != null) {
                result.addAll(tmp);
            }
            tmp = this.getExternalEndpoints();
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (RegistryException e) {
            this.log.warning(e.getMessage());
        }
        return result;
    }
}