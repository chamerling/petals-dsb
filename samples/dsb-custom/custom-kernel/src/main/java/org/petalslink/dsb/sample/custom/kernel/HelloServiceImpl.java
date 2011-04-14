/**
 * 
 */
package org.petalslink.dsb.sample.custom.kernel;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.sample.custom.api.HelloService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = HelloService.class) })
public class HelloServiceImpl implements HelloService {

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

	@Override
	public String sayHello(String input) {
		return "Hello : " + input;
	}
}
