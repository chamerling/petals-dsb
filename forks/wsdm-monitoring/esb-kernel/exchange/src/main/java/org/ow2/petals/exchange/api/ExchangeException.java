package org.ow2.petals.exchange.api;

public class ExchangeException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ExchangeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExchangeException(String message) {
		super(message);
	}

	public ExchangeException(Throwable cause) {
		super(cause);
	}
}
