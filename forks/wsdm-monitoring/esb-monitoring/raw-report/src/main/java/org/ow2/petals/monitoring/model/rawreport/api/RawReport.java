package org.ow2.petals.monitoring.model.rawreport.api;

import org.ow2.easywsdl.schema.api.XMLElement;


/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         Raw report : used to transfert a report from CoreMonitoringLayer to
 *         DataCollectorLayer.
 */
public interface RawReport extends XMLElement {

	public String getExchangeId();

	public void setExchangeId(final String exchangeId_);

	public Report getRequestIn();

	public void setRequestIn(final Report requestIn_);

	public Report getRequestOut();

	public void setRequestOut(final Report requestOut_);

	public Report getResponseIn();

	public void setResponseIn(final Report responseIn_);

	public Report getResponseOut();

	public void setResponseOut(final Report responseOut_);

	
}
