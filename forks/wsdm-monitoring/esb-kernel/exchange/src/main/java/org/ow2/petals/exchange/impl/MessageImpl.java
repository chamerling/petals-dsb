package org.ow2.petals.exchange.impl;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.Documentation;
import org.ow2.easywsdl.schema.api.SchemaElement;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl;
import org.ow2.easywsdl.schema.api.extensions.SchemaLocatorImpl;
import org.ow2.petals.exchange.api.Body;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.exchange.api.Header;
import org.ow2.petals.exchange.api.Message;

import petals.ow2.org.exchange.BodyType;
import petals.ow2.org.exchange.ExchangeType;
import petals.ow2.org.exchange.HeaderType;
import petals.ow2.org.exchange.MessageType;

public class MessageImpl extends AbstractSchemaElementImpl<MessageType> implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Body body = null;
	
	private Header header = null; 

	public MessageImpl(MessageType msg, SchemaElement parent) {
		super(msg, (AbstractSchemaElementImpl) parent);
		
		if(this.model.getBody() != null) {
			this.body = new BodyImpl(this.model.getBody(), this);
		}
		
		if(this.model.getHeader() != null) {
			this.header = new HeaderImpl(this.model.getHeader(), this);
		}
	}



	public Body getBody() {
		if(this.body == null) {
			this.model.setBody(new BodyType());
			this.body = new BodyImpl(this.model.getBody(), this);
		}
		return this.body;
	}



	public Header getHeader() {
		if(this.header == null) {
			this.model.setHeader(new HeaderType());
			this.header = new HeaderImpl(this.model.getHeader(), this);
		}
		return this.header;
	}
	


}
