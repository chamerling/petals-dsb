package org.ow2.petals.exchange.impl;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.ow2.easywsdl.schema.api.Documentation;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl;
import org.ow2.easywsdl.schema.api.extensions.SchemaLocatorImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.exchange.api.Message;

import petals.ow2.org.exchange.ExchangeType;
import petals.ow2.org.exchange.MessageType;
import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;

public class ExchangeImpl extends AbstractSchemaElementImpl<ExchangeType> implements Exchange {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the namespace context
	 */
	protected NamespaceMapperImpl namespaceMapper = new org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl();

	/**
	 * the baseUri string
	 */
	protected String documentBaseURIString;

	private URI documentURI = null;
	
	private Message in = null;
	
	private Message out = null;
	
	private Message error = null;
	
	private QName name = new QName(Constants.EXCHANGE_NAMESPACE, Constants.EXCHANGE_ROOT_TAG);

	public ExchangeImpl(URI schemaURI, ExchangeType def,
			NamespaceMapperImpl namespaceMapper,
			SchemaLocatorImpl schemaLocator) {
		super(def, null);
		this.namespaceMapper = namespaceMapper;
		this.documentURI = schemaURI;
		
		if(this.model.getIn() != null) {
			in = new MessageImpl(this.model.getIn(), this);
		}
		
		if(this.model.getOut() != null) {
			out = new MessageImpl(this.model.getOut(), this);
		}
		
		if(this.model.getError() != null) {
			error = new MessageImpl(this.model.getError(), this);
		}
	}

	public QName getDestination() {
		return this.model.getDestination();
	}

	public Message getError() {
		if(this.error == null) {
			this.error = new MessageImpl(new MessageType(), this);
		}
		return this.error;
	}

	public Message getIn() {
		if(this.in == null) {
			this.model.setIn(new MessageType());
			this.in = new MessageImpl(this.model.getIn(), this);
		}
		return this.in;
	}

	public QName getInterface() {
		return this.model.getInterfaceName();
	}

	public String getOperation() {
		return this.model.getOperation();
	}

	public Message getOut() {
		if(this.out == null) {
			this.model.setOut(new MessageType());
			this.out = new MessageImpl(this.model.getOut(), this);
		}
		return this.out;
	}

	public PatternType getPattern() {
		return this.model.getPattern();
	}

	public RoleType getRole() {
		return this.model.getRole();
	}

	public QName getSource() {
		return this.model.getSource();
	}

	public StatusType getStatus() {
		return this.model.getStatus();
	}

	public UUID getUuid() {
		return UUID.fromString(this.model.getUuid());
	}

	public void setDestination(QName pe) {
		this.model.setDestination(pe);
	}

	public void setError(Message msg) {
		if(msg != null) {
			this.model.setError((MessageType) ((AbstractSchemaElementImpl)msg).getModel());
			this.error = msg;
		} else {
			this.error = null;
		}
	}

	public void setError(Exception e) throws ExchangeException {
		throw new NotImplementedException();
	}

	public void setIn(Message msg) {
		if(msg != null) {
			this.model.setIn((MessageType) ((AbstractSchemaElementImpl)msg).getModel());
			this.in = msg;
		} else {
			this.in = null;
		}
	}

	public void setInterface(QName itf) {
		this.model.setInterfaceName(itf);
	}

	public void setOperation(String operation) {
		this.model.setOperation(operation);
	}

	public void setOut(Message msg) {
		if(msg != null) {
			this.model.setOut((MessageType) ((AbstractSchemaElementImpl)msg).getModel());
			this.out = msg;
		} else {
			this.out = null;
		}
	}

	public void setPattern(PatternType pattern) {
		this.model.setPattern(pattern);
	}

	public void setRole(RoleType role) {
		this.model.setRole(role);
	}

	public void setSource(QName ce) {
		this.model.setSource(ce);
	}

	public void setStatus(StatusType status) {
		this.model.setStatus(status);
	}

	public void setUuid(UUID uuid) {
		this.model.setUuid(uuid.toString());
	}

	public Documentation createDocumentation() {
		throw new UnsupportedOperationException();
	}

	public Documentation getDocumentation() {
		throw new UnsupportedOperationException();
	}

	public Map<QName, String> getOtherAttributes() throws XmlException {
		throw new UnsupportedOperationException();
	}

	public void setDocumentation(Documentation doc) {
		throw new UnsupportedOperationException();
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see org.ow2.easywsdl.schema.api.Schema#getAllNamespaces()
	 */
	public NamespaceMapperImpl getAllNamespaces() {
		return this.namespaceMapper;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfSchema#getDocumentBaseURIString()
	 */
	public String getDocumentBaseURIString() {
		if (documentURI != null) {
			if (documentURI.getRawPath() != null && documentURI.getRawPath().lastIndexOf("/") != -1) {
				return documentURI.getRawPath().substring(0, documentURI.getRawPath().lastIndexOf("/") + 1);
			} else if (documentURI.isOpaque() && documentURI.toString().lastIndexOf("/") != -1) {
				// trying to see if it's an "opaque URI"
				// e.g. 'jar:file:/path/to/jar/myjar.jar!/mydocument.wsdl',
				return documentURI.toString().substring(0, documentURI.toString().lastIndexOf("/") + 1);
			}
		}
		return documentBaseURIString;
	}

	public URI getDocumentURI() {
		return documentURI;
	}

	public void setDocumentURI(final URI documentURI) {
		this.documentURI = documentURI;
	}

	public QName getName() {
		return this.name;
	}

	public void setName(QName name) {
		this.name = name;
	}

	public void copyValueOf(Exchange ex) {
		this.setDestination(ex.getDestination());
		this.setError(ex.getError());
		this.setIn(ex.getIn());
		this.setInterface(ex.getInterface());
		this.setName(ex.getName());
		this.setOperation(ex.getOperation());
		this.setOut(ex.getOut());
		this.setPattern(ex.getPattern());
		this.setRole(ex.getRole());
		this.setSource(ex.getSource());
		this.setStatus(ex.getStatus());
	}

	@Override
	public String toString() {
		String res = null;
		if(this.model == null) {
			res = super.toString();
		} else {
			res = this.model.toString();
		}
		return res;
	}
	
	
}
