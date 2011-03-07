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

import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportReader;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportWriter;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;

import com.ebmwebsourcing.semeuse.org.rawreport.RawReportType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportListType;
import com.ebmwebsourcing.semeuse.org.rawreport.ReportType;


/**
 * This class is a concrete implementation of the abstract class WSDMFactory.
 * Some ideas used here have been shamelessly copied from the wonderful JAXP and
 * Xerces work.
 *
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class RawReportFactoryImpl extends RawReportFactory {


    /**
     * Create a new instance of a WSDLReaderImpl.
     * @throws WSDMException 
     */
    @Override
    public RawReportReader newRawReportReader() throws RawReportException {
        final RawReportReader reader = new RawReportReaderImpl();
        return reader;
    }

    /**
     * Create a new instance of a WSDLWriterImpl.
     * @throws WSDMException 
     */
    @Override
    public RawReportWriter newRawReportWriter() throws RawReportException {
        final RawReportWriter writer = new RawReportWriterImpl();
        return writer;
    }
    
    @Override
    public RawReport newRawReport() throws RawReportException {
        return new RawReportImpl(new RawReportType(), null);
    }

	@Override
	public Report newReport() throws RawReportException {
		return new ReportImpl(new ReportType(), null);
	}

	@Override
	public ReportList newReportList() throws RawReportException {
		return new ReportListImpl(new ReportListType(), null);
	}

}
