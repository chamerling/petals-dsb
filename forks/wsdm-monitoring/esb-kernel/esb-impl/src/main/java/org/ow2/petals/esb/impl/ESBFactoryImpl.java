package org.ow2.petals.esb.impl;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.api.ESBFactory;
import org.ow2.petals.esb.api.component.AdminComponent;
import org.ow2.petals.esb.api.endpoint.AdminEndpoint;
import org.ow2.petals.esb.api.entity.AdminProvider;
import org.ow2.petals.esb.api.service.AdminService;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPListenerImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServer;
import org.ow2.petals.esb.impl.component.AdminComponentImpl;
import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.ESBKernelFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;

public class ESBFactoryImpl extends ESBKernelFactoryImpl implements ESBFactory {

	@Override
	public Node createNode(QName name, boolean explorer) throws ESBException {
		return this.createNode(name, AdminBehaviourImpl.class, explorer);
	}

	public Node createNode(QName name,
			Class<? extends AdminBehaviourImpl> adminBehaviourClass,
			boolean explorer) throws ESBException {
		Node node = super.createNode(name, explorer);
		AdminComponent adminComponent = node.createComponent(new QName(name.getNamespaceURI(), "adminComponent"), "adminComponent", AdminComponentImpl.class);
		AdminProvider adminProvider = adminComponent.createAdminProvider();
		AdminService adminService = adminProvider.createAdminService();
		AdminEndpoint adminEndpoint = adminService.createAdminEndpoint();
		if(adminBehaviourClass != null) {
			adminEndpoint.setBehaviourClass(adminBehaviourClass);
			
			// reload the description
			adminEndpoint.getBehaviour().setDescription(null);
			Description desc = adminEndpoint.getBehaviour().getDescription();
		}
		
		Client client = adminComponent.createClient(new QName(adminComponent.getQName().getNamespaceURI(), "adminClient"), "service", ClientImpl.class);
		ClientProxyEndpoint clientEndpoint = client.createClientEndpoint(new QName(adminComponent.getQName().getNamespaceURI(), "adminExternalEndpoint"), "proxy-client-service", ClientProxyEndpointImpl.class, null);
		clientEndpoint.setBehaviourClass(ClientProxyBehaviourImpl.class);
		clientEndpoint.setProviderEndpointName(adminEndpoint.getQName());
		clientEndpoint.setExternalAddress("http://localhost:" + SoapServer.getInstance().getConfig().getPort() + "/services/" + clientEndpoint.getQName().getLocalPart() + "/");
		SOAPListenerImpl soapListener = new SOAPListenerImpl(clientEndpoint); 
		clientEndpoint.getExternalListeners().put("SOAP", soapListener);
		
		return node;
	}

}
