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
package org.petalslink.dsb.kernel.monitor.wsdm.registry;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hsqldb.lib.HashMap;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.RegistryListener;
import org.ow2.petals.util.LoggingUtil;
import org.petals.ow2.admin.Admin;
import org.petals.ow2.admin.Admin_Service;
import org.petalslink.dsb.kernel.monitor.wsdm.ConfigurationService;
import org.w3c.dom.Document;

/**
 * Notify the monitoring Bus that a new endpoint has been added in the registry.
 * Uses the {@link RegistryListener} listener which is fired at each endpoint
 * registration..
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListener.class) })
public class MonitoringNotifierImpl implements RegistryListener {

    private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";

    private static Admin_Service adminService;

    @Requires(name = "monitoringconfiguration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * TODO : Cache endpoints to be registered
     */
    // private Map<String, ServiceEndpoint> cache;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void onRegister(ServiceEndpoint endpoint) {
        if (!configurationService.isActive()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Monitoring is not active, do not register endpoint");
                // TODO : Cache the endpoint information for future use when the
                // monitoring can be activated at runtime
            }
            return;
        }

        // let's say to the monitoring platform that there is something new...
        if (this.log.isInfoEnabled()) {
            this.log.info("Notifying monitoring Bus that endpoint has been registered : "
                    + endpoint);
        }
        Thread t = new AddTask(endpoint);
        t.start();
    }

    /**
     * {@inheritDoc}
     */
    public void onUnregister(ServiceEndpoint endpoint) {
        if (!configurationService.isActive()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Monitoring is not active, do not unregister endpoint");
                // TODO : Cache the endpoint information for future use when the
                // monitoring can be activated at runtime
            }
            return;
        }
        // TODO = Unregister!
    }

    private class AddTask extends Thread {

        private final ServiceEndpoint endpoint;

        /**
		 * 
		 */
        public AddTask(ServiceEndpoint serviceEndpoint) {
            this.endpoint = serviceEndpoint;
        }

        @Override
        public void run() {
            URL wsdl = null;
            try {
                wsdl = new URL(MonitoringNotifierImpl.this.configurationService.getAdminURL()
                        + "?wsdl");
            } catch (MalformedURLException e) {
                MonitoringNotifierImpl.this.log.warning(e.getMessage());
            }

            Admin_Service client = getClient(wsdl);
            if (client != null) {
                Admin port = client.getAdminSOAP();
                javax.xml.namespace.QName _createMonitoringEndpoint_wsdmServiceName = this.endpoint
                        .getServiceName();
                java.lang.String _createMonitoringEndpoint_wsdmProviderEndpointName = this.endpoint
                        .getEndpointName() + ENDPOINT_SUFFIX;
                boolean _createMonitoringEndpoint_exposeInSoap = true;
                java.lang.String _createMonitoringEndpoint__return = port.createMonitoringEndpoint(
                        _createMonitoringEndpoint_wsdmServiceName,
                        _createMonitoringEndpoint_wsdmProviderEndpointName,
                        _createMonitoringEndpoint_exposeInSoap);
                MonitoringNotifierImpl.this.log.info("createMonitoringEndpoint.result="
                        + _createMonitoringEndpoint__return);

            } else {
                MonitoringNotifierImpl.this.log.error("Can not get admin service client...");
            }
        }
    }

    private static synchronized Admin_Service getClient(URL wsdlURL) {
        if (adminService == null) {
            try {
                adminService = new Admin_Service(wsdlURL, new QName("http://ow2.petals.org/Admin/",
                        "Admin"));
            } catch (Exception e) {
                adminService = null;
            }
        }
        return adminService;
    }

    public static void writeDocument(final Document document, final OutputStream outputStream)
            throws Exception {

        if ((document != null) && (outputStream != null)) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } else {
            throw new Exception("Can not write document to output stream");
        }
    }
}
