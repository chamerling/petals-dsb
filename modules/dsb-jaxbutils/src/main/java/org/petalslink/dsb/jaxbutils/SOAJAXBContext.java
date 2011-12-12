/*******************************************************************************
 * Copyright (c) 2011 EBM Websourcing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     EBM Websourcing - initial API and implementation
 ******************************************************************************/
/**
 * easyWSDL - easyWSDL toolbox Platform.
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
package org.petalslink.dsb.jaxbutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;


/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class SOAJAXBContext {

	
	public static String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * The JAXB context
	 */
	private JAXBContext jaxbContext;


	protected final List<Class> defaultObjectFactories = new ArrayList<Class>(Arrays.asList(new Class[] {
			}));


	private List<Class> currentObjectFactories = new ArrayList<Class>(defaultObjectFactories);

	private static SOAJAXBContext instance = null;

	/**
	 * Private object initializations
	 */
	private SOAJAXBContext() throws SOAException {
		//TODO correct class conflict to pass to XMLConstants.W3C_XML_SCHEMA_NS_URI without bug
		//final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		SchemaFactory.newInstance(W3C_XML_SCHEMA_INSTANCE_NS_URI);

		// The EndpointReference parent resource is in the Jar where the class is loaded
		try {
			this.jaxbContext = JAXBContext
			.newInstance(currentObjectFactories.toArray(new Class[currentObjectFactories.size()]));

		} catch (final JAXBException e) {
			throw new SOAException(e);
		}

	}


	public static SOAJAXBContext getInstance() throws SOAException {
		if(instance == null) {
			instance = new SOAJAXBContext();
		}
		return instance;
	}



	public synchronized void addOtherObjectFactory(Class<?>... list) throws SOAException {
		if(list != null) {
			for(Class clazz: list) {
				if(!currentObjectFactories.contains(clazz)) {
					currentObjectFactories.add(clazz);
				}
			}
			try {
				this.jaxbContext = JAXBContext
				.newInstance(currentObjectFactories.toArray(new Class[currentObjectFactories.size()]));

			} catch (final JAXBException e) {
				throw new SOAException(e);
			}
		}
	}



	/**
	 * @return the jaxbContext
	 */
	public JAXBContext getJaxbContext() {
		return this.jaxbContext;
	}

	
	public <T> T marshallAnyType(Document doc, Class<T> clazz) throws SOAException {
		T res = null;
		try {
			Unmarshaller unmarshaller = SOAJAXBContext.getInstance().getJaxbContext().createUnmarshaller();
			javax.xml.bind.JAXBElement binding = (javax.xml.bind.JAXBElement) unmarshaller.unmarshal(doc, clazz);
			res = (T) binding.getValue();
		} catch (JAXBException e) {
			throw new SOAException(e);
		} 


		return res;
	}

	public <E> Document unmarshallAnyType(QName tag, E jaxbElmt, Class<E> clazz) throws SOAException {
		Document doc = null;
		try {
			doc = DOMUtil.getInstance().getDocumentBuilderFactory().newDocumentBuilder().newDocument();

			final javax.xml.bind.JAXBElement element = new javax.xml.bind.JAXBElement(tag, clazz, jaxbElmt);

			Marshaller marshaller = SOAJAXBContext.getInstance().getJaxbContext().createMarshaller();
			marshaller.marshal(element, doc);

		} catch (final JAXBException ex) {
			throw new SOAException(ex);
		} catch (final ParserConfigurationException ex) {
			throw new SOAException(ex);

		}
		return doc;
	}
	
	public Document unmarshallAnyElement(Object jaxbElmt) throws SOAException {
		Document doc = null;
		try {
			doc = DOMUtil.getInstance().getDocumentBuilderFactory().newDocumentBuilder().newDocument();

			Marshaller marshaller = SOAJAXBContext.getInstance().getJaxbContext().createMarshaller();
			marshaller.marshal(jaxbElmt, doc);

		} catch (final JAXBException ex) {
			throw new SOAException(ex);
		} catch (final ParserConfigurationException ex) {
			throw new SOAException(ex);

		}
		return doc;
	}

}
