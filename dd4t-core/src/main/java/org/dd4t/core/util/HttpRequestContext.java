package org.dd4t.core.util;

import org.dd4t.core.request.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * Has become a duplicate of BasicRequestContext. TODO: merge
 *
 * @author R. Kempees
 */
public class HttpRequestContext implements RequestContext {
	private HttpServletRequest httpServletRequest;

	public HttpRequestContext() {
		httpServletRequest = HttpUtils.getCurrentRequest();
	}
	@Override
	public Object getRequest () {
		return this.httpServletRequest;
	}

	public boolean isUserInRole(String role) {
		return httpServletRequest.isUserInRole(role);
	}
}
