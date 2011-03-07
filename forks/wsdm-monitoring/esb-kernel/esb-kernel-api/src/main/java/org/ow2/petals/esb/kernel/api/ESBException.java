package org.ow2.petals.esb.kernel.api;

public class ESBException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ESBException(String message, Throwable cause) {
		super(message, cause);
	}

	public ESBException(String message) {
		super(message);
	}

	public ESBException(Throwable cause) {
		super(cause);
	}

}
