/**
 * 
 */
package org.petalslink.dsb.kernel.tools.service.sample;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;

/**
 * This is just a sample to see if it works within the DSB. To be removed.
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Service.class) })
public class HelloService implements Service {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        System.out.println(String.format(
                "Starting simple hello service provider (will be available at %s)", getURL()));
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getWSDLURL()
     */
    public String getWSDLURL() {
        return "helloservice.wsdl";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getURL()
     */
    public String getURL() {
        return "http://localhost:6789/foo/bar/HelloService";
    }

    public QName getService() {
        return new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService");
    }

    public QName getInterface() {
        return null;
    }

    public QName getEndpoint() {
        return new QName("http://api.ws.dsb.petalslink.org/", "HelloServicePort");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public void invoke(SimpleExchange exchange) throws ServiceException {
        System.out.println("I have been invoked!");
        System.out.println(exchange.getOperation());
    }
}
