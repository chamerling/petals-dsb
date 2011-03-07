package org.ow2.petals.esb.kernel.api.endpoint.behaviour;

public class MarshallerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MarshallerException(String message, Throwable cause) {
		super(message, cause);
	}

	public MarshallerException(String message) {
		super(message);
	}

	public MarshallerException(Throwable cause) {
		super(cause);
	}

}
