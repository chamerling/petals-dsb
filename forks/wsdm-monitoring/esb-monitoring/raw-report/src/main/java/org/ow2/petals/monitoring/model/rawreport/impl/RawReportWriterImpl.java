/**
 * easyWSDL - SOA Tools Platform.
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

package org.ow2.petals.monitoring.model.rawreport.impl;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportWriter;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.w3c.dom.Document;

import com.ebmwebsourcing.semeuse.org.rawreport.ObjectFactory;
import com.ebmwebsourcing.semeuse.org.rawreport.RawReportType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportListType;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class RawReportWriterImpl implements RawReportWriter {


	private RawReportJAXBContext context = null;


	/*
	 * Private object initializations
	 */
	public RawReportWriterImpl() throws RawReportException {
		context = new RawReportJAXBContext();
	}

	public RawReportWriterImpl(List<Class> addedObjectFactories) throws RawReportException {
		context = new RawReportJAXBContext(addedObjectFactories);
	}

	/**
	 * Build the XML nodes from the WSDL descriptor in Java classes form.
	 *
	 * @param EndpointReferenceDescriptorClass
	 *            The EndpointReference Descriptor root class
	 * @param EndpointReferenceDescriptorNode
	 *            The XML Node to fill with the EndpointReference descriptor XML
	 *            nodes
	 */
	@SuppressWarnings("unchecked")
	private Document convertRawReport2DOMElement(final RawReportType rawReportDescriptor)
	throws RawReportException {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			ObjectFactory factory = new ObjectFactory();
			final JAXBElement element = factory.createRawReport(rawReportDescriptor);

			this.context.getJaxbContext().createMarshaller().marshal(element, doc);

		} catch (final JAXBException ex) {
			throw new RawReportException(
					"Failed to build XML binding from WSDL descriptor Java classes", ex);
		} catch (final ParserConfigurationException ex) {
			throw new RawReportException(
					"Failed to build XML binding from WSDL descriptor Java classes", ex);

		}
		return doc;
	}
	
	@SuppressWarnings("unchecked")
	private Document convertReportList2DOMElement(final ReportListType rawReportDescriptor)
	throws RawReportException {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			ObjectFactory factory = new ObjectFactory();
			final JAXBElement element = factory.createReportList(rawReportDescriptor);

			this.context.getJaxbContext().createMarshaller().marshal(element, doc);

		} catch (final JAXBException ex) {
			throw new RawReportException(
					"Failed to build XML binding from WSDL descriptor Java classes", ex);
		} catch (final ParserConfigurationException ex) {
			throw new RawReportException(
					"Failed to build XML binding from WSDL descriptor Java classes", ex);

		}
		return doc;
	}



	public Document getDocument(final RawReport rawReportDef) throws RawReportException {
		Document doc = null;
		if (rawReportDef != null) {
			doc = this.convertRawReport2DOMElement(((RawReportImpl) rawReportDef).getModel());
		}
		return doc;
	}

	public Document getDocument(ReportList reportListDef) throws RawReportException {
		Document doc = null;
		if (reportListDef != null) {
			doc = this.convertReportList2DOMElement(((ReportListImpl) reportListDef).getModel());
		}
		return doc;
	}

}
