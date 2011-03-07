package org.ow2.petals.base.fractal.api;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotations.Interface;

@Interface(name="service")
public interface FractalComponent {

	void createFractalComponent() throws FractalException;
	
	// TODO: Fix bug => method not necessary
	void initFractalComponent(Component component);

	void startFractalComponent() throws FractalException;

	void stopFractalComponent() throws FractalException;

	void destroyFractalComponent() throws FractalException;

	String getName();

	void setName(String name) throws FractalException;

	Component getComponent();
}
