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
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.messaging.RegistryListener;
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

    @Requires(name = "monitoringconfiguration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "monitoringclientfactory", signature = MonitoringClientFactory.class)
    private MonitoringClientFactory factory;

    @Monolog(name = "logger")
    private Logger logger;

    protected org.ow2.petals.kernel.api.log.Logger log;

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
    public void onRegister(ServiceEndpoint endpoint) throws DSBException {
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
        MonitoringAdminClient client = getClient();
        if (client == null) {
            log.warning("Can not get any client to send message to monitoring layer");
            return;
        }

        try {
            client.createMonitoringEndpoint(endpoint);
        } catch (Exception e) {
            final String message = "Can not add monitoring endpoint";
            if (this.log.isDebugEnabled()) {
                this.log.warning(message, e);                
            } else {
                this.log.warning(message);                                
            }
        }
    }

    /**
     * 
     */
    private MonitoringAdminClient getClient() {
        return factory.getMonitoringAdminClient();
    }

    /**
     * {@inheritDoc}
     */
    public void onUnregister(ServiceEndpoint endpoint) throws DSBException {
        if (!configurationService.isActive()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Monitoring is not active, do not unregister endpoint");
                // TODO : Cache the endpoint information for future use when the
                // monitoring can be activated at runtime
            }
            return;
        }

        if (this.log.isInfoEnabled()) {
            this.log.info("Notifying monitoring Bus that endpoint has been unregistered : "
                    + endpoint);
        }

        MonitoringAdminClient client = getClient();
        if (client == null) {
            log.warning("Can not get any client to send message to monitoring layer");
            return;
        }

        try {
            client.deleteMonitoringEndpoint(endpoint);
        } catch (Exception e) {
            this.log.warning("Can not delete monitoring endpoint", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.messaging.RegistryListener#getName()
     */
    public String getName() {
        return "MonitoringRegistryListener";
    }
}
