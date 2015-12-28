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


import org.dd4t.mvc.controllers.AbstractComponentPresentationController;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class ComponentPresentationController extends AbstractComponentPresentationController {

	/**
	 * Renders the component/model, which must be put on the request using
	 * {@link ComponentUtils#setComponent}.
	 *
	 * @param componentViewName the viewName to be used to render this component
	 * @param componentId       the id as an int, not a tcm uri!
	 * @param request           the request on which the component must be present
	 * @return the view name to render
	 */
	@Override
	@RequestMapping (value = {"/{componentViewName}/{componentId}.dcp"}, method = {RequestMethod.GET, RequestMethod.HEAD})
	public String showComponentPresentation (@PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
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
	@Override
	@RequestMapping (value = {"/{componentViewPrefix}/{componentViewName}/{componentId}.dcp"}, method = {RequestMethod.GET, RequestMethod.HEAD})
	public String showComponentPresentation (@PathVariable final String componentViewPrefix, @PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
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
