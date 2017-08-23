#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ${package}.controllers;

import org.dd4t.contentmodel.Page;
import org.dd4t.mvc.controllers.AbstractPageController;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class PageController extends AbstractPageController {

	/**
	 * All page requests with the configured extensions are handled by this method.
	 *
	 * @param model
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = {"/**/*.html", "/**/*.txt", "/**/*.xml"}, method = {RequestMethod.GET})
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
