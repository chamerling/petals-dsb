/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.binding.soap.util;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SOAPTestUtil {

	public static final SOAPEnvelope loadSOAPEnvelope(String resourceName,
			int soapversion) throws Exception {
		Source source = loadSource(resourceName);
		final XMLStreamReader parser = StaxUtils.createXMLStreamReader(source);
		SOAPFactory factory = null;
		if (soapversion == 11) {
			factory = new SOAP11Factory();
		} else if (soapversion == 12) {
			factory = new SOAP12Factory();
		} else {
			throw new Exception("Unknow version " + soapversion);
		}

		StAXSOAPModelBuilder staxbuilder = OMXMLBuilderFactory
				.createStAXSOAPModelBuilder(factory, parser);
		return staxbuilder.getSOAPEnvelope();
	}

	private static final Source loadSource(String resourceName) {
		return new StreamSource(SOAPTestUtil.class.getClass()
				.getResourceAsStream(resourceName));
	}

}
