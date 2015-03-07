package org.dd4t.core.exceptions;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public abstract class FactoryException extends Exception {

	private static final long serialVersionUID = -1540633460800491579L;

	public FactoryException () {
		super();
	}

	public FactoryException (final String message) {
		super(message);
	}

	public FactoryException (final String message, final Throwable cause) {
		super(message, cause);
	}

	public FactoryException (final Throwable cause) {
		super(cause);
	}
}
