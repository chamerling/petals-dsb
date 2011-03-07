package org.ow2.petals.esb.kernel.api.component;

import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;
import org.ow2.petals.esb.kernel.api.entity.Provider;

@Interface(name="service")
public interface Component extends Endpoint {

	<P extends Provider> P createProvider(QName name, String fractalInterfaceName, Class<P> providerClassName) throws ESBException;
	
	<C extends Client> C createClient(QName name, String fractalInterfaceName, Class<C> clientClassName) throws ESBException;
	
	<CP extends ClientAndProvider, C extends Client, P extends Provider> CP createClientAndProvider(QName name, String fractalInterfaceName, Class<CP> className, String fractalClientInterfaceName, Class<C> clientClassName, String fractalProviderInterfaceName, Class<P> providerClassName) throws ESBException;
	
	List<Provider> getProviders();
	
	List<Client> getClients();
	
	List<ClientAndProvider> getClientAndProviders();
}
