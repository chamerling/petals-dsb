/**
 * 
 */
package org.petalslink.dsb.kernel.service;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.api.service.CoreServiceManager;
import org.petalslink.dsb.kernel.api.service.Server;
import org.petalslink.dsb.kernel.api.service.ServiceServer;
import org.petalslink.dsb.ws.api.HelloService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceServer.class) })
public class ServiceServerImpl implements ServiceServer {

    @Requires(name = "configuration", signature = ConfigurationService.class)
    protected ConfigurationService configurationService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Server server;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        this.log = new LoggingUtil(logger);
        log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        log.end();
        if (server != null) {
            server.stop();
        }
    }

    @LifeCycleListener(phase = Phase.START, priority = 102)
    public void expose() {
        //doTestExpose();
    }

    protected void doTestExpose() {

        if (configurationService.getContainerConfiguration().getName().equals("0")) {
            CoreServiceManager manager = new CoreServiceManagerImpl();
            this.server = manager.createService(HelloService.class, new HelloService() {
                public String sayHello(String input) throws PEtALSWebServiceException {
                    System.out.println("SAY HELLO IS INVOKED ON SERVER!!!");
                    return "You said : " + input;
                }
            }, configurationService.getContainerConfiguration().getName());
        } else {
            System.out.println(">>> Server, not the right node");
        }

    }
}
