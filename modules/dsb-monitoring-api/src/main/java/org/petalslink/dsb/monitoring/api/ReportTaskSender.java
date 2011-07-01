package org.petalslink.dsb.monitoring.api;

/**
 * @deprecated
 * @author chamerling
 * 
 */
public class ReportTaskSender {// implements Runnable {

    // /**
    // * Logger wrapper.
    // */
    // private final LoggingUtil log;
    //
    // private final String address;
    //
    // private final ReportList reportList;
    //
    // /**
    // * TODO = Need a pool to not being instanciated each time!
    // */
    // private static final SOAPSender sender = new SOAPSender();
    //
    // private static RawReportWriter writer;
    //
    // static {
    // try {
    // writer = RawReportFactory.getInstance().newRawReportWriter();
    // } catch (RawReportException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // /**
    // *
    // */
    // public ReportTaskSender(ReportList list, String address, LoggingUtil log)
    // {
    // this.address = address;
    // this.reportList = list;
    // this.log = log;
    // }
    //
    // public void run() {
    // try {
    // Document request = writer.getDocument(this.getReportList());
    // Document soapRequest = createSOAPMessageRequest(request);
    // if ((this.log != null) && this.log.isDebugEnabled()) {
    // this.log.debug("SOAP Report to send to '" + this.address + "' : \n"
    // + XMLPrettyPrinter.prettyPrint(soapRequest));
    // }
    // sender.sendSoapRequest(soapRequest, this.address);
    // } catch (Exception e) {
    // String message =
    // "Got an error while sending message to monitoring bus : "
    // + e.getMessage();
    // if (this.log.isDebugEnabled()) {
    // this.log.warning(message, e);
    // } else {
    // this.log.warning(message);
    // }
    // }
    // }
    //
    // private static Document createSOAPMessageRequest(Document msg) throws
    // JDOMException {
    // Document res = null;
    //
    // Element env = new Element("Envelope", Namespace.getNamespace("soap-env",
    // "http://schemas.xmlsoap.org/soap/envelope/"));
    // env.addNamespaceDeclaration(Namespace.getNamespace("xsd",
    // "http://www.w3.org/1999/XMLSchema"));
    // env.addNamespaceDeclaration(Namespace.getNamespace("xsi",
    // "http://www.w3.org/1999/XMLSchema-instance"));
    // org.jdom.Document jdom = new org.jdom.Document(env);
    //
    // Element body = new Element("Body", Namespace.getNamespace("soap-env",
    // "http://schemas.xmlsoap.org/soap/envelope/"));
    // env.addContent(body);
    //
    // DOMBuilder builder = new DOMBuilder();
    // org.jdom.Document jdomDocument = builder.build(msg);
    //
    // body.addContent((jdomDocument.getRootElement()).detach());
    //
    // DOMOutputter converter = new DOMOutputter();
    // res = converter.output(jdom);
    //
    // return res;
    // }
    //
    // public ReportList getReportList() throws RawReportException {
    // ReportList result = null;
    // if (this.reportList == null) {
    // result = RawReportFactory.getInstance().newReportList();
    // } else {
    // result = this.reportList;
    // }
    // return result;
    // }
}
