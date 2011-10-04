package org.petalslink.dsb.kernel.resources.service.utils;
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


import java.util.HashMap;
import java.util.Map;

import com.ebmwebsourcing.easybox.api.XmlContext;
import com.ebmwebsourcing.easybox.api.XmlContextFactory;
import com.ebmwebsourcing.easybox.api.XmlObjectReader;
import com.ebmwebsourcing.easybox.api.XmlObjectWriter;

public class SOAUtil {
	
	private Map<Framework, XmlContext> xmlContexts = null;

	private Map<Framework, ThreadLocal<XmlObjectWriter>> xmlwriters = null;

	private Map<Framework, ThreadLocal<XmlObjectReader>> xmlreaders = null;
	
	private static SOAUtil INSTANCE = null;

	
	private SOAUtil() {
	    xmlContexts = new HashMap<Framework, XmlContext>();
	    xmlwriters = new HashMap<Framework, ThreadLocal<XmlObjectWriter>>();
	    xmlreaders = new HashMap<Framework, ThreadLocal<XmlObjectReader>>();
	}
	
	public static SOAUtil getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SOAUtil();
		}
		return INSTANCE;
	}
 	
	public ThreadLocal<XmlObjectWriter> getWriter(Framework fw) {
	    ThreadLocal<XmlObjectWriter> xmlwriter = this.xmlwriters.get(fw);
	    if(xmlwriter == null) {
	        xmlwriter = new ThreadFrameworkLocal<XmlObjectWriter>(fw) {
	            protected XmlObjectWriter initialValue() {
	                return this.getXmlContext().createWriter();
	            }
	        };
	        this.xmlwriters.put(fw, xmlwriter);
	    }
	    
		return xmlwriter;
	}

	public ThreadLocal<XmlObjectReader> getReader(Framework fw) {
	    ThreadLocal<XmlObjectReader> xmlreader = this.xmlreaders.get(fw);
        if(xmlreader == null) {
            xmlreader = new ThreadFrameworkLocal<XmlObjectReader>(fw) {
                protected XmlObjectReader initialValue() {
                    return this.getXmlContext().createReader();
                }
            };
            this.xmlreaders.put(fw, xmlreader);
        }
        
        return xmlreader;
	}

	public XmlContext getXmlContext(Framework fw) {
	    XmlContext ctxt = this.xmlContexts.get(fw);
	    if(ctxt == null) {
	        ctxt = new XmlContextFactory().newContext();
	        this.xmlContexts.put(fw, ctxt);
	    }
		return ctxt;
	}


	private class ThreadFrameworkLocal<T> extends ThreadLocal<T> {
	    
	    private Framework fw = null;
	    
	    public ThreadFrameworkLocal(Framework fw) {
	        this.fw = fw;
	    }
	    
	    public XmlContext getXmlContext() {
	        return SOAUtil.getInstance().getXmlContext(this.fw);
	    }
	}
}
