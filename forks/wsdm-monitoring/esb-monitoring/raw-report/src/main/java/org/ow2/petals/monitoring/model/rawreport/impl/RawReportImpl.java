package org.ow2.petals.monitoring.model.rawreport.impl;

import java.util.List;

import org.ow2.easywsdl.schema.api.XMLElement;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractXMLElementImpl;
import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.w3c.dom.Element;

import com.ebmwebsourcing.semeuse.org.rawreport.RawReportType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportType;

/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         Raw report : used to transfert a report from CoreMonitoringLayer to
 *         DataCollectorLayer.
 */
@SuppressWarnings("serial")
public class RawReportImpl extends AbstractXMLElementImpl<RawReportType> implements RawReport, XMLElement {

	private Report requestIn;
	private Report requestOut;
	private Report responseIn;
	private Report responseOut;



	public RawReportImpl(RawReportType model, AbstractXMLElementImpl parent) {
		super(model, parent);
				
		this.requestIn = new ReportImpl(this.model.getRequestIn(), this);
		this.requestOut = new ReportImpl(this.model.getRequestOut(), this);
		this.responseIn = new ReportImpl(this.model.getResponseIn(), this);
		this.responseOut = new ReportImpl(this.model.getResponseOut(), this);
	}



	@Override
	public String toString() {
		return this.model.toString();
	}



	public String getExchangeId() {
		return this.model.getExchangeId();
	}

	public void setExchangeId(final String messageId_) {
		this.model.setExchangeId(messageId_);
	}

	public Report getRequestIn() {
		return requestIn;
	}

	public void setRequestIn(final Report requestIn_) {
		this.model.setRequestIn((ReportType) ((AbstractXMLElementImpl)requestIn_).getModel());
		this.requestIn = requestIn_;
	}

	public Report getRequestOut() {
		return requestOut;
	}

	public void setRequestOut(final Report requestOut_) {
		this.model.setRequestOut((ReportType) ((AbstractXMLElementImpl)requestOut_).getModel());
		this.requestOut = requestOut_;
	}

	public Report getResponseIn() {
		return responseIn;
	}

	public void setResponseIn(final Report responseIn_) {
		this.model.setResponseIn((ReportType) ((AbstractXMLElementImpl)responseIn_).getModel());
		this.responseIn = responseIn_;
	}

	public Report getResponseOut() {
		return responseOut;
	}

	public void setResponseOut(final Report responseOut_) {
		this.model.setResponseOut((ReportType) ((AbstractXMLElementImpl)responseOut_).getModel());
		this.responseOut = responseOut_;
	}



	public void addOtherElements(Element arg0) {
		throw new UnsupportedOperationException();
	}

	public List<Element> getOtherElements() throws XmlException {
		throw new UnsupportedOperationException();
	}

}
