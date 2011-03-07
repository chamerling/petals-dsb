package org.ow2.petals.monitoring.model.rawreport.impl;

import java.io.Serializable;
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
import org.w3c.dom.Element;

import com.ebmwebsourcing.semeuse.org.rawreport.ReportType;

/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         We assume that (long) timeStampExpressedInMilliSec is 0 (zero).
 */

@SuppressWarnings("serial")
public class ReportImpl extends AbstractXMLElementImpl<ReportType> implements Report, Serializable {

	/**
	 * Use String sss = dateInGMT.toGMTString() to get the Date of the message
	 * arrival in the ESB;
	 * 
	 * Use Date dd = new Date(sss) to get back the Date object.
	 */
	private Object exception;

	
	
	
	public ReportImpl(ReportType model, AbstractXMLElementImpl parent) {
		super(model, parent);
	}


	public Date getDateInGMT() {
		Date res = null;
		if(this.model.getDateInGMT() != null) {
			res = this.model.getDateInGMT().toGregorianCalendar().getTime();
		}
		return res;
	}

	public void setDateInGMT(final Date dateInGMT_) throws RawReportException {
		if(dateInGMT_ != null) {
			try {
				GregorianCalendar gCalendar = new GregorianCalendar();
				gCalendar.setTime(dateInGMT_);

				XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);

				this.model.setDateInGMT(xmlCalendar);
			} catch (DatatypeConfigurationException e) {
				throw new RawReportException(e);
			}
		}
	}

	public String getConsumerName() {
		return this.model.getConsumerName();
	}

	public void setConsumerName(final String consumerName_) {
		this.model.setConsumerName(consumerName_);
	}

	public String getServiceName() {
		return this.model.getServiceName();
	}

	public void setServiceName(final String serviceName_) {
		this.model.setServiceName(serviceName_);
	}

	public String getOperationName() {
		return this.model.getOperationName();
	}

	public void setOperationName(final String operationName_) {
		this.model.setOperationName(operationName_);
	}

	public String getServiceProviderName() {
		return this.model.getServiceProviderName();
	}

	public void setServiceProviderName(final String serviceProviderName_) {
		this.model.setServiceProviderName(serviceProviderName_);
	}

	public String getEndPoint() {
		return this.model.getEndPoint();
	}

	public void setEndPoint(final String endPoint_) {
		this.model.setEndPoint(endPoint_);
	}

	public long getContentLength() {
		return this.model.getContentLength();
	}

	public void setContentLength(final long contentLength_) {
		this.model.setContentLength(contentLength_);
	}

	public boolean getDoesThisRequestInIsAnException() {
		return this.model.isDoesThisResponseInIsAnException();
	}

	public void setDoesThisRequestInIsAnException(
			final boolean doesThisSemeuseRequestInIsAnException_) {
		this.model.setDoesThisResponseInIsAnException(doesThisSemeuseRequestInIsAnException_);
	}

	public Object getException() {
		return this.model.getAny();
	}

	public void setException(final Object exception_) {
		this.model.setAny(exception_);
	}



	public void addOtherElements(Element arg0) {
		throw new UnsupportedOperationException();
	}

	public List<Element> getOtherElements() throws XmlException {
		throw new UnsupportedOperationException();
	}


	public int compareTo(Report o) {
	      return this.getDateInGMT().compareTo(o.getDateInGMT());
	}


	public String getExchangeId() {
		return this.model.getExchangeId();
	}


	public String getInterfaceName() {
		return this.model.getInterfaceName();
	}


	public void setExchangeId(String id) {
		this.model.setExchangeId(id);
	}


	public void setInterfaceName(String itf) {
		this.model.setInterfaceName(itf);
	}

}
