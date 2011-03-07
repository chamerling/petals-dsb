package org.ow2.petals.base.fractal.impl;

import static org.objectweb.fractal.fraclet.types.Step.CREATE;
import static org.objectweb.fractal.fraclet.types.Step.DESTROY;
import static org.objectweb.fractal.fraclet.types.Step.START;
import static org.objectweb.fractal.fraclet.types.Step.STOP;

import java.util.logging.Logger;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotations.Controller;
import org.objectweb.fractal.fraclet.annotations.Lifecycle;
import org.ow2.petals.base.fractal.api.FractalComponent;
import org.ow2.petals.base.fractal.api.FractalException;

@org.objectweb.fractal.fraclet.annotations.Component
public class FractalComponentImpl implements FractalComponent {

	private static Logger log = Logger.getLogger(FractalComponentImpl.class.getName());
	
    /**
     * Fractal component
     */
    @Controller("component")
    private Component component;
    
    
    /**
     * Start the scope behaviour
     * @throws MonitoringException
     */
    @Lifecycle(step=CREATE)
    public void createFractalComponent() throws FractalException {
    	FractalComponentImpl.log.fine("Fractal component created: " + FractalHelper.getFractalHelper().getName(this.component));
    }

    /**
     * Start the scope behaviour
     * @throws MonitoringException
     */
    @Lifecycle(step=START)
    public void startFractalComponent() throws FractalException {
    	FractalComponentImpl.log.fine("Fractal component started: " + FractalHelper.getFractalHelper().getName(this.component));
    }

    /**
     * Stop the scope behaviour
     * @throws MonitoringException
     */
    @Lifecycle(step=STOP)
    public void stopFractalComponent() throws FractalException {
    	FractalComponentImpl.log.fine("Fractal component stopped: " + FractalHelper.getFractalHelper().getName(this.component));
    }

    @Lifecycle(step=DESTROY)
    public void destroyFractalComponent() throws FractalException {
    	FractalComponentImpl.log.fine("Fractal component destroyed: " + FractalHelper.getFractalHelper().getName(this.component));
    }


    public Component getComponent() {
        return this.component;
    }


    public String getName()  {
    	String name = null;
        try {
			name = FractalHelper.getFractalHelper().getName(this.component);
		} catch (FractalException e) {
			name = e.getMessage();
		}
		return name;
    }

    public void setName(String name) throws FractalException {
        FractalHelper.getFractalHelper().changeName(this.component, name);
    }

	public void initFractalComponent(Component component) {
		this.component = component;
	}



}
