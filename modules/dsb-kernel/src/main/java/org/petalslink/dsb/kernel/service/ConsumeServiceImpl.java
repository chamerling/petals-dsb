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
import org.petalslink.dsb.ws.api.HelloService;

/**
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ConsumeService.class) })
public class ConsumeServiceImpl implements ConsumeService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    protected ConfigurationService configuration;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        log = new LoggingUtil(logger);
        log.start();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        log.end();
    }

    @LifeCycleListener(phase = Phase.START, priority = 100)
    public void consumeService() {
        //doTestConsume();
    }

    /**
     * 
     */
    protected void doTestConsume() {
        System.out.println("##############################################");
        // only if I am container 0...
        if (configuration.getContainerConfiguration().getName().equals("1")) {
            System.out.println("Callling remote service on container 0...");
            CoreServiceManager manager = new CoreServiceManagerImpl();
            HelloService client = manager.getClient(HelloService.class, "0");
            String out = null;
            try {
                out = client.sayHello("Christophe");
            } catch (PEtALSWebServiceException e) {
                e.printStackTrace();
            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> GOT RESPONSE ON THE CLIENT : " + out);
        } else {
            System.out.println("Not the right container for the client!");
        }
    }

}
