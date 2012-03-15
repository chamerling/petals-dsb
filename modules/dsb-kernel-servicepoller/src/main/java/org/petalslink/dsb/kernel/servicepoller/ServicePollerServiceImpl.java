/**
 * 
 */
package org.petalslink.dsb.kernel.servicepoller;

import java.text.ParseException;

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
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.servicepoller.api.ServicePollerService;

/**
 * This is the exposed service for polling external services. By calling start,
 * it needs to deploy things on the current DSB node (for now JBI artefacts...)
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServicePollerService.class) })
public class ServicePollerServiceImpl implements ServicePollerService {

    private org.petalslink.dsb.servicepoller.api.ServicePollerService adapter;

    @Requires(name = "servicepoller-manager", signature = ServicePoller.class)
    private ServicePoller manager;
    
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

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.servicepoller.api.ServicePollerService#start(org.
     * petalslink.dsb.servicepoller.api.ServicePollerInformation,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler, java.lang.String,
     * org.petalslink.dsb.servicepoller.api.ServicePollerInformation)
     */
    public String start(ServicePollerInformation toPoll, DocumentHandler inputMessage,
            String cronExpression, ServicePollerInformation replyTo) throws ServicePollerException {
        try {
            CronExpressionValidator.validateExpression(cronExpression);
        } catch (ParseException e) {
            throw new ServicePollerException(String.format("Invalid CronExpression %s", cronExpression), e);
        }
        return this.getAdapter().start(toPoll, inputMessage, cronExpression, replyTo);
    }

    /**
     * 
     */
    private synchronized ServicePollerService getAdapter() {
        if (adapter == null) {
            adapter = new org.petalslink.dsb.servicepoller.api.ServicePollerServiceAdapter(manager);
        }
        return adapter;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(java.lang.String)
     */
    public boolean stop(String id) throws ServicePollerException {
        return this.getAdapter().stop(id);
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePollerService#pause(java.lang.String)
     */
    public boolean pause(String id) throws ServicePollerException {
        return this.getAdapter().pause(id);
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.ServicePollerService#resume(java.lang.String)
     */
    public boolean resume(String id) throws ServicePollerException {
        return this.getAdapter().resume(id);
    }

}
