package org.ow2.petals.esb.kernel.api.entity;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;

@Interface(name="service")
public interface ClientAndProvider extends Client, Provider {

	
	void setClient(Client client) throws ESBException;
	
	void setProvider(Provider provider) throws ESBException;
	
}
