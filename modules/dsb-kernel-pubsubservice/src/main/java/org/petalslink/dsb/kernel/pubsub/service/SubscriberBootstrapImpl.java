/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.notification.commons.PropertiesConfigurationProducer;
import org.petalslink.dsb.notification.commons.api.ConfigurationProducer;
import org.petalslink.dsb.notification.commons.api.NotificationManager;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * Subscribe to the local engine on behalf of others... Defined in local
 * properties file.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
public class SubscriberBootstrapImpl {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void subscribeOnBehalf() {
        // Let see if the notification center is available...
        NotificationManager manager = NotificationCenter.get().getManager();
        if (manager == null) {
            log.warning("Can not find the notification manager");
            return;
        }

        NotificationProducerEngine engine = manager.getNotificationProducerEngine();
        if (engine == null) {
            log.warning("Can not find the notification producer engine");
            return;
        }

        // create default subscribers, ie automatically subscribe to myself for
        // others...
        // look if we have some configuration about subscribers...
        URL subscribers = SubscriberBootstrapImpl.class.getClassLoader().getResource(
                "subscribers.cfg");

        Properties subscriberProps = null;
        if (subscribers != null) {
            subscriberProps = new Properties();
            try {
                subscriberProps.load(SubscriberBootstrapImpl.class.getClassLoader()
                        .getResourceAsStream("subscribers.cfg"));
            } catch (IOException e) {
                log.warning("Error while loading subscribers", e);
            }
        }

        if (subscriberProps != null) {
            ConfigurationProducer producers = new PropertiesConfigurationProducer(subscriberProps);
            List<Subscribe> toSubscribe = producers.getSubscribes();
            for (Subscribe subscribe : toSubscribe) {
                // let's subscribe...
                try {
                    final com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse = engine
                            .subscribe(subscribe);

                    if (log.isDebugEnabled()) {
                        Document doc = Wsnb4ServUtils.getWsnbWriter().writeSubscribeResponseAsDOM(
                                subscribeResponse);
                        log.debug("KERNEL SUBSCRIBE RESPONSE = "
                                + XMLHelper.createStringFromDOMDocument(doc));
                    }
                } catch (Exception e) {
                    log.warning("Error while subscribing", e);
                }
            }
        }
    }
}
