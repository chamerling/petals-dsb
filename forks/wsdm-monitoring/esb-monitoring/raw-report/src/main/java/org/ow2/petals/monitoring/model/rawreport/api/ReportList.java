package org.ow2.petals.monitoring.model.rawreport.api;

import java.util.Date;
import java.util.List;

import org.ow2.easywsdl.schema.api.XMLElement;

/**
 * @author CHAZALET Antonin;
 * 
 *         Copyright (C) 2009 France Telecom R&D
 * 
 *         We assume that (long) timeStampExpressedInMilliSec is 0 (zero).
 */

public interface ReportList extends XMLElement {


	public List<Report> getReports();


	public void addReport(Report report);


}
