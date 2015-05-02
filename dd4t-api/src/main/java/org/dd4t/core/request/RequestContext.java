package org.dd4t.core.request;

/**
 *
 * TODO: update comment - CWA is sooo old ;)
 * Bean Interface describes the request which is given to CWA for retrieval. It will at the
 * minimum contain a reference to the actual servletrequest (with references to request parameters,
 * headers, etc), but is here mostly to be expanded in implementations.
 *
 * For instance, adding an authenticated User doing the request would be useful when determining
 * if said user has access to secure content - proving the security filter with whatever input
 * required to match authorisation to authentication supplied by a controller.
 *
 * @author rooudsho
 */

public interface RequestContext {
   Object getRequest ();
}
