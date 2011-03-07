package org.ow2.petals.exchange.impl;

import org.ow2.easywsdl.schema.api.SchemaElement;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.petals.exchange.api.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.HeaderType;

public class HeaderImpl extends AbstractSchemaElementImpl<HeaderType> implements Header {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Document doc = null;

	public HeaderImpl(HeaderType msg, SchemaElement parent) {
		super(msg, (AbstractSchemaElementImpl) parent);
		if(this.model.getAny() != null) {
			doc = ((Element)this.model.getAny()).getOwnerDocument();
		}
	}



	public Document getContent() {
		return this.doc;
	}



	public void setContent(Document doc) {
		this.doc = doc;
		if(doc != null) {
			this.model.setAny(doc.getDocumentElement());
		}
	}




}
