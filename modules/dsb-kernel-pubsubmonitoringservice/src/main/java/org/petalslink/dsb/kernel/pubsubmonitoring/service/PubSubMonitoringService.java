/**
 * 
 */
package org.petalslink.dsb.kernel.pubsubmonitoring.service;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.pubsub.service.NotificationCenter;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.petalslink.dsb.ws.api.DSBWebServiceException;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = org.petalslink.dsb.ws.api.PubSubMonitoringService.class) })
public class PubSubMonitoringService implements org.petalslink.dsb.ws.api.PubSubMonitoringService {

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
     * @see
     * org.petalslink.dsb.ws.api.PubSubMonitoringService#subscribe(java.lang
     * .String)
     */
    public String subscribe(String subscriberEndpoint) throws DSBWebServiceException {
        String result = null;
        try {
            @SuppressWarnings("unused")
            SubscribeResponse response = NotificationCenter
                    .get()
                    .getManager()
                    .getNotificationProducerEngine()
                    .subscribe(
                            NotificationHelper.createSubscribe(subscriberEndpoint,
                                    Constants.MONITORING_TOPIC));
            result = "registered/TODO";
        } catch (Exception e) {
            throw new DSBWebServiceException(e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.PubSubMonitoringService#unsubscribe(java.lang
     * .String)
     */
    public boolean unsubscribe(String subscriptionID) throws DSBWebServiceException {
        throw new DSBWebServiceException("Not implemented");
    }

}
