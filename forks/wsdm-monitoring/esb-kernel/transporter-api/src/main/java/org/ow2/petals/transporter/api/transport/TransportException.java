package org.ow2.petals.transporter.api.transport;

public class TransportException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransportException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransportException(String message) {
		super(message);
	}

	public TransportException(Throwable cause) {
		super(cause);
	}

}
