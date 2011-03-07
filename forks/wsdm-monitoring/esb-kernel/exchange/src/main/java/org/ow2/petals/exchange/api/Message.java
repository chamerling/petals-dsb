package org.ow2.petals.exchange.api;

import org.ow2.easywsdl.schema.api.SchemaElement;


public interface Message extends SchemaElement {

	Header getHeader();
	
	Body getBody();
	
}
