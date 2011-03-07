package org.ow2.petals.esb.kernel.api.endpoint;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.transport.Stub;

@Interface(name="service")
public interface ClientEndpoint extends Endpoint, Stub {

	Stub getStub();
	
	void setStub(Stub stub);
	
	void setQName(QName name);
}
