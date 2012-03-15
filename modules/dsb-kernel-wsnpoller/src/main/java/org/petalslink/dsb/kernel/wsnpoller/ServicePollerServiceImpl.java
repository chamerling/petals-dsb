/**
 * 
 */
package org.petalslink.dsb.kernel.wsnpoller;

import java.text.ParseException;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.servicepoller.api.CronExpressionValidator;
import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.servicepoller.api.WSNPoller;
import org.petalslink.dsb.servicepoller.api.WSNPollerService;
import org.petalslink.dsb.servicepoller.api.WSNPollerServiceInformation;

/**
 * This is the exposed service for polling external services and sending WSN
 * Notify messages. By calling start, it needs to deploy things on the current
 * DSB node (for now JBI artefacts...)
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WSNPollerService.class) })
public class ServicePollerServiceImpl implements WSNPollerService {

    private WSNPollerService adapter;

    @Requires(name = "servicepoller-manager", signature = WSNPoller.class)
    private WSNPoller manager;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(logger);
        log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        log.start();
    }

    /**
     * 
     */
    private synchronized WSNPollerService getAdapter() {
        if (adapter == null) {
            adapter = new ServicePollerServiceAdapter(manager);
        }
        return adapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(java.lang
     * .String)
     */
    public boolean stop(String id) throws ServicePollerException {
        return this.getAdapter().stop(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#pause(java.
     * lang.String)
     */
    public boolean pause(String id) throws ServicePollerException {
        return this.getAdapter().pause(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#resume(java
     * .lang.String)
     */
    public boolean resume(String id) throws ServicePollerException {
        return this.getAdapter().resume(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.wsnpoller.WSNPollerService#start(org.petalslink
     * .dsb.servicepoller.api.ServicePollerInformation,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler, java.lang.String,
     * org.petalslink.dsb.servicepoller.api.ServicePollerInformation,
     * javax.xml.namespace.QName)
     */
    public String start(ServicePollerInformation toPoll, DocumentHandler inputMessage,
            String cronExpression, ServicePollerInformation replyTo, String topicName,
            String topicURI, String topicPrefix) throws ServicePollerException {
        try {
            CronExpressionValidator.validateExpression(cronExpression);
        } catch (ParseException e) {
            throw new ServicePollerException(String.format("Invalid CronExpression %s",
                    cronExpression), e);
        }
        return this.getAdapter().start(toPoll, inputMessage, cronExpression, replyTo, topicName,
                topicURI, topicPrefix);
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.WSNPollerService#getInformation()
     */
    public List<WSNPollerServiceInformation> getInformation() {
        return this.getAdapter().getInformation();
    }

}
