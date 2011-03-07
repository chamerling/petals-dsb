package org.ow2.petals.monitoring.model.rawreport.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractXMLElementImpl;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.w3c.dom.Element;

import com.ebmwebsourcing.semeuse.org.rawreport.ReportListType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportType;

/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         We assume that (long) timeStampExpressedInMilliSec is 0 (zero).
 */

@SuppressWarnings("serial")
public class ReportListImpl extends AbstractXMLElementImpl<ReportListType> implements ReportList, Serializable {


	private List<Report> reports = new ArrayList<Report>();
	
	
	public ReportListImpl(ReportListType model, AbstractXMLElementImpl parent) {
		super(model, parent);
		
		for(ReportType report: this.model.getReport()) {
			this.reports.add(new ReportImpl(report, this));
		}
	}

	public void addReport(Report report) {
		this.reports.add(report);
		((ReportListType)this.getModel()).getReport().add((ReportType) ((AbstractXMLElementImpl)report).getModel());
	}

	public List<Report> getReports() {
		return this.reports;
	}

	public void addOtherElements(Element elmt) {
		
	}

	public List<Element> getOtherElements() throws XmlException {
		return null;
	}


	@Override
	public String toString() {
		return this.model.toString();
	}

}
