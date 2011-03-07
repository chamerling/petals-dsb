package org.ow2.petals.base.fractal.api;

public class FractalException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FractalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FractalException(Throwable cause) {
		super(cause);
	}

	public FractalException(String message) {
		super(message);
	}

}
