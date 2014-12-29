package org.dd4t.test.web.controller;


import org.dd4t.mvc.controllers.AbstractComponentController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class SpringComponentController extends AbstractComponentController {

	/**
	 * Renders the component/model, which must be put on the request using
	 * {@link ComponentUtils#setComponent}.
	 *
	 * @param componentViewName the viewName to be used to render this component
	 * @param componentId       the id as an int, not a tcm uri!
	 * @param request           the request on which the component must be present
	 * @return the view name to render
	 */
	@Override public String showComponentPresentation (@PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
		return super.showComponentPresentation(componentViewName, componentId, request);
	}

	/**
	 * Renders the component template response, the exact mapping needs to be
	 * determined.
	 *
	 * @param componentViewPrefix the prefix to use for the view
	 * @param componentViewName   the viewName to be used to render this component
	 * @param componentId         the id as an int, not a tcm uri!
	 * @param request             the request on which the component must be present
	 * @return the view name to render
	 */
	@Override public String showComponentPresentation (@PathVariable final String componentViewPrefix, @PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
		return super.showComponentPresentation(componentViewPrefix, componentViewName, componentId, request);
	}

	/**
	 * @return, the component view path
	 */
	@Override public String getComponentViewPath () {
		return super.getComponentViewPath();
	}

	/**
	 * @param componentViewPath , sets the component view path relative to the view resolver path
	 */
	@Override public void setComponentViewPath (final String componentViewPath) {
		super.setComponentViewPath(componentViewPath);
	}
}
