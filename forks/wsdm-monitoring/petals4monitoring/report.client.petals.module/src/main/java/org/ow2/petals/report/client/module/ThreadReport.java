package org.ow2.petals.report.client.module;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportWriter;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.ow2.petals.soap.handler.SOAPException;
import org.ow2.petals.soap.handler.SOAPSender;
import org.ow2.petals.util.LoggingUtil;
import org.w3c.dom.Document;

public class ThreadReport extends Thread {

	/**
	 * Logger wrapper.
	 */
	private LoggingUtil log;
	
	private String address;
	
	private ReportList reportList = null;

	private SOAPSender sender = new SOAPSender();
	
	private static RawReportWriter writer;
	
	static {
		try {
			writer = RawReportFactory.getInstance().newRawReportWriter();
		} catch (RawReportException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			Document request = writer.getDocument(this.getReportList());
			Document soapRequest = createSOAPMessageRequest(request);
			System.out.println("Trace to send: \n" + XMLPrettyPrinter.prettyPrint(soapRequest));
			this.sender.sendSoapRequest(soapRequest, address);
		} catch (RawReportException e) {
			System.out.println("************************* WARNING IN SEND: " + e.getMessage());
			this.log.warning(e.getMessage());
			//e.printStackTrace();
		} catch (JDOMException e) {
			System.out.println("************************* WARNING IN SEND: " + e.getMessage());
			this.log.warning(e.getMessage());
			//e.printStackTrace();
		} catch (SOAPException e) {
			System.out.println("************************* WARNING IN SEND: " + e.getMessage());
			this.log.warning(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	private static Document createSOAPMessageRequest(Document msg) throws JDOMException {
		Document res = null;

		Element env = new Element("Envelope", Namespace.getNamespace("soap-env", "http://schemas.xmlsoap.org/soap/envelope/"));
		env.addNamespaceDeclaration(Namespace.getNamespace("xsd", "http://www.w3.org/1999/XMLSchema"));
		env.addNamespaceDeclaration(Namespace.getNamespace("xsi", "http://www.w3.org/1999/XMLSchema-instance"));
		org.jdom.Document jdom = new org.jdom.Document(env);

		Element body = new Element("Body", Namespace.getNamespace("soap-env", "http://schemas.xmlsoap.org/soap/envelope/"));
		env.addContent(body);
		
		
		DOMBuilder builder = new DOMBuilder();
		org.jdom.Document jdomDocument = builder.build(msg);

		body.addContent(((Element)jdomDocument.getRootElement()).detach());

		DOMOutputter converter = new DOMOutputter();
		res = converter.output(jdom);

		return res;
	}


	public LoggingUtil getLog() {
		return log;
	}


	public void setLog(LoggingUtil log) {
		this.log = log;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public ReportList getReportList() throws RawReportException {
		if(this.reportList == null) {
			this.reportList = RawReportFactory.getInstance().newReportList();
		}
		return reportList;
	}
	
	public void setReportList(ReportList reportList) {
		this.reportList = reportList;
	}

}
