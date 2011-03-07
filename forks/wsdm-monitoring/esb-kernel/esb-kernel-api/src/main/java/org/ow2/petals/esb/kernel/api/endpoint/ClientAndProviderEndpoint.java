package org.ow2.petals.esb.kernel.api.endpoint;

import org.objectweb.fractal.fraclet.annotations.Interface;

@Interface(name="service")
public interface ClientAndProviderEndpoint extends ClientEndpoint, ProviderEndpoint {

}
