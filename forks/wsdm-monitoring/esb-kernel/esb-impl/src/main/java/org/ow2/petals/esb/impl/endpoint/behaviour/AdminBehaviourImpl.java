package org.ow2.petals.esb.impl.endpoint.behaviour;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlReader;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.api.endpoint.behaviour.AdminBehaviour;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPListenerImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPSenderImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;
import org.ow2.petals.esb.kernel.impl.service.ServiceImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.w3c.dom.Document;

public class AdminBehaviourImpl extends AbstractBehaviourImpl<Object> implements AdminBehaviour {

	private Logger log = Logger.getLogger(AdminBehaviourImpl.class.getName());

	private static WSDL4ComplexWsdlReader reader = null;
	
	private DocumentBuilderFactory documentFactory = null;

	public AdminBehaviourImpl(Endpoint ep) {
		super(ep);
		this.documentFactory = DocumentBuilderFactory.newInstance();
		this.documentFactory.setNamespaceAware(true);
	}
	
	static {
		try {
			reader = getReader();
		} catch (WSDLException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Description getDescription() {
		if(super.getDescription() == null) {
			try {
				URL wsdl = Thread.currentThread().getContextClassLoader().getResource("wsdl/Admin.wsdl");
				Description desc = this.getReader().read(wsdl);
				super.setDescription(desc);
			} catch (WSDLException e) {
				// do nothing
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// do nothing
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// do nothing
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		return super.getDescription();
	}

	public Object marshall(Document document) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public Document unmarshall(Object object) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public static WSDL4ComplexWsdlReader getReader() throws WSDLException {
		if(reader == null) {
			reader = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader();
		}
		return reader;
	}

	public void execute(Exchange exchange) throws BusinessException {
		try {
			if(exchange.getIn().getBody().getContent() == null) {
				throw new BusinessException("the message in cannot be null");
			}

			// convert dom to jdom
			DOMBuilder builder = new DOMBuilder();
			org.jdom.Document doc = builder.build(exchange.getIn().getBody().getContent());

			if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createComponent", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				System.out.println("CREATE COMPONENT METHOD");
				Element createComponent = doc.getRootElement().getChild("createComponent", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName componentName = QName.valueOf(createComponent.getChild("componentName").getValue());
				String fractalItfName = createComponent.getChild("fractalItfName").getValue();
				String classComponentName = createComponent.getChild("classComponentName").getValue();
				String res = this.createComponent(componentName, fractalItfName, classComponentName);

				Document docResp = createResponseFromQName(res, "createComponentResponse", "componentName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createClient", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				System.out.println("CREATE CLIENT METHOD");
				Element createComponent = doc.getRootElement().getChild("createClient", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName componentName = QName.valueOf(createComponent.getChild("componentName").getValue());
				QName clientName = QName.valueOf(createComponent.getChild("clientName").getValue());
				String fractalClientItfName = createComponent.getChild("fractalClientItfName").getValue();
				String classClientName = createComponent.getChild("classClientName").getValue();
				String res = this.createClient(componentName, clientName, fractalClientItfName, classClientName);

				Document docResp = createResponseFromQName(res, "createClientResponse", "clientName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createProvider", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE PROVIDER METHOD");
				Element createComponent = doc.getRootElement().getChild("createProvider", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName componentName = QName.valueOf(createComponent.getChild("componentName").getValue());
				QName providerName = QName.valueOf(createComponent.getChild("providerName").getValue());
				String fractalProviderItfName = createComponent.getChild("fractalProviderItfName").getValue();
				String classProviderName = createComponent.getChild("classProviderName").getValue();
				String res = this.createProvider(componentName, providerName, fractalProviderItfName, classProviderName);

				Document docResp = createResponseFromQName(res, "createProviderResponse", "providerName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createClientAndProvider", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE CLIENT AND PROVIDER METHOD");
				Element createComponent = doc.getRootElement().getChild("createClientAndProvider", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName componentName = QName.valueOf(createComponent.getChild("componentName").getValue());
				QName clientAndProviderName = QName.valueOf(createComponent.getChild("clientAndProviderName").getValue());
				String fractalClientAndProviderInterfaceName = createComponent.getChild("fractalClientAndProviderInterfaceName").getValue();
				String classClientAndProviderName = createComponent.getChild("classClientAndProviderName").getValue();
				String fractalClientItfName = createComponent.getChild("fractalClientItfName").getValue();
				String classClientName = createComponent.getChild("classClientName").getValue();
				String fractalProviderItfName = createComponent.getChild("fractalProviderItfName").getValue();
				String classProviderName = createComponent.getChild("classProviderName").getValue();

				String res = this.createClientAndProvider(componentName, clientAndProviderName, fractalClientAndProviderInterfaceName, classClientAndProviderName, fractalClientItfName, classClientName, fractalProviderItfName, classProviderName);

				Document docResp = createResponseFromQName(res, "createClientAndProviderResponse", "clientAndProviderName");
				exchange.getOut().getBody().setContent(docResp);

			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createService", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE SERVICE METHOD");
				Element createComponent = doc.getRootElement().getChild("createService", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName providerName = QName.valueOf(createComponent.getChild("providerName").getValue());
				QName serviceName = QName.valueOf(createComponent.getChild("serviceName").getValue());
				String fractalServiceItfName = createComponent.getChild("fractalServiceItfName").getValue();
				String classServiceName = createComponent.getChild("classServiceName").getValue();
				String res = this.createService(providerName, serviceName, fractalServiceItfName, classServiceName);

				Document docResp = createResponseFromQName(res, "createServiceResponse", "serviceName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createClientEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE CLIENT ENDPOINT METHOD");
				Element createComponent = doc.getRootElement().getChild("createClientEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName clientName = QName.valueOf(createComponent.getChild("clientName").getValue());
				QName clientEndpointName = QName.valueOf(createComponent.getChild("clientEndpointName").getValue());
				String fractalClientEndpointItfName = createComponent.getChild("fractalClientEndpointItfName").getValue();
				String classClientEndpointName = createComponent.getChild("classClientEndpointName").getValue();
				String classClientEndpointBehaviourName = createComponent.getChild("classClientEndpointBehaviourName").getValue();
				String res = this.createClientEndpoint(clientName, clientEndpointName, fractalClientEndpointItfName, classClientEndpointName, classClientEndpointBehaviourName);

				Document docResp = createResponseFromQName(res, "createClientEndpointResponse", "clientEndpointName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createProviderEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE PROVIDER ENDPOINT METHOD");
				Element createComponent = doc.getRootElement().getChild("createProviderEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName serviceName = QName.valueOf(createComponent.getChild("serviceName").getValue());
				String providerEndpointName = createComponent.getChild("providerEndpointName").getValue();
				String fractalProviderEndpointItfName = createComponent.getChild("fractalProviderEndpointItfName").getValue();
				String classProviderEndpointName = createComponent.getChild("classProviderEndpointName").getValue();
				String classProviderEndpointBehaviourName = createComponent.getChild("classProviderEndpointBehaviourName").getValue();
				URI wsdl = new URI(createComponent.getChild("wsdl").getValue());

				String res = this.createProviderEndpoint(serviceName, providerEndpointName, fractalProviderEndpointItfName, classProviderEndpointName, classProviderEndpointBehaviourName, wsdl);

				Document docResp = createResponseFromQName(res, "createProviderEndpointResponse", "providerEndpointName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("addSoapListener", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("ADD SOAP LISTENER METHOD");
				Element createComponent = doc.getRootElement().getChild("addSoapListener", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName clientEndpointName = QName.valueOf(createComponent.getChild("clientEndpointName").getValue());
				String res = this.addSoapListener(clientEndpointName);

				Document docResp = createResponseFromQName(res, "addSoapListenerResponse", "soapAddress");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("exposeInSoapOnClient", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("EXPOSE IN SOAP ON CLIENT METHOD");
				Element createComponent = doc.getRootElement().getChild("exposeInSoapOnClient", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName serviceName = QName.valueOf(createComponent.getChild("serviceName").getValue());
				String providerEndpointName = createComponent.getChild("providerEndpointName").getValue();
				QName clientName = QName.valueOf(createComponent.getChild("clientName").getValue());
				String res = this.exposeInSoapOnClient(serviceName, providerEndpointName, clientName);

				Document docResp = createResponseFromQName(res, "exposeInSoapOnClientResponse", "soapAddress");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createServiceEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE SERVICE ENDPOINT METHOD");
				Element createComponent = doc.getRootElement().getChild("createServiceEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName providerName = QName.valueOf(createComponent.getChild("providerName").getValue());
				QName serviceName = QName.valueOf(createComponent.getChild("serviceName").getValue());
				String fractalServiceItfName = createComponent.getChild("fractalServiceItfName").getValue();
				String classServiceName = createComponent.getChild("classServiceName").getValue();
				String providerEndpointName = createComponent.getChild("providerEndpointName").getValue();
				String fractalProviderEndpointItfName = createComponent.getChild("fractalProviderEndpointItfName").getValue();
				String classProviderEndpointName = createComponent.getChild("classProviderEndpointName").getValue();
				String classProviderEndpointBehaviourName = createComponent.getChild("classProviderEndpointBehaviourName").getValue();
				URI wsdl = new URI(createComponent.getChild("wsdl").getValue());
				String res = this.createServiceEndpoint(providerName, serviceName, fractalServiceItfName, classServiceName, providerEndpointName, fractalProviderEndpointItfName, classProviderEndpointName, classProviderEndpointBehaviourName, wsdl);

				Document docResp = createResponseFromQName(res, "createServiceEndpointResponse", "endpointName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("createServiceEndpointOnSoapClient", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("CREATE SERVICE ENDPOINT ON SOAP CLIENT METHOD");
				Element createComponent = doc.getRootElement().getChild("createServiceEndpointOnSoapClient", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName providerName = ( createComponent.getChild("providerName") != null ? QName.valueOf(createComponent.getChild("providerName").getValue()) : null);
				QName serviceName = QName.valueOf(createComponent.getChild("serviceName").getValue());
				String fractalServiceItfName = ( createComponent.getChild("fractalServiceItfName") != null ? createComponent.getChild("fractalServiceItfName").getValue() : null);
				String classServiceName = ( createComponent.getChild("classServiceName") != null ? createComponent.getChild("classServiceName").getValue() : null);
				String providerEndpointName = createComponent.getChild("providerEndpointName").getValue();
				String fractalProviderEndpointItfName = ( createComponent.getChild("fractalProviderEndpointItfName") != null ? createComponent.getChild("fractalProviderEndpointItfName").getValue() : null); 
				String classProviderEndpointName = ( createComponent.getChild("classProviderEndpointName") != null ? createComponent.getChild("classProviderEndpointName").getValue() : null);
				String classProviderEndpointBehaviourName = createComponent.getChild("classProviderEndpointBehaviourName").getValue();
				URI wsdl = new URI(createComponent.getChild("wsdl").getValue());
				QName clientName = ( createComponent.getChild("clientName") != null ? QName.valueOf(createComponent.getChild("clientName").getValue()) : null); 
				String res = this.createServiceEndpointOnSoapOnClient(providerName, serviceName, fractalServiceItfName, classServiceName, providerEndpointName, fractalProviderEndpointItfName, classProviderEndpointName, classProviderEndpointBehaviourName, wsdl, clientName);

				Document docResp = createResponseFromQName(res, "createServiceEndpointOnSoapClientResponse", "soapAddress");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("importSoapEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("IMPORT SOAP ENDPOINT METHOD");
				Element createComponent = doc.getRootElement().getChild("importSoapEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName providerName = ( createComponent.getChild("providerName") != null ? QName.valueOf(createComponent.getChild("providerName").getValue()) : null);
				String soapAddress = createComponent.getChild("soapAddress").getValue();
				URI wsdl = new URI(createComponent.getChild("wsdl").getValue());
				String res = this.importSoapEndpoint(providerName, soapAddress, wsdl);

				Document docResp = createResponseFromQName(res, "importSoapEndpointResponse", "endpointName");
				exchange.getOut().getBody().setContent(docResp);
			} else if(doc.getRootElement() != null &&
					doc.getRootElement().getChild("wrapSoapEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null) {
				log.info("ADD PROXY SOAP ENDPOINT METHOD");
				Element createComponent = doc.getRootElement().getChild("wrapSoapEndpoint", Namespace.getNamespace("http://ow2.petals.org/Admin/"));
				QName providerName = ( createComponent.getChild("providerName") != null ? QName.valueOf(createComponent.getChild("providerName").getValue()) : null);
				String soapAddress = createComponent.getChild("soapAddress").getValue();
				URI wsdl = new URI(createComponent.getChild("wsdl").getValue());
				QName clientName = ( createComponent.getChild("clientName") != null ? QName.valueOf(createComponent.getChild("clientName").getValue()) : null); 
				List<String> interceptorClassName = null;
				String res = this.wrapSoapEndpoint(providerName, soapAddress, wsdl, interceptorClassName, clientName);

				Document docResp = createResponseFromQName(res, "wrapSoapEndpointResponse", "soapAddress");
				exchange.getOut().getBody().setContent(docResp);
			} 
		} catch (URISyntaxException e) {
			throw new BusinessException(e);
		}
	}

	private Document createResponseFromQName(String res, String messageResponse, String return_)
	throws BusinessException {
		Document docResp = null;
		try {
			// create the document
			docResp = this.documentFactory.newDocumentBuilder().newDocument();
			org.w3c.dom.Element body = docResp.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soapenv");
			org.w3c.dom.Element createComponentResponse = docResp.createElementNS("http://ow2.petals.org/Admin/", messageResponse);
			createComponentResponse.setPrefix("adm");
			body.appendChild(createComponentResponse);

			org.w3c.dom.Element endpoint = docResp.createElement(return_);
			endpoint.setTextContent(res.toString());
			createComponentResponse.appendChild(endpoint);

			docResp.appendChild(body);
		} catch (ParserConfigurationException e) {
			throw new BusinessException(e);
		}
		return docResp;
	}


	public String createComponent(QName componentName, String fractalItfName,
			String classComponentName) {
		String res = null;
		Class<? extends ComponentImpl> clazz;
		try {
			clazz = (Class<? extends ComponentImpl>) Class.forName(classComponentName);
			Component c = this.getEndpoint().getNode().createComponent(componentName, fractalItfName, clazz);

			res = componentName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}

	public String createClient(QName componentName, QName clientName,
			String fractalClientItfName, String classClientName) {
		String res = null;
		Class<? extends ClientImpl> clazz;
		try {
			clazz = (Class<? extends ClientImpl>) Class.forName(classClientName);
			Component c = (Component) this.getEndpoint().getNode().getRegistry().getEndpoint(componentName);
			if(c == null) {
				throw new ESBException("Impossible to find component " + c.getQName() + " in registry");
			}
			Client client = (Client) c.createClient(clientName, fractalClientItfName, clazz);		
			res = clientName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}

	public String createProvider(QName componentName, QName providerName, 
			String fractalProviderItfName, String classProviderName) {
		String res = null;
		Class<? extends ProviderImpl> clazz;
		try {
			clazz = (Class<? extends ProviderImpl>) Class.forName(classProviderName);
			Component c = (Component) this.getEndpoint().getNode().getRegistry().getEndpoint(componentName);
			if(c == null) {
				throw new ESBException("Impossible to find component " + c.getQName() + " in registry");
			}
			Provider client = (Provider) c.createProvider(providerName, fractalProviderItfName, clazz);		
			res = providerName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}


	public String createClientAndProvider(QName componentName,
			QName clientAndProviderName, String fractalClientAndProviderInterfaceName, String classClientAndProviderName, String fractalClientItfName,
			String classClientName, String fractalProviderItfName,
			String classProviderName) {
		String res = null;
		Class<? extends ClientAndProviderImpl> clazzClientAndProviderName;
		Class<? extends ClientImpl> clazzClientName;
		Class<? extends ProviderImpl> clazzProviderName;
		try {
			clazzClientAndProviderName = (Class<? extends ClientAndProviderImpl>) Class.forName(classClientAndProviderName);
			clazzClientName = (Class<? extends ClientImpl>) Class.forName(classClientName);
			clazzProviderName = (Class<? extends ProviderImpl>) Class.forName(classProviderName);
			Component c = (Component) this.getEndpoint().getNode().getRegistry().getEndpoint(componentName);
			if(c == null) {
				throw new ESBException("Impossible to find component " + c.getQName() + " in registry");
			}
			ClientAndProvider client = (ClientAndProvider) c.createClientAndProvider(clientAndProviderName, fractalClientAndProviderInterfaceName, clazzClientAndProviderName, fractalClientItfName,
					clazzClientName, fractalProviderItfName, clazzProviderName);		
			res = clientAndProviderName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}

	public String createService(QName providerName,
			QName serviceName, String fractalServiceItfName,
			String classServiceName) {
		String res = null;
		Class<? extends ServiceImpl> clazz;
		try {
			if(classServiceName != null) {
				clazz = (Class<? extends ServiceImpl>) Class.forName(classServiceName);
			} else {
				// default service class
				clazz = ServiceImpl.class;
			}

			Provider p = null;
			if(providerName != null) {
				p = (Provider) this.getEndpoint().getNode().getRegistry().getEndpoint(providerName);
				if(p == null) {
					throw new ESBException("Impossible to find provider " + p.getQName() + " in registry");
				}
			}

			// find if service exist
			Service service = (Service) this.getEndpoint().getNode().getRegistry().getEndpoint(serviceName);

			if(service == null) { 
				if(providerName == null) {
					// get default provider
					p = (Provider) this.getEndpoint().getNode().getRegistry().getEndpoint(new QName(this.getEndpoint().getNode().getQName().getNamespaceURI(), "adminProvider"));
				}
				if(fractalServiceItfName != null) {
					service = (Service) p.createService(serviceName, fractalServiceItfName, clazz);		
				} else {
					// default fractal interface of service
					service = (Service) p.createService(serviceName, "service", clazz);		
				}
			}
			res = serviceName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}

	public String createClientEndpoint(QName clientName,
			QName clientEndpointName, String fractalClientEndpointItfName,
			String classClientEndpointName, String classClientEndpointBehaviourName) {
		String res = null;
		Class<? extends ClientEndpointImpl> clazz;
		try {
			if(classClientEndpointName != null) {
				clazz = (Class<? extends ClientEndpointImpl>) Class.forName(classClientEndpointName);
			} else {
				clazz = ClientEndpointImpl.class;
			}
			Client c = null;
			if(clientName != null) {
				c = (Client) this.getEndpoint().getNode().getRegistry().getEndpoint(clientName);
			} else {
				// default client
				c =  (Client) this.getEndpoint().getNode().getRegistry().getEndpoint(new QName(this.getEndpoint().getNode().getQName().getNamespaceURI(), "adminClient"));
			}
			if(c == null) {
				throw new ESBException("Impossible to find client " + clientName + " in registry");
			}
			ClientEndpoint client = (ClientEndpoint) c.createClientEndpoint(clientEndpointName, fractalClientEndpointItfName, clazz, new EndpointInitialContextImpl(5));		

			// add behaviour
			if(classClientEndpointBehaviourName != null && classClientEndpointBehaviourName.trim().length() > 0) {
				Class<? extends Behaviour<?>> clazzBehaviour = (Class<? extends Behaviour<?>>) Class.forName(classClientEndpointBehaviourName);
				client.setBehaviourClass(clazzBehaviour);
			}

			res = clientEndpointName.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}

		return res;
	}

	public String createProviderEndpoint(QName serviceName,
			String providerEndpointName, String fractalProviderEndpointItfName,
			String classProviderEndpointName, String classProviderEndpointBehaviourName, URI wsdl) {
		String res = null;
		Class<? extends ProviderEndpointImpl> clazz;
		try {
			if(classProviderEndpointName != null) {
				clazz = (Class<? extends ProviderEndpointImpl>) Class.forName(classProviderEndpointName);
			} else {
				// default provider endpoint class
				clazz = ProviderEndpointImpl.class;
			}
			Service s = (Service) this.getEndpoint().getNode().getRegistry().getEndpoint(serviceName);
			if(s == null) {
				throw new ESBException("Impossible to find service " + serviceName + " in registry");
			}


			ProviderEndpoint providerEndpoint = null;

			if(fractalProviderEndpointItfName != null) {
				providerEndpoint = (ProviderEndpoint) s.createProviderEndpoint(providerEndpointName, fractalProviderEndpointItfName, clazz, new EndpointInitialContextImpl(5));		
			} else {
				providerEndpoint = (ProviderEndpoint) s.createProviderEndpoint(providerEndpointName, "service", clazz, new EndpointInitialContextImpl(5));		
			}

			// add behaviour
			if(classProviderEndpointBehaviourName != null && classProviderEndpointBehaviourName.trim().length() > 0) {
				Class<? extends Behaviour<?>> clazzBehaviour = (Class<? extends Behaviour<?>>) Class.forName(classProviderEndpointBehaviourName);
				providerEndpoint.setBehaviourClass(clazzBehaviour);
				providerEndpoint.getBehaviour().setDescription(this.getReader().read(wsdl.toURL()));
			}

			res = providerEndpoint.getQName().toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (WSDLException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} 

		return res;
	}

	public String addSoapListener(QName clientEndpointName) {
		String res = null;
		try {
			ClientEndpoint ce = (ClientEndpoint) this.getEndpoint().getNode().getRegistry().getEndpoint(clientEndpointName);
			if(!(ce instanceof ClientProxyEndpoint)) {
				throw new ESBException("Impossible to add listener on non-client proxy endpoint: " + clientEndpointName);
			}
			ClientProxyEndpoint cpe = (ClientProxyEndpoint)ce;
			SOAPListenerImpl soapListener = new SOAPListenerImpl(cpe); 
			cpe.getExternalListeners().put("SOAP", soapListener);
			res = cpe.getExternalAddress();
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		return res;
	}

	public String exposeInSoapOnClient(QName serviceName, String providerEndpointName, QName clientName) {
		String res = null;
		try {
			ProviderEndpoint pe = (ProviderEndpoint) this.getEndpoint().getNode().getRegistry().getEndpoint(new QName(serviceName.getNamespaceURI(), providerEndpointName));
			
			if(!(pe instanceof ProviderEndpoint)) {
				throw new ESBException("Impossible to find provider endpoint: " + providerEndpointName + " on service " + serviceName);
			}

			String ceName = this.createClientEndpoint(clientName, new QName(serviceName.getNamespaceURI(), providerEndpointName + "ClientProxyEndpoint"),"proxy-client-service", "org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl", "org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl");

			
			// find proxy client endpoint
			ClientProxyEndpoint cpe = (ClientProxyEndpoint) this.getEndpoint().getNode().getRegistry().getEndpoint(QName.valueOf(ceName));
			if(!(cpe instanceof ClientProxyEndpoint)) {
				throw new ESBException("Impossible to find proxy client endpoint: " + ceName);
			}
			cpe.setProviderEndpointName(pe.getQName());

			
			res = this.addSoapListener(cpe.getQName());
			
			log.finest("load client description");
			Description desc = cpe.getBehaviour().getDescription();
			
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		return res;
	}

	public String createServiceEndpoint(QName providerName, QName serviceName,
			String fractalServiceItfName, String classServiceName,
			String providerEndpointName, String fractalProviderEndpointItfName,
			String classProviderEndpointName,
			String classProviderEndpointBehaviourName, URI wsdl) {
		String res = null;
		this.createService(providerName, serviceName, fractalServiceItfName, classServiceName);
		res = this.createProviderEndpoint(serviceName, providerEndpointName, fractalProviderEndpointItfName, classProviderEndpointName, classProviderEndpointBehaviourName, wsdl);
		return res;
	}

	public String createServiceEndpointOnSoapOnClient(QName providerName,
			QName serviceName, String fractalServiceItfName,
			String classServiceName, String providerEndpointName,
			String fractalProviderEndpointItfName,
			String classProviderEndpointName,
			String classProviderEndpointBehaviourName, URI wsdl, QName clientName) {
		String res = null;
		this.createServiceEndpoint(providerName, serviceName, fractalServiceItfName, classServiceName, providerEndpointName, fractalProviderEndpointItfName, classProviderEndpointName, classProviderEndpointBehaviourName, wsdl);
		res = this.exposeInSoapOnClient(serviceName, providerEndpointName, clientName);
		return res;
	}

	public String importSoapEndpoint(QName providerName, String soapAddress,
			URI wsdl) {
		String res = null;
		try {
			Description desc = this.getReader().read(wsdl.toURL());

			org.ow2.easywsdl.wsdl.api.Service service = null;
			org.ow2.easywsdl.wsdl.api.Endpoint endpoint = null;
			for(org.ow2.easywsdl.wsdl.api.Service s: desc.getServices()) {
				for(org.ow2.easywsdl.wsdl.api.Endpoint ep: s.getEndpoints()) {
					if(ep.getAddress().equals(soapAddress)) {
						service = s;
						endpoint = ep;
						break;
					}
				}
				if(endpoint != null) {
					break;
				}
			}

			if(endpoint == null) {
				throw new ESBException("Impossible to find endpoint at this description: " + wsdl + " corresponding to this soap address: " + soapAddress);
			}

			String endpointQName = this.createServiceEndpoint(providerName, service.getQName(), null, null, endpoint.getName(), "proxy-provider-service", "org.ow2.petals.esb.kernel.impl.endpoint.ProviderProxyEndpointImpl", "org.ow2.petals.esb.external.protocol.soap.impl.behaviour.proxy.SoapProviderProxyBehaviourImpl", wsdl);
			ProviderEndpoint pe = (ProviderEndpoint) this.getEndpoint().getNode().getRegistry().getEndpoint(QName.valueOf(endpointQName));
			if(!(pe instanceof ProviderProxyEndpoint)) {
				throw new ESBException("Impossible to find provider endpoint: " + endpointQName + " on service " + service.getQName());
			}


			((ProviderProxyEndpoint)pe).setExternalAddress(soapAddress);
			((ProviderProxyEndpoint)pe).setWSDLDescriptionAddress(wsdl);
			((ProviderProxyEndpoint)pe).getExternalSenders().put("soap", new SOAPSenderImpl());

			res = pe.getQName().toString();
		} catch (WSDLException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		return res;
	}


	public String wrapSoapEndpoint(QName providerName,
			String soapAddress, URI wsdl, List<String> interceptorClassName,
			QName clientName) {
		String res = null;
		try {
			String endpointQName = this.importSoapEndpoint(providerName, soapAddress, wsdl);

			// find endpoint
			ProviderEndpoint pe = (ProviderEndpoint) this.getEndpoint().getNode().getRegistry().getEndpoint(QName.valueOf(endpointQName));
			if(!(pe instanceof ProviderEndpoint)) {
				throw new ESBException("Impossible to find provider endpoint: " + QName.valueOf(endpointQName));
			}

			res = this.exposeInSoapOnClient(pe.getService().getQName(), QName.valueOf(endpointQName).getLocalPart(), clientName);
		} catch (ESBException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		return res;
	}

}
