package org.ow2.petals.monitoring.model.rawreport.api;

import java.util.Date;

import org.ow2.easywsdl.schema.api.XMLElement;

/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         We assume that (long) timeStampExpressedInMilliSec is 0 (zero).
 */

public interface Report extends XMLElement, Comparable<Report> {


	public Date getDateInGMT();

	public void setDateInGMT(final Date dateInGMT_) throws RawReportException;

	public String getConsumerName();

	public void setConsumerName(final String consumerName_);

	public String getServiceName() ;

	public void setServiceName(final String serviceName_) ;

	public String getOperationName();

	public void setOperationName(final String operationName_);

	public String getServiceProviderName() ;

	public void setServiceProviderName(final String serviceProviderName_);
	
	public String getEndPoint();

	public void setEndPoint(final String endPoint_);

	public long getContentLength() ;

	public void setContentLength(final long contentLength_);

	public boolean getDoesThisRequestInIsAnException();

	public void setDoesThisRequestInIsAnException(
			final boolean doesThisRequestInIsAnException_);

	public Object getException();

	public void setException(final Object exception_);

	public String getExchangeId();
	
	public void setExchangeId(String id);
	
	public String getInterfaceName();
	
	public void setInterfaceName(String itf);

}
