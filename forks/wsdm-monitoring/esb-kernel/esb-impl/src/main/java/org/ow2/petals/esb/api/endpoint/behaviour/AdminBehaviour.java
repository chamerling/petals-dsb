package org.ow2.petals.esb.api.endpoint.behaviour;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;

public interface AdminBehaviour extends Behaviour<Object> {

	String createComponent(QName componentName, String fractalItfName, String classComponentName);
	
	
	String createClient(QName componentName, QName clientName, String fractalClientItfName, String classClientName);


	String createProvider(QName componentName, QName providerName, String fractalProviderItfName, String classProviderName);


	String createClientAndProvider(QName componentName, QName clientAndProviderName, String fractalClientAndProviderInterfaceName, String classClientAndProviderName,String fractalClientItfName, String classClientName, String fractalProviderItfName, String classProviderName);

	
	String createService(QName providerName, QName serviceName, String fractalServiceItfName, String classServiceName);
	
	
	String createClientEndpoint(QName clientName, QName clientEndpointName, String fractalClientEndpointItfName, String classClientEndpointName, String classClientEndpointBehaviourName);
	
	
	String createProviderEndpoint(QName serviceName, String providerEndpointName, String fractalProviderEndpointItfName, String classProviderEndpointName, String classProviderEndpointBehaviourName, URI wsdl);

	
	String addSoapListener(QName clientEndpointName);
	
	
	String exposeInSoapOnClient(QName serviceName, String providerEndpointName, QName clientName);

	
	String createServiceEndpoint(QName providerName, QName serviceName, String fractalServiceItfName, String classServiceName, String providerEndpointName, String fractalProviderEndpointItfName, String classProviderEndpointName, String classProviderEndpointBehaviourName, URI wsdl);

	
	String createServiceEndpointOnSoapOnClient(QName providerName, QName serviceName, String fractalServiceItfName, String classServiceName, String providerEndpointName, String fractalProviderEndpointItfName, String classProviderEndpointName, String classProviderEndpointBehaviourName, URI wsdl, QName clientName);

	
	String importSoapEndpoint(QName providerName, String soapAddress, URI wsdl);
	
	
	String wrapSoapEndpoint(QName providerName, String soapAddress, URI wsdl, List<String> interceptorClassName, QName clientName);
}
