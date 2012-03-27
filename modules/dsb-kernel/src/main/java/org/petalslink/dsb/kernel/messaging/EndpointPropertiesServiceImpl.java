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
package org.petalslink.dsb.kernel.messaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.container.lifecycle.ServiceAssemblyLifeCycle;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.descriptor.original.generated.Provides;
import org.ow2.petals.jbi.descriptor.original.generated.ServiceAssembly;
import org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.w3c.dom.Element;

/**
 * Get the SU and SA information and put them in the Endpoint. Look at
 * {@link EndpointPropertiesService}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
// CHA 2012 : Not used in DSB and org.ow2.petals.jbi.messaging.endpoint.EndpointPropertiesService dispaeared...
public class EndpointPropertiesServiceImpl { //implements EndpointPropertiesService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "adminservice", signature = AdminService.class)
    private AdminService adminService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    public java.util.Map<String, String> getProperties(String endpointName,
            javax.xml.namespace.QName serviceName) {
        Map<String, String> properties = new HashMap<String, String>();
        Map<String, ServiceAssemblyLifeCycle> sas = this.adminService.getServiceAssemblies();
        Iterator<String> iter = sas.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            ServiceAssemblyLifeCycle saLifeCycle = sas.get(key);
            if (saLifeCycle != null) {
                List<ServiceUnitLifeCycle> suLifeCycles = saLifeCycle.getServiceUnitLifeCycles();
                for (ServiceUnitLifeCycle serviceUnitLifeCycle : suLifeCycles) {
                    if (serviceUnitLifeCycle.getServiceUnitDescriptor().getServices() != null) {
                        List<Provides> provides = serviceUnitLifeCycle.getServiceUnitDescriptor()
                                .getServices().getProvides();
                        if (provides != null) {
                            for (Provides provides2 : provides) {
                                if (serviceName.equals(provides2.getServiceName())
                                        && endpointName.equals(provides2.getEndpointName())) {

                                    String suName = serviceUnitLifeCycle.getSuName();
                                    ServiceAssembly sa = saLifeCycle.getServiceAssembly();
                                    Map<String, String> saProperties = new HashMap<String, String>();
                                    saProperties.put("sa.name", sa.getIdentification().getName());
                                    saProperties.put("sa.description", sa.getIdentification()
                                            .getDescription());
                                    properties.putAll(saProperties);

                                    ServiceUnit su = null;
                                    List<ServiceUnit> sus = sa.getServiceUnit();
                                    for (ServiceUnit serviceUnit : sus) {
                                        if (suName
                                                .equals(serviceUnit.getIdentification().getName())) {
                                            su = serviceUnit;
                                        }
                                    }

                                    if (su != null) {
                                        Map<String, String> suProperties = new HashMap<String, String>();
                                        suProperties.put("su.description", su.getIdentification()
                                                .getDescription());
                                        suProperties.put("su.name", su.getIdentification()
                                                .getName());
                                        suProperties
                                                .put("su.zip", su.getTarget().getArtifactsZip());
                                        suProperties.put("su.componentname", su.getTarget()
                                                .getComponentName());
                                        properties.putAll(suProperties);
                                    }

                                    properties.put("su.provides.service", provides2
                                            .getServiceName().toString());
                                    properties.put("su.provides.interface", provides2
                                            .getInterfaceName().toString());
                                    properties.put("su.provides.endpoint", provides2
                                            .getEndpointName());

                                    List<Element> elements = provides2.getAnyOrAny();
                                    for (Element element : elements) {
                                        // TODO : go into children
                                        if (element.getTextContent() != null) {
                                            QName q = new QName(element.getNamespaceURI(), element
                                                    .getLocalName());
                                            properties.put(q.toString(), element.getTextContent());
                                        }
                                    }
                                    return properties;
                                }
                            }
                        }
                    }
                }
            }
        }
        return properties;
    }
}
