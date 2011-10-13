/**
 * 
 */
package org.petalslink.dsb.kernel.pubsubmonitoring.service;

import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.Constants;
import org.petalslink.dsb.kernel.pubsub.service.DSBNotificationSender;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;

import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;

/**
 * Used to inject property in message to avoid to monitor myself ie monitoring
 * message must not be monitored because it creates infinite loop...
 * 
 * @author chamerling
 * 
 */
public class MonitoringNotificationSender extends DSBNotificationSender {

    /**
     * @param producer
     */
    public MonitoringNotificationSender(NotificationProducerEngine producer) {
        super(producer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.notification.commons.AbstractNotificationSender#
     * getProducerAddress()
     */
    @Override
    protected String getProducerAddress() {
        return "dsb://MonitoringNotifcationSender";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.pubsub.service.DSBNotificationSender#getClient
     * (org.petalslink.dsb.api.ServiceEndpoint)
     */
    @Override
    protected synchronized Client getClient(ServiceEndpoint se) {
        // we create a client which can handle specific needs for
        // monitoring.
        // For example, we ant to add some properties to messages, etc...
        final Client tmp = super.getClient(se);
        Client result = new Client() {

            public Message sendReceive(Message message) throws ClientException {
                message.setProperty(Constants.MESSAGE_SKIP_MONITORING, "true");
                return tmp.sendReceive(message);
            }

            public void sendAsync(Message message, MessageListener listener) throws ClientException {
                message.setProperty(Constants.MESSAGE_SKIP_MONITORING, "true");
                tmp.sendAsync(message, listener);
            }

            public String getName() {
                return tmp.getName();
            }

            public void fireAndForget(Message message) throws ClientException {
                message.setProperty(Constants.MESSAGE_SKIP_MONITORING, "true");
                tmp.fireAndForget(message);
            }
        };
        return result;
    }

}
