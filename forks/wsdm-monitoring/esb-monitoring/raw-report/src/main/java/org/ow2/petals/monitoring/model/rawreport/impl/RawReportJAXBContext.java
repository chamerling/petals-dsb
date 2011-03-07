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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.SchemaFactory;

import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;



/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class RawReportJAXBContext {

    /**
     * The JAXB context
     */
    private JAXBContext jaxbContext;
    
    public static final String XSD_SCHEMA_RAWREPORT = "schema/rawreport/rawReport.xsd";


    private List<Class> defaultObjectFactories = new ArrayList<Class>(Arrays.asList(new Class[] {
            com.ebmwebsourcing.semeuse.org.rawreport.ObjectFactory.class
            }));

    /**
     * Private object initializations
     */
    public RawReportJAXBContext() throws RawReportException  {
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);


        
        try {
//            factory.newSchema(new StreamSource[] { 
//                    new StreamSource(schemaUrlAddressing.openStream()),
//                    new StreamSource(schemaUrlMuws1_2.openStream()), 
//                    new StreamSource(schemaUrlMuws2_2.openStream()), 
//                    new StreamSource(schemaUrlMows2.openStream()), 
//                    new StreamSource(schemaUrlPbm.openStream())});

            this.jaxbContext = JAXBContext.newInstance(defaultObjectFactories.toArray(new Class[defaultObjectFactories.size()]));

//        } catch (final SAXException e) {
//            throw new WSDMException(e);
//        } catch (final IOException e) {
//            throw new WSDMException(e);
        } catch (final JAXBException e) {
            throw new RawReportException(e);
        }
    }


    /**
     * Private object initializations
     */
    public RawReportJAXBContext(List<Class> addedObjectFactories) throws RawReportException {
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

//        final URL schemaUrlAddressing = WSDMJAXBContext.class.getResource("/" + XSD_SCHEMA_ADDRESSING);
//        final URL schemaUrlMuws1_2 = WSDMJAXBContext.class.getResource("/" + XSD_SCHEMA_MUWS1_2);
//        final URL schemaUrlMuws2_2 = WSDMJAXBContext.class.getResource("/" + XSD_SCHEMA_MUWS2_2);
//        final URL schemaUrlMows2 = WSDMJAXBContext.class.getResource("/" + XSD_SCHEMA_MOWS2);
//        final URL schemaUrlPbm = WSDMJAXBContext.class.getResource("/" + XSD_SCHEMA_PBM);
        
        List<Class> objectFactories = new ArrayList<Class>();
        objectFactories.addAll(defaultObjectFactories);
        if(addedObjectFactories != null) {
            objectFactories.addAll(addedObjectFactories);
        }

        try {
//            factory.newSchema(new StreamSource[] { 
//                    new StreamSource(schemaUrlAddressing.openStream()),
//                    new StreamSource(schemaUrlMuws1_2.openStream()), 
//                    new StreamSource(schemaUrlMuws2_2.openStream()), 
//                    new StreamSource(schemaUrlMows2.openStream()), 
//                    new StreamSource(schemaUrlPbm.openStream())});

            this.jaxbContext = JAXBContext.newInstance(objectFactories.toArray(new Class[objectFactories.size()]));

//        } catch (final SAXException e) {
//            throw new WSDMException(e);
//        } catch (final IOException e) {
//            throw new WSDMException(e);
        } catch (final JAXBException e) {
            throw new RawReportException(e);
        }

    }

    /**
     * @return the jaxbContext
     */
    public JAXBContext getJaxbContext() {
        return this.jaxbContext;
    }
}
