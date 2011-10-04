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
package org.petalslink.dsb.kernel.resources.service.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
    
    private DocumentBuilderFactory domBuilder = null;
    
    private static DOMUtil INSTANCE;
    
    private DOMUtil() {
        domBuilder = DocumentBuilderFactory.newInstance();
        domBuilder.setNamespaceAware(true);
    }
    
    
    public static DOMUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DOMUtil();
        }
        return INSTANCE;
    }

	public Element getFirstElement(Element parent) {
		Element res = null;
		if(parent != null) {
			NodeList list = parent.getChildNodes();
			for(int i = 0; i < list.getLength(); i++) {
				if(list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					res = (Element) list.item(i);
					break;
				}
			}
		}
		return res;
	}
	
	public Document createDocumentFromElement(Element elmt) throws ParserConfigurationException {
		Document doc = this.getDocumentBuilderFactory().newDocumentBuilder().newDocument();
		doc.appendChild((Element)doc.importNode(elmt.cloneNode(true), true));
		return doc;
	}
	
	public DocumentBuilderFactory getDocumentBuilderFactory() {
        return this.domBuilder;
    }


    public Document convertFirstElementIntoDocument(final DocumentBuilderFactory factory, final Document in)
	throws SOAException {
		Document res = null;
		try {
			res = factory.newDocumentBuilder()
			.newDocument();

			final Element first = this.getFirstElement(in.getDocumentElement());
			if (first != null) {
				res.appendChild(res.importNode(first.cloneNode(true), true));
			}
		} catch (ParserConfigurationException e) {
			throw new SOAException(e);
		}
		return res;
	}
}
