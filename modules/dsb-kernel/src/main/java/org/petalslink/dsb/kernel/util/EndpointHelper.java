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
package org.petalslink.dsb.kernel.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.petalslink.dsb.ws.api.ServiceEndpoint;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class EndpointHelper {

    public static final List<ServiceEndpoint> getEndpoints(URI wsdlURI) {
        if (wsdlURI == null) {
            return null;
        }
        return getEndpoints(WSDLHelper.readWSDL(wsdlURI));
    }

    /**
     * Get the endpoints of a given WSDL Description
     * 
     * @param description
     * @return
     */
    public static final List<ServiceEndpoint> getEndpoints(Description description) {
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        List<Service> services = description.getServices();
        for (Service service : services) {
            QName serviceName = service.getQName();
            List<Endpoint> endpoints = service.getEndpoints();
            for (Endpoint endpoint : endpoints) {
                ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
                serviceEndpoint.setEndpoint(endpoint.getName());
                serviceEndpoint.setItf(new QName(endpoint.getBinding().getInterface().getQName()
                        .getNamespaceURI(), endpoint.getBinding().getInterface().getQName()
                        .getLocalPart()));
                serviceEndpoint.setService(new QName(serviceName.getNamespaceURI(), serviceName
                        .getLocalPart()));
                result.add(serviceEndpoint);
            }
        }
        return result;
    }

    /**
     * Get the service endpoint from the REST service URL
     * 
     * @param restServiceURL
     * @return
     */
    public static final ServiceEndpoint getRESTEndpoint(URI restServiceURI) {
        if (restServiceURI == null) {
            return null;
        }
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
        String epName = null;
        epName = restServiceURI.toString();
        if (epName.startsWith("http://")) {
            epName = epName.substring("http://".length(), epName.length());
        } else if (epName.startsWith("https://")) {
            epName = epName.substring("https://".length(), epName.length());
        }
        epName = epName.replaceAll("\\.", "");

        epName = epName.replaceAll("\\:", "");
        epName = epName.replaceAll("\\/", "");
        String serviceName = epName + "Service";
        String interfaceName = epName + "Interface";
        String namespace = restServiceURI.toString();

        // TODO : pass it as parameter : a platform service got a prefix...

        String endpointName = org.petalslink.dsb.kernel.Constants.REST_PLATFORM_ENDPOINT_PREFIX
                + epName + "Endpoint";

        serviceEndpoint.setEndpoint(endpointName);
        serviceEndpoint.setItf(new QName(namespace, interfaceName));
        serviceEndpoint.setService(new QName(namespace, serviceName));

        return serviceEndpoint;
    }

}
