package org.ow2.petals.esb.external.protocol.soap.impl;

import java.util.logging.Logger;

import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalSender;
import org.ow2.petals.soap.handler.SOAPException;
import org.ow2.petals.soap.handler.SOAPSender;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

public class SOAPSenderImpl implements ExternalSender {

	/**
	 * The logger
	 */
	private Logger log = Logger.getLogger(SOAPSenderImpl.class.getName());

	
	private SOAPSender sender = new SOAPSender();
	
	
	public SOAPSenderImpl() {
	}

	public Document sendSoapRequest(Document request, String address) throws TransportException {
		Document response = null;
		try {
			response = this.sender.sendSoapRequest(request, address);
		} catch (SOAPException e) {
			throw new TransportException(e);
		}
		return response;
	}


}
