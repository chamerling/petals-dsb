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
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportReader;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.w3c.dom.Document;

import com.ebmwebsourcing.semeuse.org.rawreport.RawReportType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportListType;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class RawReportReaderImpl implements RawReportReader {


	RawReportJAXBContext context = null;

    /*
     * Private object initializations
     */
    public RawReportReaderImpl() throws RawReportException {
        	context = new RawReportJAXBContext();
    }
    
    public RawReportReaderImpl(List<Class> addedObjectFactories) throws RawReportException {
    	context = new RawReportJAXBContext(addedObjectFactories);
    }

    private RawReportType convertStream2RawReport(final Source rawReportDescriptorStream)
    throws RawReportException {

        try {
            // TODO : Check if it is a Thread safe method
            final JAXBElement<RawReportType> rawReportBinding = this.context.getJaxbContext().createUnmarshaller()
            .unmarshal(rawReportDescriptorStream, RawReportType.class);

            return rawReportBinding.getValue();

        } catch (final JAXBException e) {
            throw new RawReportException(
                    "Failed to build Java bindings from WSDL descriptor XML document", e);
        }
    }

    
    private ReportListType convertStream2ReportList(final Source rawReportDescriptorStream)
    throws RawReportException {

        try {
            // TODO : Check if it is a Thread safe method
            final JAXBElement<ReportListType> rawReportBinding = this.context.getJaxbContext().createUnmarshaller()
            .unmarshal(rawReportDescriptorStream, ReportListType.class);

            return rawReportBinding.getValue();

        } catch (final JAXBException e) {
            throw new RawReportException(
                    "Failed to build Java bindings from WSDL descriptor XML document", e);
        }
    }



    public RawReport readRawReport(final Document rawReportDocument) throws RawReportException {
    	RawReport res = null;
    	RawReportType desc = this.convertStream2RawReport(new DOMSource(rawReportDocument));
    	res = new RawReportImpl(desc, null);
    	return res;
    }

    public ReportList readReportList(final Document rawReportDocument) throws RawReportException {
    	ReportList res = null;
    	ReportListType desc = this.convertStream2ReportList(new DOMSource(rawReportDocument));
    	res = new ReportListImpl(desc, null);
    	return res;
    }
}
