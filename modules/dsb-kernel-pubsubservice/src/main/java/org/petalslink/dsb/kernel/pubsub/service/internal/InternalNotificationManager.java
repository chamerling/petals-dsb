/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.util.EndpointHelper;
import org.petalslink.dsb.kernel.pubsub.service.NotificationCenter;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.notification.commons.api.NotificationManager;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * Register all the internal notification aware components in the notification
 * center
 * 
 * @author chamerling
 * 
 */
@FractalComponent
// @Provides(interfaces = { @Interface(name = "service", signature =
// enclosing_type) })
public class InternalNotificationManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "scanner", signature = NotificationConsumerScanner.class)
    protected NotificationConsumerScanner scanner;

    @Requires(name = "registry", signature = NotificationConsumerRegistry.class)
    protected NotificationConsumerRegistry registry;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /**
     * Register all the components in the notification center. Done when all is
     * ready...
     */
    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void register() {
        NotificationManager manager = NotificationCenter.get().getManager();
        if (manager == null) {
            this.log.warning("Can not find the notification manager to register local subscribers!");
        }

        NotificationProducerEngine engine = manager.getNotificationProducerEngine();
        if (engine == null) {
            this.log.warning("Can not find the notification engine to register local subscribers!");
        }

        List<NotificationTargetBean> beans = this.scanner.scan();
        for (NotificationTargetBean bean : beans) {

            if (log.isDebugEnabled()) {
                log.debug(String.format("Registering Java listener to the notification engine %s",
                        bean.toString()));
            }

            // get all the topics, then register and create the wrapper if
            // subscription is successful...
            String[] topics = bean.topic;
            if (topics != null) {
                for (String string : topics) {
                    QName tmp = QName.valueOf(string);
                    String namespaceURI = tmp.getNamespaceURI();
                    String localPart = null;
                    String prefix = "";
                    if (tmp.getLocalPart().contains(":")) {
                        localPart = tmp.getLocalPart().substring(
                                tmp.getLocalPart().indexOf(':') + 1);
                        prefix = tmp.getLocalPart().substring(0, tmp.getLocalPart().indexOf(':'));
                    } else {
                        localPart = tmp.getLocalPart();
                    }
                    QName topic = new QName(namespaceURI, localPart, prefix);

                    // wrap and subscribe...
                    InternalNotificationConsumer consumer = new InternalNotificationConsumerWrapper(
                            bean, topic);

                    // create a unique local ID...
                    String uniqueID = EndpointHelper.JAVA_PREFIX + "://" + UUID.randomUUID();

                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Registering for topic '%s' with unique ID '%s'",
                                topic.toString(), uniqueID));
                    }

                    try {
                        // store before subscriber so that we can receive
                        // notification right now!
                        this.registry.add(uniqueID, consumer);
                        SubscribeResponse response = engine.subscribe(NotificationHelper
                                .createSubscribe(uniqueID, topic));

                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Got response : %s", XMLHelper
                                    .createStringFromDOMDocument(Wsnb4ServUtils.getWsnbWriter()
                                            .writeSubscribeResponseAsDOM(response))));
                        }

                        // TODO : Manage ID so we can unregister...

                    } catch (Exception e) {
                        // remove the subscriber from the registry!
                        e.printStackTrace();
                        this.registry.remove(uniqueID);
                    }
                }
            }
        }
    }
}
