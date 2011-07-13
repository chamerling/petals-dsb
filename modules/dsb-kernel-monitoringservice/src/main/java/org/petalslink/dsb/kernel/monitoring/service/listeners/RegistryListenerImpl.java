/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service.listeners;

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
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.jbi.Adapter;
import org.petalslink.dsb.kernel.monitoring.service.ConfigurationService;
import org.petalslink.dsb.monitoring.api.MonitoringAdminClient;
import org.petalslink.dsb.monitoring.api.MonitoringClientFactory;

/**
 * Notify the monitoring Bus that a new endpoint has been added in the registry.
 * Uses the {@link RegistryListener} listener which is fired at each endpoint
 * registration..
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListener.class) })
public class RegistryListenerImpl implements RegistryListener {

    private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";

    // private static Admin_Service adminService;

    @Requires(name = "monitoringconfiguration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "monitoringclientfactory", signature = MonitoringClientFactory.class)
    private MonitoringClientFactory factory;

    @Monolog(name = "logger")
    private Logger logger;

    protected org.ow2.petals.kernel.api.log.Logger log;

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
        org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint = Adapter
                .createServiceEndpoint(endpoint);
        if (this.log.isInfoEnabled()) {
            this.log.info("Notifying monitoring Bus that endpoint has been registered : "
                    + serviceEndpoint);
        }
        MonitoringAdminClient client = getClient();
        if (client == null) {
            log.warning("Can not get any client to send message to monitoring layer");
            return;
        }

        try {
            client.createMonitoringEndpoint(serviceEndpoint);
        } catch (DSBException e) {
            this.log.warning("Can not add monitoring endpoint", e);
        }
    }

    /**
     * 
     */
    private MonitoringAdminClient getClient() {
        return factory.getMonitoringAdminClient(configurationService.getAdminURL());
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

        org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint = Adapter
                .createServiceEndpoint(endpoint);
        if (this.log.isInfoEnabled()) {
            this.log.info("Notifying monitoring Bus that endpoint has been unregistered : "
                    + serviceEndpoint);
        }

        MonitoringAdminClient client = getClient();
        if (client == null) {
            log.warning("Can not get any client to send message to monitoring layer");
            return;
        }

        try {
            client.deleteMonitoringEndpoint(serviceEndpoint);
        } catch (DSBException e) {
            this.log.warning("Can not delete monitoring endpoint", e);
        }
    }

    // private class AddTask extends Thread {
    //
    // private final ServiceEndpoint endpoint;
    //
    // /**
    // *
    // */
    // public AddTask(ServiceEndpoint serviceEndpoint) {
    // this.endpoint = serviceEndpoint;
    // }
    //
    // @Override
    // public void run() {
    // URL wsdl = null;
    // try {
    // wsdl = new
    // URL(RegistryListenerImpl.this.configurationService.getAdminURL()
    // + "?wsdl");
    // } catch (MalformedURLException e) {
    // RegistryListenerImpl.this.log.warning(e.getMessage());
    // }
    //
    // MonitoringAdminClient client = getClient(wsdl);
    // if (client != null) {
    // Admin port = client.getAdminSOAP();
    // javax.xml.namespace.QName _createMonitoringEndpoint_wsdmServiceName =
    // this.endpoint
    // .getServiceName();
    // java.lang.String _createMonitoringEndpoint_wsdmProviderEndpointName =
    // this.endpoint
    // .getEndpointName() + ENDPOINT_SUFFIX;
    // boolean _createMonitoringEndpoint_exposeInSoap = true;
    // java.lang.String _createMonitoringEndpoint__return =
    // port.createMonitoringEndpoint(
    // _createMonitoringEndpoint_wsdmServiceName,
    // _createMonitoringEndpoint_wsdmProviderEndpointName,
    // _createMonitoringEndpoint_exposeInSoap);
    // RegistryListenerImpl.this.log.info("createMonitoringEndpoint.result="
    // + _createMonitoringEndpoint__return);
    //
    // } else {
    // RegistryListenerImpl.this.log.error("Can not get admin service client...");
    // }
    // }
    // }
    //
    // private static synchronized Admin_Service getClient(URL wsdlURL) {
    // if (adminService == null) {
    // try {
    // adminService = new Admin_Service(wsdlURL, new
    // QName("http://ow2.petals.org/Admin/",
    // "Admin"));
    // } catch (Exception e) {
    // adminService = null;
    // }
    // }
    // return adminService;
    // }
    //
    // public static void writeDocument(final Document document, final
    // OutputStream outputStream)
    // throws Exception {
    //
    // if ((document != null) && (outputStream != null)) {
    // Transformer transformer =
    // TransformerFactory.newInstance().newTransformer();
    //
    // transformer.transform(new DOMSource(document), new
    // StreamResult(outputStream));
    // } else {
    // throw new Exception("Can not write document to output stream");
    // }
    // }

}
