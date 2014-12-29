package org.dd4t.test.web.controller;

import org.dd4t.contentmodel.Page;
import org.dd4t.mvc.controllers.AbstractPageController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class SpringPageController extends AbstractPageController {

	/**
	 * All page requests are handled by this method. The page meta XML is
	 * queried based on the request URI, the page meta XML contains the actual
	 * view name to be rendered.
	 *
	 * @param model
	 * @param request
	 * @param response
	 */
	@Override public String showPage (final Model model, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		return super.showPage(model, request, response);
	}

	@Override public String getPageViewName (final Page page) {
		return super.getPageViewName(page);
	}

	/**
	 * @return the pageViewPrefix
	 */
	@Override public String getPageViewPath () {
		return super.getPageViewPath();
	}

	/**
	 * @param pageViewPath
	 */
	@Override public void setPageViewPath (final String pageViewPath) {
		super.setPageViewPath(pageViewPath);
	}

	@Override public boolean isRemoveContextPath () {
		return super.isRemoveContextPath();
	}

	@Override public void setRemoveContextPath (final boolean removeContextPath) {
		super.setRemoveContextPath(removeContextPath);
	}
}
