/**
 * easySchema - easyWSDL toolbox Platform.
 * Copyright (c) 2008,  eBM Websourcing
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.ow2.petals.exchange.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.EasyXMLFilter;
import org.ow2.easywsdl.schema.util.SourceHelper;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.exchange.api.ExchangeReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import petals.ow2.org.exchange.ExchangeType;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 * @author Gael Blondelle - EBM WebSourcing
 */
public class ExchangeReaderImpl implements ExchangeReader {

	private static Logger log = Logger.getLogger(ExchangeReaderImpl.class.getName());


	private ExchangeJAXBContext jaxbContext = null;


	/*
	 * Private object initializations
	 */
	public ExchangeReaderImpl() throws SchemaException {
		jaxbContext = new ExchangeJAXBContext();
	}



	/**
	 * @return the jaxbContext
	 */
	public JAXBContext getJaxbContext() {
		return this.jaxbContext.getJaxbContext();
	}

	public Exchange readExchange(final URI schemaURI) throws ExchangeException {
		InputSource source = null;
		try {
			source = new InputSource(getInputStream(schemaURI));
		} catch (SchemaException e) {
			throw new ExchangeException(e);
		}
		return this.readExchange(schemaURI, source);
	}



	public Exchange readExchange(final URI schemaURI, final InputSource source) throws ExchangeException {
		Exchange desc = null;

		try {
			log.fine("Loading " + schemaURI);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			EasyXMLFilter filter = new EasyXMLFilter(xmlReader);
			SAXSource saxSource = new SAXSource(filter, source);

			// TODO use SAX validation instead of JAXB validation
			// turn off the JAXB provider's default validation mechanism to
			// avoid duplicate validation
			// SchemaReaderImpl.getUnMarshaller().setValidating( false );

			// unmarshal
			final JAXBElement<ExchangeType> schemaBinding = this.jaxbContext.getJaxbContext().createUnmarshaller().unmarshal(saxSource, ExchangeType.class);

			final ExchangeType def = schemaBinding.getValue();

			desc = new ExchangeImpl(schemaURI, def, filter.getNamespaceMapper(), filter.getSchemaLocator());
		} catch (SAXException e) {
			throw new ExchangeException("Can not get exchange at: " + schemaURI, e);
		} catch (JAXBException e) {
			throw new ExchangeException("Can not get exchange at: " + schemaURI, e);
		}
		return desc;
	}

	public Exchange readExchange(Document doc) throws ExchangeException {
		Exchange desc = null;
		try {
			URI uri = null;
			if (doc.getDocumentURI() != null) {
				uri = new URI(doc.getDocumentURI());
			} else {
				uri = new URI(".");
			}
			InputSource source =  SourceHelper.convertDOMSource2InputSource(new DOMSource(doc));
			desc = this.readExchange(uri, source);
		} catch (URISyntaxException e) {
			throw new ExchangeException(e);
		} catch (XmlException e) {
			throw new ExchangeException(e);
		}
		return desc;
	}
	
	private static InputStream getInputStream(final URI uri)
    throws SchemaException {
        InputStream input = null;
        try {
            File f = null;
            if (uri.getPath() != null && uri.getScheme() == null) {
                f = new File(uri.getPath());
            }
            if (f != null) {
                input = new FileInputStream(f);
            } else {
                input = uri.toURL().openStream();
            }
        } catch (final MalformedURLException e) {
            throw new SchemaException("Can not get document at: " + uri, e);
        } catch (final IOException e) {
            throw new SchemaException("Can not get document at: " + uri, e);
        }
        return input;
    }

}
