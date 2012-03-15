/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Client.class) })
public class InternalClient implements Client {

    public static final String UNIQUEID = "org.petalslink.dsb.clientUUID";

    private static Client instance;

    /**
     * @return the instance
     */
    public static Client getInstance() {
        return instance;
    }

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "registry", signature = NotificationConsumerRegistry.class)
    protected NotificationConsumerRegistry registry;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        instance = this;
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#fireAndForget(org.petalslink
     * .dsb.service.client.Message)
     */
    public void fireAndForget(Message message) throws ClientException {
        // get the ID from a property...
        String id = message.getProperty(UNIQUEID);
        InternalNotificationConsumer consumer = null;
        if (id != null) {
            consumer = this.registry.get(id);
        }

        if (consumer == null) {
            throw new ClientException(
                    "Can not find any valid consumer for the current notification...");
        }

        // create the notify
        try {
            consumer.notify(Wsnb4ServUtils.getWsnbReader().readNotify(message.getPayload()));
        } catch (WsnbException e) {
            throw new ClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendReceive(org.petalslink.dsb
     * .service.client.Message)
     */
    public Message sendReceive(Message message) throws ClientException {
        throw new ClientException("sendReceive is not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendAsync(org.petalslink.dsb
     * .service.client.Message,
     * org.petalslink.dsb.service.client.MessageListener)
     */
    public void sendAsync(Message message, MessageListener listener) throws ClientException {
        throw new ClientException("sendAsync is not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.service.client.Client#getName()
     */
    public String getName() {
        return "JavaInternalClient";
    }

}
