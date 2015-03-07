package org.dd4t.core.request;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class AbstractRequestContext {

	protected Object request;

	public AbstractRequestContext(Object request) {
		this.request = request;
	}

	public AbstractRequestContext () {
	}
}
