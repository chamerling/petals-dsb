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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.NullArgumentException;
import org.ow2.petals.jbi.descriptor.original.generated.LinkType;
import org.ow2.petals.jbi.messaging.endpoint.JBIServiceEndpointImpl;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.RegistryListener;
import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.kernel.api.service.ServiceEndpoint.EndpointType;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.registry.api.Endpoint;
import org.ow2.petals.registry.api.LocalRegistry;
import org.ow2.petals.registry.api.Query;
import org.ow2.petals.registry.api.exception.RegistryException;
import org.ow2.petals.registry.api.util.XMLUtil;
import org.ow2.petals.registry.client.api.RegistryClient;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public abstract class BaseEndpointRegistry extends AbstractEndpointRegistry {

    public static final String LISTENER_FRACTAL_PREFIX = "listener";

    /**
     * Default configuration is in the petals configuration file
     */
    protected static final String CONFIG = ConfigurationService.SERVER_PROPS_FILE;

    protected LocalRegistry registry;

    protected RegistryClient client;

    /**
     * 
     */
    public BaseEndpointRegistry() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint activateEndpoint(QName serviceName, String endpointName,
            ServiceEndpoint address) throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        return this.activateEndpoint(serviceName, endpointName, null, null, address, null);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint activateEndpoint(QName serviceName, String endpointName,
            QName[] interfaces, Document description, ServiceEndpoint address,
            Map<String, String> properties)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        final JBIServiceEndpointImpl endpoint = new JBIServiceEndpointImpl();
        endpoint.setServiceName(serviceName);
        endpoint.setEndpointName(endpointName);
        endpoint.setInterfacesName(Arrays.asList(interfaces));
        endpoint.setLocation(address.getLocation());
        endpoint.setDescription(description);
        endpoint.setProperties(properties);

        this.registerEndpoint(endpoint);

        if (this.getListeners() != null) {
            for (RegistryListener listener : this.getListeners()) {
                listener.onRegister(endpoint);
            }
        }
        return endpoint;
    }

    protected void registerEndpoint(
            org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (serviceEndpoint == null) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Can not register a null service endpoint");
        }

        // TODO: check if the service is already registered

        org.ow2.petals.registry.api.Endpoint endpoint = new org.ow2.petals.registry.api.Endpoint();
        if (serviceEndpoint.getDescription() != null) {
            // TODO = Use a common library!
            try {
                // let's say that the description is an XML one!
                endpoint.setDescription(XMLUtil.createStringFromDOMDocument(serviceEndpoint
                        .getDescription()));
            } catch (TransformerException e) {
                this.log.warning(e.getMessage());
            }
        }
        if (serviceEndpoint.getEndpointName() != null) {
            endpoint.setName(QName.valueOf(serviceEndpoint.getEndpointName()));
        }
        if (serviceEndpoint.getInterfaces() != null) {
            endpoint.setInterface(serviceEndpoint.getInterfaces()[0]);
        }
        endpoint.setService(serviceEndpoint.getServiceName());

        if (serviceEndpoint.getLocation() != null) {
            endpoint.setComponent(serviceEndpoint.getLocation().getComponentName());
            endpoint.setContainer(serviceEndpoint.getLocation().getContainerName());
            endpoint.setSubdomain(serviceEndpoint.getLocation().getSubdomainName());
        }
        endpoint.setType(serviceEndpoint.getType().toString().toLowerCase());

        try {
            // propagate to all
            this.client.put(this.getKey(serviceEndpoint), endpoint, true);
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateEndpoint(String endpointName, QName serviceName)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if ((endpointName == null) || (serviceName == null)) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Can not deactivate null endpoint");
        }

        Endpoint ep = null;
        String key = this.getKey(endpointName, serviceName);
        try {
            // get from all
            ep = this.client.get(key, true);
            if (ep == null) {
                throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                        "Can not unregister not registered endpoint...");

            }
        } catch (RegistryException e1) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e1.getMessage());
        }

        try {
            // say it to all
            this.client.delete(key, true);
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Can not delete endpoint under " + key, e);
        }

        List<RegistryListener> listeners = this.getListeners();
        if (listeners != null) {
            final JBIServiceEndpointImpl endpoint = new JBIServiceEndpointImpl();
            endpoint.setServiceName(ep.getService());
            endpoint.setEndpointName(ep.getName());
            List<QName> interfaces = new ArrayList<QName>(1);
            interfaces.add(ep.getInterface());
            endpoint.setInterfacesName(interfaces);
            endpoint.setStringDescription(ep.getDescription());
            for (RegistryListener listener : this.getListeners()) {
                listener.onUnregister(endpoint);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deregisterConnection(QName consInterface, QName provService, String provEndpoint)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    public void deregisterConnection(QName consService, String consEndpoint, QName provService,
            String provEndpoint) throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    public void deregisterExternalEndpoint(String endpointName, QName serviceName)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        this.deactivateEndpoint(endpointName, serviceName);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint getEndpoint(QName service, String name)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getEndpoint with params service, name = " + service
                    + ", " + name);
        }

        org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint result = null;

        Query q = new Query();
        q.setEndpoint(QName.valueOf(name));
        q.setService(service);

        try {
            List<Endpoint> ep = this.client.lookup(q, false);
            // FIXME : get the first one...
            if ((ep != null) && (ep.size() > 0)) {
                Endpoint e = ep.get(0);
                if ((e.getType() != null)
                        && e.getType().equalsIgnoreCase(
                                EndpointType.EXTERNAL.toString().toLowerCase())) {

                    JBIServiceEndpointImpl xe = new JBIServiceEndpointImpl();
                    xe.setType(EndpointType.EXTERNAL);
                    xe.setStringDescription(e.getDescription());
                    xe.setEndpointName(e.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(e.getInterface());
                    xe.setInterfacesName(interfaces);
                    xe.setServiceName(e.getService());
                    xe.getLocation().setComponentName(e.getComponent());
                    xe.getLocation().setContainerName(e.getContainer());
                    xe.getLocation().setSubdomainName(e.getSubdomain());

                    result = xe;
                } else if ((e.getType() != null)
                        && e.getType().equalsIgnoreCase(
                                EndpointType.INTERNAL.toString().toLowerCase())) {
                    JBIServiceEndpointImpl ie = new JBIServiceEndpointImpl();
                    ie.setType(EndpointType.INTERNAL);
                    ie.setStringDescription(e.getDescription());
                    ie.setEndpointName(e.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(e.getInterface());
                    ie.setInterfacesName(interfaces);
                    ie.setServiceName(e.getService());
                    ie.getLocation().setComponentName(e.getComponent());
                    ie.getLocation().setContainerName(e.getContainer());
                    ie.getLocation().setSubdomainName(e.getSubdomain());
                    result = ie;
                } else {
                    this.log.warning("UNKNOW EP TYPE " + e.getType());
                }
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Document getEndpointDescriptorForEndpoint(ServiceEndpoint endpoint)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getEndpointDescriptorForEndpoint with params endpoint = "
                            + endpoint);
        }

        if (endpoint == null) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException("Null endpoint");
        }
        Document result = endpoint.getDescription();
        if (result == null) {
            // try to get the endpoint from the registry
            try {
                Endpoint e = this.client.get(this.getKey(endpoint), true);
                if ((e != null) && (e.getDescription() != null)) {
                    result = XMLUtil.createDocumentFromString(e.getDescription());
                }
            } catch (RegistryException e) {
                throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
            }
        }
        return result;
    }

    public ServiceEndpoint[] getExternalEndpointsForInterface(QName interfaceName)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getExternalEndpointsForInterface with params interfaceName = "
                            + interfaceName);
        }

        ServiceEndpoint[] result = new ServiceEndpoint[0];
        Query q = new Query();
        q.setInterface(interfaceName);
        q.setType(EndpointType.EXTERNAL.toString().toLowerCase());

        try {
            List<Endpoint> eps = this.client.lookup(q, false);
            if (eps != null) {
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> list = new ArrayList<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>(
                        eps.size());
                for (Endpoint endpoint : eps) {
                    JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                    ep.setType(EndpointType.EXTERNAL);
                    ep.setStringDescription(endpoint.getDescription());
                    ep.setEndpointName(endpoint.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(endpoint.getInterface());
                    ep.setInterfacesName(interfaces);
                    ep.setServiceName(endpoint.getService());
                    ep.getLocation().setComponentName(endpoint.getComponent());
                    ep.getLocation().setContainerName(endpoint.getContainer());
                    ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                    list.add(ep);
                }
                result = list
                        .toArray(new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint[list
                                .size()]);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    public ServiceEndpoint[] getExternalEndpointsForService(QName serviceName)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getExternalEndpointsForService with params serviceName = "
                            + serviceName);
        }

        if (serviceName == null) {
            throw new NullArgumentException("serviceName");
        }

        ServiceEndpoint[] result = new ServiceEndpoint[0];
        Query q = new Query();
        q.setService(serviceName);
        q.setType(EndpointType.EXTERNAL.toString().toLowerCase());

        try {
            List<Endpoint> eps = this.client.lookup(q, false);
            if (eps != null) {
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> list = new ArrayList<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>(
                        eps.size());
                for (Endpoint endpoint : eps) {
                    JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                    ep.setType(EndpointType.EXTERNAL);
                    ep.setStringDescription(endpoint.getDescription());
                    ep.setEndpointName(endpoint.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(endpoint.getInterface());
                    ep.setInterfacesName(interfaces);
                    ep.setServiceName(endpoint.getService());
                    ep.getLocation().setComponentName(endpoint.getComponent());
                    ep.getLocation().setContainerName(endpoint.getContainer());
                    ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                    list.add(ep);
                }
                result = list
                        .toArray(new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint[list
                                .size()]);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public QName[] getInterfacesForEndpoint(ServiceEndpoint endpoint) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getInterfacesForEndpoint with params endpoint = "
                    + endpoint);
        }

        if (endpoint == null) {
            throw new NullArgumentException("endpoint");
        }

        QName[] result = null;

        if (endpoint.getInterfaces() == null) {
            // try to get it from the registry...
            try {
                Endpoint e = this.client.get(this.getKey(endpoint), true);
                if ((e != null) && (e.getInterface() != null)) {
                    result = endpoint.getInterfaces();
                }
            } catch (RegistryException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ServiceEndpoint[] getInternalEndpointsForInterface(QName interfaceName, LinkType linktype)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getInternalEndpointsForInterface with params interfaceName, linktype = "
                            + interfaceName + ", " + linktype);
        }

        ServiceEndpoint[] result = new ServiceEndpoint[0];
        Query q = new Query();
        // if interface is null, we will get all...
        q.setInterface(interfaceName);
        q.setType(EndpointType.INTERNAL.toString().toLowerCase());

        try {
            List<Endpoint> eps = this.client.lookup(q, false);
            if (eps != null) {
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> list = new ArrayList<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>(
                        eps.size());
                for (Endpoint endpoint : eps) {
                    JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                    ep.setType(EndpointType.INTERNAL);
                    ep.setStringDescription(endpoint.getDescription());
                    ep.setEndpointName(endpoint.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(endpoint.getInterface());
                    ep.setInterfacesName(interfaces);
                    ep.setServiceName(endpoint.getService());
                    ep.getLocation().setComponentName(endpoint.getComponent());
                    ep.getLocation().setContainerName(endpoint.getContainer());
                    ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                    list.add(ep);
                }
                result = list
                        .toArray(new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint[list
                                .size()]);
            }
        } catch (RegistryException e) {
            this.log.warning("Fail to get endpoints for interface '" + interfaceName + "'");
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e);
        }
        return result;
    }

    public ServiceEndpoint[] getInternalEndpointsForService(QName serviceName, LinkType linktype)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getInternalEndpointsForService with params serviceName, linktype = "
                            + serviceName + ", " + linktype);
        }
        if (serviceName == null) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "No serviceName found");
        }

        ServiceEndpoint[] result = new ServiceEndpoint[0];
        Query q = new Query();
        q.setService(serviceName);
        q.setType(EndpointType.INTERNAL.toString().toLowerCase());

        try {
            List<Endpoint> eps = this.client.lookup(q, false);
            if (eps != null) {
                List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> list = new ArrayList<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>(
                        eps.size());
                for (Endpoint endpoint : eps) {
                    JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                    ep.setType(EndpointType.INTERNAL);
                    ep.setStringDescription(endpoint.getDescription());
                    ep.setEndpointName(endpoint.getName().toString());
                    List<QName> interfaces = new ArrayList<QName>(1);
                    interfaces.add(endpoint.getInterface());
                    ep.setInterfacesName(interfaces);
                    ep.setServiceName(endpoint.getService());
                    ep.getLocation().setComponentName(endpoint.getComponent());
                    ep.getLocation().setContainerName(endpoint.getContainer());
                    ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                    list.add(ep);
                }
                result = list
                        .toArray(new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint[list
                                .size()]);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    public void registerConnection(QName consInterface, QName provService, String provEndpoint)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        throw new NotImplementedException();

    }

    public void registerConnection(QName consService, String consEndpoint, QName provService,
            String provEndpoint) throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        throw new NotImplementedException();
    }

    /**
     * FIXME ! {@inheritDoc}
     */
    public void registerExternalEndpoint(javax.jbi.servicedesc.ServiceEndpoint externalEndpoint)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : registerExternalEndpoint with params externalEndpoint = "
                            + externalEndpoint);
        }

        if (externalEndpoint == null) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Can not register a null external endpoint");
        }
        if (externalEndpoint instanceof org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) {
            this
                    .registerEndpoint((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) externalEndpoint);
        } else {
            this.log.warning("Trying to register a bad external service endpoint");
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(
                    "Bad external service endpoint type");
        }

    }

    /**
     * {@inheritDoc}
     */
    public Document getDescription(String serviceName, String endpointName)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {

        if (this.log.isDebugEnabled()) {
            this.log
                    .debug("Entering method : getDescription with params serviceName, endpointName = "
                            + serviceName + ", " + endpointName);
        }
        final String s = serviceName;
        final String e = endpointName;

        ServiceEndpoint endpoint = new ServiceEndpoint() {

            public QName getServiceName() {
                return QName.valueOf(s);
            }

            public QName[] getInterfaces() {
                return null;
            }

            public String getEndpointName() {
                return e;
            }

            public DocumentFragment getAsReference(QName operationName) {
                return null;
            }

            public EndpointType getType() {
                return null;
            }

            public Document getDescription() {
                return null;
            }

            public List<QName> getInterfacesName() {
                return null;
            }

            public Location getLocation() {
                return null;
            }

            public void setType(EndpointType type) {
            }

            public Map<String, String> getProperties() {
                return null;
            }
        };
        return this.getEndpointDescriptorForEndpoint(endpoint);
    }

    private String getKey(ServiceEndpoint ep) {
        return this.getKey(ep.getEndpointName(), ep.getServiceName());
    }

    protected String getKey(final String epName, final QName serviceName) {
        String rootPath = this.getRootPath();
        if ((rootPath == null) || (rootPath.trim().length() == 0)) {
            rootPath = "/";
        }

        if (!rootPath.endsWith("/")) {
            rootPath = rootPath + "/";
        }
        return rootPath + serviceName.toString() + "@" + epName;
    }

    /**
     * Get the root path where the endpoints will be stored.
     * 
     * @return A path which must end with '/'
     */
    protected abstract String getRootPath();

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> getExternalEndpoints()
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getExternalEndpoints");
        }

        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        Query q = new Query();
        q.setType(EndpointType.EXTERNAL.toString().toLowerCase());
        try {
            List<Endpoint> list = this.client.lookup(q, true);
            for (Endpoint endpoint : list) {
                JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                ep.setType(EndpointType.EXTERNAL);
                ep.setStringDescription(endpoint.getDescription());
                ep.setEndpointName(endpoint.getName().toString());
                List<QName> interfaces = new ArrayList<QName>(1);
                interfaces.add(endpoint.getInterface());
                ep.setInterfacesName(interfaces);
                ep.setServiceName(endpoint.getService());
                ep.getLocation().setComponentName(endpoint.getComponent());
                ep.getLocation().setContainerName(endpoint.getContainer());
                ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                result.add(ep);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    public List<ServiceEndpoint> getInternalEndpoints()
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : getInternalEndpoints");
        }
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        Query q = new Query();
        q.setType(EndpointType.INTERNAL.toString().toLowerCase());
        try {
            List<Endpoint> list = this.client.lookup(q, true);
            for (Endpoint endpoint : list) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Found internal endoint : " + endpoint);
                }
                JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                ep.setType(EndpointType.INTERNAL);
                ep.setStringDescription(endpoint.getDescription());
                ep.setEndpointName(endpoint.getName().toString());
                List<QName> interfaces = new ArrayList<QName>(1);
                interfaces.add(endpoint.getInterface());
                ep.setInterfacesName(interfaces);
                ep.setServiceName(endpoint.getService());
                ep.getLocation().setComponentName(endpoint.getComponent());
                ep.getLocation().setContainerName(endpoint.getContainer());
                ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                result.add(ep);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ServiceEndpoint> query(String endpointName, QName itf, QName service,
            String containerName, String componentName, String subDomainName, String type)
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : query");
        }
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();

        Query q = new Query();
        if (endpointName != null) {
            q.setEndpoint(QName.valueOf(endpointName));
        }

        q.setInterface(itf);
        q.setService(service);
        q.setContainer(containerName);
        q.setComponent(componentName);
        q.setSubDomain(subDomainName);
        q.setType(type);

        try {
            List<Endpoint> list = this.client.lookup(q, true);
            for (Endpoint endpoint : list) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Found endoint : " + endpoint);
                }
                JBIServiceEndpointImpl ep = new JBIServiceEndpointImpl();
                ep.setType(EndpointType.INTERNAL);
                ep.setStringDescription(endpoint.getDescription());
                ep.setEndpointName(endpoint.getName().toString());
                List<QName> itfs = new ArrayList<QName>(1);
                itfs.add(endpoint.getInterface());
                ep.setInterfacesName(itfs);
                ep.setServiceName(endpoint.getService());
                ep.getLocation().setComponentName(endpoint.getComponent());
                ep.getLocation().setContainerName(endpoint.getContainer());
                ep.getLocation().setSubdomainName(endpoint.getSubdomain());
                result.add(ep);
            }
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void removeAllLocalEndpoints()
            throws org.ow2.petals.jbi.messaging.registry.RegistryException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Entering method : removeAllLocalEndpoints");
        }

        try {
            // delete all the entries from the local registry means that we
            // delete all under the local path.
            this.client.delete(this.getRootPath(), true);
        } catch (RegistryException e) {
            throw new org.ow2.petals.jbi.messaging.registry.RegistryException(e.getMessage());
        }
    }

}
