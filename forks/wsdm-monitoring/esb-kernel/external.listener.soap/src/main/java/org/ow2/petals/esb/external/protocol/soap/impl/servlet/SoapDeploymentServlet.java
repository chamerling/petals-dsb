/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.esb.external.protocol.soap.impl.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mortbay.jetty.Server;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlWriter;
import org.ow2.easywsdl.schema.util.DOMUtil;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfOperation;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfOperation.MEPPatternConstants;
import org.ow2.easywsdl.wsdl.decorator.DecoratorDescriptionImpl;
import org.ow2.easywsdl.wsdl.impl.wsdl11.DescriptionImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPListenerImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServer;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServerConfig;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.proxy.ClientProxyBehaviour;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.StatusType;

/**
 * A servlet which displays basic information. It replaces the default 404
 * pages.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class SoapDeploymentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger log = Logger.getLogger(SoapDeploymentServlet.class.getName());

	private final SoapServerConfig config;

	private List<SOAPListenerImpl> listeners = new ArrayList<SOAPListenerImpl>();


	private static WSDL4ComplexWsdlWriter writer;

	// private static WSDL4ComplexWsdlReader reader;

	private final DocumentBuilderFactory builder;

	private static final String HTML_TITLE = "<html><head><title>Welcome SOAP Listener</title></head><body>";


	static {
		try {
			writer = getWriter();
	//		reader = getReader();
		} catch (WSDL4ComplexWsdlException e) {
			e.printStackTrace();
		}
	}
	
	public static WSDL4ComplexWsdlWriter getWriter() throws WSDL4ComplexWsdlException {
		if(writer == null) {
			writer = WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter();
		}
		return writer;
	}
	
//	public static WSDL4ComplexWsdlReader getReader() throws WSDL4ComplexWsdlException {
//		if(reader == null) {
//			reader = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader();
//		}
//		return reader;
//	}
	
	public SoapDeploymentServlet(final SoapServerConfig config, final List<SOAPListenerImpl> listeners) throws WSDLException {
		this.config = config;
		this.listeners = listeners;
		this.builder = DocumentBuilderFactory.newInstance();
		this.builder.setNamespaceAware(true);
	}


	public List<SOAPListenerImpl> getListeners() {
		return this.listeners;
	}

	/**
	 * 
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	throws ServletException, IOException {
		String queryString = request.getQueryString();
		this.log.finest("doGet: queryString = " + queryString);
		if (this.isUpperWSDLRequest(queryString)) {
			this.log.finest("Print main WSDL description");
			response.sendRedirect(this.buildRedirect(request));
			return;
		} else if (this.isImportWSDLRequest(queryString)) {
			this.log.finest("Print imported WSDL description");
			this.printImportedDocuments(request, response, queryString);
		} else {
			this.welcomeMessage(response);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.axis2.transport.http.AxisServlet#doPost(javax.servlet.http
	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
	throws ServletException, IOException {
		try {
			String queryString = request.getQueryString();
			this.log.finest("doGet: queryString = " + queryString);
			this.handleRequest(request, response);
		} catch (ESBException e) {
			throw new ServletException(e);
		}  

	}

	private void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ESBException {
		try {

			String buffer = "";
			String line = "";
			while((line = request.getReader().readLine()) != null) {
				buffer = buffer + line;
			}

			System.out.println("BUFFER = " + buffer);

			Document soapRequest = this.builder.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.getBytes()));

			System.out.println("REQUEST = "
					+ XMLPrettyPrinter.prettyPrint(soapRequest));

			String endpointName = request.getRequestURI().replaceFirst("/services/", "");
			String soapAction = null;
			if(request.getHeader("SOAPAction") != null) {
				 soapAction = request.getHeader("SOAPAction").replaceAll("\"", "");
			}

			if (this.log.isLoggable(Level.FINEST)) {
				this.log.finest("soaprequest: \n"
						+ XMLPrettyPrinter.prettyPrint(soapRequest));
				this.log.finest("soapAction = " + soapAction);
				this.log.finest("endpointName = " + endpointName);
			}

			SOAPListenerImpl listener = this.findListener(endpointName);
			if(listener == null) {
				throw new ESBException("Impossible to find listener corresponding to endpoint: " + endpointName); 
			}

			// create exchange
			Exchange exchange = this.createExchangeFromSoapRequest(soapRequest, soapAction, listener);

			this.log.info("receive soap request for internal endpoint: " + listener.getEndpoint().getProviderEndpointName());
			if(exchange.getPattern().equals(PatternType.IN_OUT)) {
				// execute behaviour of endpoint
				if(listener.getEndpoint().getBehaviourClass() != null) {
					this.log.finest("execute behaviour");
					listener.getEndpoint().getBehaviour().execute(exchange);
				} else {
					this.log.finest("execute empty behaviour");
				}

				// send exchange
				//((ClientProxyEndpoint)this.listener.getEndpoint()).sendSync(exchange, 0);

				// create response
				Document soapResponse = this.createSoapResponseFromExchange(exchange);

				// write response
				String msg = XMLPrettyPrinter.prettyPrint(soapResponse);
				this.log.finest("SOAP Response: " + msg);
				this.log.info("soap response sended by: " + exchange.getDestination());
				
				response.setContentType("text/xml; charset=utf-8");
				response.getOutputStream().print(msg);
				response.getOutputStream().flush();

			} else if(exchange.getPattern().equals(PatternType.IN_ONLY)) {
				ExecuteInOnlyExchange exec = new ExecuteInOnlyExchange(listener, exchange);
				exec.start();
			} else {
				throw new ESBException("MEP pattern not accepted for the moment");
			}


		} catch (IOException e) { 
			throw new ESBException(e);
		} catch (ESBException e) {
			throw new ESBException(e);
		} catch (SAXException e) {
			throw new ESBException(e);
		} catch (ParserConfigurationException e) {
			throw new ESBException(e);
		} catch (IllegalArgumentException e) {
			throw new ESBException(e);
		} catch (BusinessException e) {
			throw new ESBException(e);
		}
	}


	private Document createSoapResponseFromExchange(Exchange exchange) throws ESBException {
		Document root = null;
		try {
			root = this.builder.newDocumentBuilder().newDocument();
			Element enveloppe = root.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
			enveloppe.setPrefix("soapenv");
			if((exchange.getOut().getHeader() != null) && (exchange.getOut().getHeader().getContent() != null) && (exchange.getOut().getHeader().getContent().getDocumentElement() != null)) {
				enveloppe.appendChild(root.adoptNode(exchange.getOut().getHeader().getContent().getDocumentElement().cloneNode(true)));
			}
			if((exchange.getOut().getBody() != null) && (exchange.getOut().getBody().getContent() != null) && (exchange.getOut().getBody().getContent().getDocumentElement() != null)) {
				enveloppe.appendChild(root.adoptNode(exchange.getOut().getBody().getContent().getDocumentElement().cloneNode(true)));
			}
			root.appendChild(enveloppe);
		} catch (ParserConfigurationException e) {
			throw new ESBException(e);
		}
		return root;
	}

	private Exchange createExchangeFromSoapRequest(Document soapRequest, String soapAction, SOAPListenerImpl listener) throws ESBException {
		Exchange exchange = null;
		try {
			exchange = listener.getEndpoint().createExchange();
			exchange.setStatus(StatusType.ACTIVE);

			// set the header
			Document header = this.builder.newDocumentBuilder().newDocument();
			NodeList headers = soapRequest.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header");
			if((headers != null)&&(headers.getLength() == 1)) {
				header.appendChild(header.adoptNode(headers.item(0).cloneNode(true)));
			}
			exchange.getIn().getHeader().setContent(header);

			// set the body
			Document body = this.builder.newDocumentBuilder().newDocument();
			NodeList bodies = soapRequest.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			if((bodies != null)&&(bodies.getLength() == 1)) {
				body.appendChild(body.adoptNode(bodies.item(0).cloneNode(true)));
			}
			exchange.getIn().getBody().setContent(body);

			// set the operation
			Description desc = (listener.getEndpoint()).getBehaviour().getDescription();

			AbsItfOperation operation = this.findOperation(soapAction, body, desc);
			if(operation == null) {
				throw new ESBException("Impossible to find operation corresponding to this request : \n" + XMLPrettyPrinter.prettyPrint(body));
			}
			exchange.setOperation(operation.getQName().toString());
			this.log.finest("exchange.getOperation() = " + exchange.getOperation());

			if(operation.getPattern().equals(MEPPatternConstants.IN_ONLY)) {
				exchange.setPattern(PatternType.IN_ONLY);
			} else if(operation.getPattern().equals(MEPPatternConstants.IN_OUT)) {
				exchange.setPattern(PatternType.IN_OUT);
			} else {
				throw new ESBException("Unsupported pattern: " + operation.getPattern());
			}
			this.log.finest("exchange.getPattern() = " + exchange.getPattern());
		} catch (ParserConfigurationException e) {
			throw new ESBException(e);
		} catch (ExchangeException e) {
			throw new ESBException(e);
		}

		return exchange;
	}

	private AbsItfOperation findOperation(String soapAction, Document body, Description desc) {
		AbsItfOperation res = this.findOperationUsingSoapAction(soapAction, desc);
		if(res == null) {
			res = this.findOperationUsingElement(body, desc);
		}
		return res;
	}


	private AbsItfOperation findOperationUsingElement(Document body, Description desc) {
		AbsItfOperation res = null;
		boolean find = false;
		Element elmt = DOMUtil.getFirstElement(body.getDocumentElement());
		if(elmt != null) {
			QName elmtName = new QName(elmt.getNamespaceURI(), org.ow2.easywsdl.wsdl.util.Util.getLocalPartWithoutPrefix(elmt.getNodeName()));
			this.log.finest("find operation corresponding to element: " + elmtName);
			((DescriptionImpl) ((DecoratorDescriptionImpl) desc)
					.getInternalObject()).getMessages();

			for(Binding b: desc.getBindings()) {
				for(BindingOperation op: b.getBindingOperations()) {
					if((op.getOperation().getInput().getElement() != null) 
							&& (op.getOperation().getInput().getElement().getQName() != null)
							&& op.getOperation().getInput().getElement().getQName().getLocalPart().equals(elmtName.getLocalPart())
							&& op.getOperation().getInput().getElement().getQName().getNamespaceURI().equals(elmtName.getNamespaceURI())) {
						res = op.getOperation();
						find = true;
						break;
					}
				}
				if(find) {
					break;
				}
			}
		}
		return res;
	}


	private AbsItfOperation findOperationUsingSoapAction(String soapAction, Description desc) {
		AbsItfOperation res = null;
		boolean find = false;
		for(Binding b: desc.getBindings()) {
			for(BindingOperation op: b.getBindingOperations()) {
				if((op.getSoapAction() != null) && op.getSoapAction().equals(soapAction)) {
					res = op.getOperation();
					find = true;
					break;
				}
			}
			if(find) {
				break;
			}
		}
		return res;
	}


	/**
	 * Get and prints the imported document from WSDL
	 * 
	 * @param request
	 * @param response
	 * @param queryString
	 * @throws AxisFault
	 * @throws IOException
	 * @throws ServletException
	 */
	private void printImportedDocuments(final HttpServletRequest request,
			final HttpServletResponse response, String queryString) throws IOException,
			ServletException {
		try {
			if("wsdl".equals(queryString)){
				String endpointName = request.getRequestURI().replaceFirst("/services/", "");
				SOAPListenerImpl listener = this.findListener(endpointName);
				if(listener == null) {
					throw new ServletException("Impossible to find listener corresponding to endpoint: " + endpointName); 
				}

				final ServletOutputStream out = response.getOutputStream();
				String importsRootURL= "http://" + request.getServerName() 
				+":" + request.getServerPort()+request.getRequestURI()+"?wsdl=";
				this.printWSDL(out, listener, importsRootURL);
				out.flush();
				out.close();
			} else{
				String endpointName = request.getRequestURI().replaceFirst("/services/", "");
				SOAPListenerImpl listener = this.findListener(endpointName);
				if(listener == null) {
					throw new ServletException("Impossible to find listener corresponding to endpoint: " + endpointName); 
				}
				String importsRootURL= "http://" + request.getServerName() 
				+":" + request.getServerPort()+request.getRequestURI()+"?wsdl=";
				Map<URI, Document> importedDocsMap = this.getImportedDocuments(listener, importsRootURL);

			

				//FIXME there are other way to request imported document than ...wsdl=..
				String docFileName = queryString.replace("wsdl=", "");

				boolean find = false;

				for (URI uri : importedDocsMap.keySet()) {
					if (uri.toString().contains(docFileName)) {
						find = true;
						Document importedDoc = importedDocsMap.get(uri);
						final ServletOutputStream out = response.getOutputStream();
						try{
							XMLPrettyPrinter.prettify(importedDoc, out, XMLPrettyPrinter
									.getEncoding(importedDoc));
							out.flush();
							out.close();
						} catch (Exception e) {
							throw new ServletException("Error on " + docFileName
									+ " streaming serialization", e);
						}
					}
				}
				if (!find) {
					throw new ServletException("Error: Document unknown: " + docFileName+". Available documents are "+importedDocsMap.keySet().toString());
				}
			}
		} catch (WSDLException e) {
			throw new ServletException(e);
		}
	}


	private SOAPListenerImpl findListener(String endpointName) {
		SOAPListenerImpl res = null;
		for(SOAPListenerImpl listener: this.listeners) {
			if(listener.getEndpoint().getQName().getLocalPart().equals(endpointName)) {
				res = listener;
				break;
			}
		}
		return res;
	}


	private Map<URI, Document> getImportedDocuments(SOAPListenerImpl listener, String importRootUrl) throws WSDLException {
		Map<URI, Document> res = new HashMap<URI, Document>();
		try {

			//Description desc = listener.getEndpoint().getBehaviour().getDescription();
			//desc = this.cloneDescription(desc);

			// remove imported docs
			//res = desc.deleteImportedDocumentsInWsdl(importRootUrl);

			res = ((ClientProxyBehaviour)listener.getEndpoint().getBehaviour()).getImports();
			if(res != null) {
				listener.getEndpoint().getBehaviour().getDescription();
				res = ((ClientProxyBehaviour)listener.getEndpoint().getBehaviour()).getImports();
			}
		}  catch (ESBException e) {
			throw new WSDLException(e);
		}

		return res;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private boolean isUpperWSDLRequest(String queryString) {
		return ((queryString != null) && ((queryString.indexOf("WSDL") >= 0) || (queryString
				.indexOf("WSDL2") >= 0)));
	}

	/**
	 * 
	 * @param queryString
	 * @return
	 */
	private boolean isImportWSDLRequest(String queryString) {
		return (queryString != null) && (queryString.toLowerCase().startsWith("wsdl"));
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private String buildRedirect(HttpServletRequest request) {
		String result = "http://" + request.getServerName() + ":" + request.getServerPort();
		String query = request.getQueryString();
		int index = query.lastIndexOf("WSDL");
		if (index >= 0) {
			query = query.replaceAll("WSDL", "wsdl");
		}
		return result + request.getRequestURI() + "?" + query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.axis2.description.AxisService#printWSDL(java.io.OutputStream,
	 * java.lang.String, java.lang.String)
	 */
	private void printWSDL(final OutputStream out, SOAPListenerImpl listener, String importRootUrl) throws WSDLException  {
		try {
			Description desc = listener.getEndpoint().getBehaviour().getDescription();
			if(desc == null) {
				throw new WSDLException("Impossible to find description corresponding on behaviour " + listener.getEndpoint().getBehaviour().getClass().getSimpleName() + " on endpoint " + listener.getEndpoint().getQName());
			}
			this.log.fine("Print WSDL: " + desc.getQName());

			for(Service serv: desc.getServices()) {
				for(Endpoint ep: serv.getEndpoints()) {
					ep.setAddress("http://localhost:" + SoapServer.getInstance().getConfig().getPort() + "/services/" + listener.getEndpoint().getQName().getLocalPart());
				}
			}

			Document doc = getWriter().getDocument(desc);
			if (doc == null) {
				this.printWSDLError(out,
						"WSDL description can not been retrieved from endpoint" + listener.getEndpoint().getQName());
			} else {
				XMLPrettyPrinter.prettify(doc, out, XMLPrettyPrinter.getEncoding(doc));
			}
		} catch (Exception e) {
			throw new WSDLException(e);
		}
	}




	/**
	 * Print the WSDL error on the output stream
	 * 
	 * @param out
	 * @throws AxisFault
	 */
	private void printWSDLError(final OutputStream out, final String message) {
		final String wsdlntfound = "<error><description>Unable to get WSDL for this service</description><reason>"
			+ message + "</reason></error>";
		this.printMessage(out, wsdlntfound);
	}

	/**
	 * Print a message to the output stream
	 * 
	 * @param out
	 * @param str
	 * @throws AxisFault
	 */
	private void printMessage(final OutputStream out, final String str) {
		try {
			out.write(str.getBytes());
			out.flush();
		} catch (final IOException e) {
		} finally {
			try {
				out.close();
			} catch (final IOException e) {
				// Do nothing
			}
		}
	}

	private void welcomeMessage(final HttpServletResponse resp)
	throws IOException {
		final ServletOutputStream out = resp.getOutputStream();
		out.write(HTML_TITLE.getBytes());
		out.write("<h1>SOAP Listener</h1>".getBytes());

		out.write("<h2>Web Services information</h2>".getBytes());
		out.write("<ul>".getBytes());
		out.write("<li>Services List : ".getBytes());
		out.write("<ul>".getBytes());
		for(SOAPListenerImpl listener: this.listeners) {

			String path = this.config.getServicesContext() + "/" + listener.getEndpoint().getQName().getLocalPart() + "?wsdl";
			out.write("<li>".getBytes());
			String link = "<a href='" + path + "'>" + listener.getEndpoint().getQName().getLocalPart().trim() + "</a>";
			out.write("</li>".getBytes());
			out.write(link.getBytes());
		}
		out.write("</ul>".getBytes());
		out.write("</li>".getBytes());
		out.write("</ul>".getBytes());

		out.write("<h2>Server Configuration</h2>".getBytes());
		out.write("<ul>".getBytes());
		out.write(("<li>Host :                " + this.config.getHost() + "</li>").getBytes());
		out.write(("<li>Port :                " + this.config.getPort() + "</li>").getBytes());
		out.write(("<li>Jetty Acceptors :     " + this.config.getJettyAcceptors() + "</li>")
				.getBytes());
		out
		.write(("<li>Jetty Max pool size : " + this.config.getJettyThreadMaxPoolSize() + "</li>")
				.getBytes());
		out
		.write(("<li>Jetty Min pool size : " + this.config.getJettyThreadMinPoolSize() + "</li>")
				.getBytes());
		out.write(("<li>Services Context :    " + this.config.getServicesContext() + "</li>")
				.getBytes());
		out.write("</ul>".getBytes());

		out.write("<h2>Server Stats</h2>".getBytes());
		out.write("<ul>".getBytes());
		out.write(("<li>Start time : "
				+ new SimpleDateFormat().format(new Date()) + "</li>")
				.getBytes());
		out.write(("<li>Jetty Server version : " + Server.getVersion() + "</li>").getBytes());

		out.write("</ul>".getBytes());

		out.write("</body></html>".getBytes());

		out.flush();
		out.close();
	}

	private class ExecuteInOnlyExchange extends Thread {

		private final SOAPListenerImpl listener;

		private final Exchange exchange;

		public ExecuteInOnlyExchange(SOAPListenerImpl listener, Exchange exchange) {
			this.listener = listener;
			this.exchange = exchange;
		}

		@Override
		public void run() {
			try {
				// execute behaviour of endpoint
				if(this.listener.getEndpoint().getBehaviourClass() != null) {
					SoapDeploymentServlet.this.log.finest("execute behaviour");
					this.listener.getEndpoint().getBehaviour().execute(this.exchange);
				} else {
					SoapDeploymentServlet.this.log.finest("execute empty behaviour");
				}
			} catch (BusinessException e) {
				// do nothing
				e.printStackTrace();
			} catch (ESBException e) {
				// do nothing
				e.printStackTrace();
			}
		}		
	}
}
