package org.ow2.petals.transporter.impl.soap;

import javax.xml.namespace.QName;

import org.ow2.petals.transporter.api.transport.TransportContext;

public class SOAPTransportContext implements TransportContext {

	QName nodeName = null;
	
	String httpAddress = null;

	public QName getNodeName() {
		return nodeName;
	}

	public void setNodeName(QName nodeName) {
		this.nodeName = nodeName;
	}

	public String getHttpAddress() {
		return httpAddress;
	}

	public void setHttpAddress(String httpAddress) {
		this.httpAddress = httpAddress;
	}
	

	
}
