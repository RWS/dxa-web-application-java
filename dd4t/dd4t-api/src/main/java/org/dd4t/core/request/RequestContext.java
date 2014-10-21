package org.dd4t.core.request;

import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;

import javax.servlet.http.HttpServletRequest;

/**
 * Bean Interface describes the request which is given to CWA for retrieval. It will at the
 * minimum contain a reference to the actual servletrequest (with references to request parameters,
 * headers, etc), but is here mostly to be expanded in implementations.
 * <p/>
 * For instance, adding an authenticated User doing the request would be usefull when determining
 * if said user has access to secure content - proving the security filter with whatever input
 * required to match authorisation to authentication supplied by a controller.
 *
 * @author rooudsho
 */
public interface RequestContext {

    public HttpServletRequest getServletRequest();

    public boolean isUserInRole(String role) throws NotAuthenticatedException;
}
