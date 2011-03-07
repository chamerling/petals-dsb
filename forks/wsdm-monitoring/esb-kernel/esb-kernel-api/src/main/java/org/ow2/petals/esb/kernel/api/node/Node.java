package org.ow2.petals.esb.kernel.api.node;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.registry.Registry;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;

@Interface(name="service")
public interface Node extends Endpoint {

	Registry createRegistry(String name) throws ESBException ;
	
	TransportersManager createTransportersManager(String name) throws ESBException ;
	
	<C extends Component> C createComponent(QName name, String fractalInterfaceName, Class<C> componentClassName) throws ESBException;
	
	List<Component> getComponents();
	
	Registry getRegistry();
	
	TransportersManager getTransportersManager();
	
	ListenersManager getListenersManager();
	
	Map<QName, Endpoint> getListenedEndpoints();
	
	void setListenedEndpoints(Map<QName, Endpoint> endpoints);
	
	String getHost();
	
	void setHost(String ip);
	
	String getPort();

	void setPort(String port);
}
