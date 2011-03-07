/**
 * MonitoringEngine-Core - SOA Tools Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $id.java
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.monitoring.datacollector.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class RawReportTest extends TestCase {

	/**
	 * @param name
	 */
	public RawReportTest(final String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System
		.setProperty("javax.xml.transform.TransformerFactory",
		"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}



	public void testReaderRawReport() throws RawReportException, SAXException,
	IOException, ParserConfigurationException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
		.newInstance();
		builderFactory.setNamespaceAware(true);
		Document doc = builderFactory
		.newDocumentBuilder()
		.parse(
				new File(
						"./src/test/resources/example/rawreport/rawreport.xml"));

		RawReport rawReport = RawReportFactory.getInstance()
		.newRawReportReader().readRawReport(doc);

		assertNotNull(rawReport);
		System.out.println("rawReport : " + rawReport);

	}

	public void testReaderReportList() throws RawReportException, SAXException,
	IOException, ParserConfigurationException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
		.newInstance();
		builderFactory.setNamespaceAware(true);
		Document doc = builderFactory
		.newDocumentBuilder()
		.parse(
				new File(
				"./src/test/resources/example/rawreport/reportList.xml"));

		ReportList reportList = RawReportFactory.getInstance()
		.newRawReportReader().readReportList(doc);

		assertNotNull(reportList);
		assertEquals(3, reportList.getReports().size());
		System.out.println("rawReport : " + reportList);

	}
}
