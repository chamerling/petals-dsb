/**
 * 
 */
package org.petalslink.dsb.kernel.wsn;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.api.WSAConstants;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.notification.commons.PropertiesConfigurationProducer;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.WSAMessageImpl;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * The notification bootstrap subscribes to notification producers at DSB
 * startup. The producers to subscribe to are defined in the consumer.cfg file
 * available in the classpath.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationBootstrap.class) })
public class NotificationBootstrapImpl implements NotificationBootstrap {

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

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.pubsub.service.NotificationBootstrap#
     * subscribeToProducers()
     */
    @LifeCycleListener(phase = Phase.START, priority = 0)
    public void subscribeToProducers() {
        // use the WSA stuff to send subscribes...
        subscribeTo();
    }

    /**
     * Subscribes to some predefined notification producers. The configuration
     * is defined in a file. This assumes that all the required components are
     * already installed since it uses the WSAdressing feature.
     */
    protected void subscribeTo() {
        URL consumerFile = NotificationBootstrapImpl.class.getClassLoader().getResource(
                "consumer.cfg");
        Properties consumerProps = null;
        if (consumerFile != null) {
            consumerProps = new Properties();
            try {
                consumerProps.load(NotificationBootstrapImpl.class.getClassLoader()
                        .getResourceAsStream("consumer.cfg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (consumerProps != null) {
            PropertiesConfigurationProducer producers = new PropertiesConfigurationProducer(
                    consumerProps);

            Map<String, Subscribe> toSubscribe = producers.getSubscribe();

            for (String key : toSubscribe.keySet()) {
                // let's subscribe on behalf of the service bus...
                String to = producers.getProperty(key, "producerReference");
                if (to != null) {
                    if (log.isInfoEnabled()) {
                        log.info(String.format("Sending subscribe to %s", to));
                    }

                    // FIXME : need to set it statically somewhere...
                    URI uri = URI.create(to);
                    ServiceEndpoint wsaEP = new ServiceEndpoint();
                    String ns = String.format(WSAConstants.NS_TEMPLATE, uri.getScheme());
                    wsaEP.setEndpointName(WSAConstants.ENDPOINT_NAME);
                    wsaEP.setServiceName(new QName(ns, WSAConstants.SERVICE_NAME));
                    wsaEP.setInterfaces(new QName[] { new QName(ns, WSAConstants.INTERFACE_NAME) });

                    QName operation = WsnbConstants.SUBSCRIBE_QNAME;

                    Message message = new WSAMessageImpl(to);
                    message.setOperation(operation);
                    Client client = null;
                    try {
                        Document payload = Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(
                                toSubscribe.get(key));
                        message.setPayload(payload);

                        // send the subscribe using WSA stuff
                        client = ClientFactoryRegistry.getFactory().getClient(wsaEP);
                        if (client != null) {
                            client.fireAndForget(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (client != null) {
                            /*
                             * try {
                             * ClientFactoryRegistry.getFactory().release(client
                             * ); } catch (ClientException e) {
                             * e.printStackTrace(); }
                             */
                        }
                    }

                } else {
                    if (log.isInfoEnabled()) {
                        log.info(String.format("No address found to send subscribe", ""));
                    }
                }
            }
        }
    }
}
