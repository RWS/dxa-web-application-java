package org.dd4t.core.exceptions;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class RenderException extends FactoryException {
	private static final long serialVersionUID = -4298178762605101264L;

	public RenderException () {
	}

	public RenderException (final String message) {
		super(message);
	}

	public RenderException (final String message, final Throwable cause) {
		super(message, cause);
	}

	public RenderException (final Throwable cause) {
		super(cause);
	}
}
