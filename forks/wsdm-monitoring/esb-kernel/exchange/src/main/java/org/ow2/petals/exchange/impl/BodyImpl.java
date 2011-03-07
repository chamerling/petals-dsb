package org.ow2.petals.exchange.impl;

import org.ow2.easywsdl.schema.api.SchemaElement;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.petals.exchange.api.Body;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.BodyType;

public class BodyImpl extends AbstractSchemaElementImpl<BodyType> implements Body {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Document doc = null;

	public BodyImpl(BodyType msg, SchemaElement parent) {
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
