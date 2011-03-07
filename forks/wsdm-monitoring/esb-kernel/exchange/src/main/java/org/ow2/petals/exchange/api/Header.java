package org.ow2.petals.exchange.api;

import org.ow2.easywsdl.schema.api.SchemaElement;
import org.w3c.dom.Document;

public interface Header extends SchemaElement {

	Document getContent();
	
	void setContent(Document doc);
}
