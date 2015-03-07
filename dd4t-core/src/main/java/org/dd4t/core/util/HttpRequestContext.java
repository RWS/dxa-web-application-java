package org.dd4t.core.util;

import org.dd4t.core.request.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class HttpRequestContext implements RequestContext {
	private HttpServletRequest httpServletRequest;

	public HttpRequestContext(Object request) {
		httpServletRequest = (HttpServletRequest) request;
	}
	@Override
	public Object getServletRequest () {
		return this.httpServletRequest;
	}
}
