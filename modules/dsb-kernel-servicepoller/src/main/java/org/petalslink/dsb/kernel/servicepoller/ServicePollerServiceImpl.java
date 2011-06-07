/**
 * 
 */
package org.petalslink.dsb.kernel.servicepoller;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
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

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        System.out.println("######----->START SERVICEPOLLERSERVICE");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        System.out.println("######----->STOP SERVICEPOLLERSERVICE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.servicepoller.api.ServicePollerService#start(org.
     * petalslink.dsb.servicepoller.api.ServicePollerInformation,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler, java.lang.String,
     * org.petalslink.dsb.servicepoller.api.ServicePollerInformation)
     */
    public void start(ServicePollerInformation toPoll, DocumentHandler inputMessage,
            String cronExpression, ServicePollerInformation replyTo) throws ServicePollerException {
        this.getAdapter().start(toPoll, inputMessage, cronExpression, replyTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(org.petalslink
     * .dsb.servicepoller.api.ServicePollerInformation,
     * org.petalslink.dsb.servicepoller.api.ServicePollerInformation)
     */
    public void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo)
            throws ServicePollerException {
        this.getAdapter().stop(toPoll, replyTo);
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

}
