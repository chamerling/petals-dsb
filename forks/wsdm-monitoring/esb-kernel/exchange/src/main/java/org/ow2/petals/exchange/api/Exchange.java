package org.ow2.petals.exchange.api;

import java.net.URI;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.SchemaElement;
import org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;

public interface Exchange extends SchemaElement {
	
	
	QName getName();
	
	void setName(QName name);
	
	UUID getUuid();
	
	void setUuid(UUID uuid);
	
	StatusType getStatus();
	
	void setStatus(StatusType status);

	PatternType getPattern();
	
	void setPattern(PatternType pattern);
	
	RoleType getRole();
	
	void setRole(RoleType role);
	
	QName getSource();
	
	void setSource(QName ce);
	
	QName getDestination();
	
	void setDestination(QName pe);
	
	Message getIn();
	
	Message getOut();
	
	Message getError();
	
	
	void setError(Exception e) throws ExchangeException;
	
	String getOperation();
	
	void setOperation(String operation);
	
	QName getInterface();
	
	void setInterface(QName itf);
	
	void copyValueOf(Exchange ex);
	
	
	/**
	 * Set the document base URI of this definition. Can be used to represent the origin of the Definition, and can be exploited when resolving relative URIs (e.g. in &lt;import&gt;s).
	 * 
	 * @param documentBaseURI
	 *            the document base URI of this definition
	 */
	public void setDocumentURI(URI documentBaseURI);

	/**
	 * Get the document base URI string of this definition.<br />
	 * Try to get the path for an opaque URI.
	 * 
	 * @return the document base URI string
	 * @see java.net.URI for the opaque URI definition
	 */
	public String getDocumentBaseURIString();

	public URI getDocumentURI();
	
	NamespaceMapperImpl getAllNamespaces();
}
