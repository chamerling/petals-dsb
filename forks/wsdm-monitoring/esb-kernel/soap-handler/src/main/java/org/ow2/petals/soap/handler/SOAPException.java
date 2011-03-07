package org.ow2.petals.soap.handler;

public class SOAPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SOAPException(String message, Throwable cause) {
		super(message, cause);
	}

	public SOAPException(String message) {
		super(message);
	}

	public SOAPException(Throwable cause) {
		super(cause);
	}

}
