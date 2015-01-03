package org.dd4t.mvc.controllers;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.impl.ComponentFactoryImpl;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.services.LabelService;
import org.dd4t.core.util.ComponentUtils;
import org.dd4t.core.util.RenderUtils;
import org.dd4t.core.util.TCMURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
/**
 * dd4t-2
 *
 * Extend this class in your own web project for default functionality.
 *
 * Do NOT add stuff here, as this will in the near future be loaded through maven only!
 *
 */
@Controller
public class AbstractComponentController {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentController.class);

	@Autowired private ComponentFactoryImpl componentFactory;

	@Autowired private PublicationResolver publicationResolver;

	@Autowired private LabelService labelService;

	private String componentViewPath = "";

	/**
	 * Renders the component/model, which must be put on the request using
	 * {@link ComponentUtils#setComponent}.
	 *
	 * @param componentViewName the viewName to be used to render this component
	 * @param componentId       the id as an int, not a tcm uri!
	 * @param request           the request on which the component must be present
	 * @return the view name to render
	 */
	@RequestMapping(value = {"/{componentViewName}/{componentId}.dcp"}, method = {RequestMethod.GET, RequestMethod.HEAD}) public String showComponentPresentation (@PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
		return showComponentPresentation(null, componentViewName, componentId, request);
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
	@RequestMapping(value = {"/{componentViewPrefix}/{componentViewName}/{componentId}.dcp"}, method = {RequestMethod.GET, RequestMethod.HEAD})
	public String showComponentPresentation (@PathVariable final String componentViewPrefix, @PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
		LOG.debug(">> {} component with viewPrefix: {}, viewName: {} and componentId: {}", new Object[]{request.getMethod(), componentViewPrefix, componentViewName, componentId});

		// double check the component is on the request - we are not actually doing something with it
		// TODO: REMOVE THIS IS WRONG!
		int publicationId = publicationResolver.getPublicationId();
		Component component = ComponentUtils.getComponent(request);

		if (component == null) {
			// In normal operation, this action is called from the server to render embedded component presentations.
			// In that case, the component should be present on the request already.
			// However, it is also possible to retrieve a DCP directly from the browser.
			LOG.debug("No component found in request.");
			try {
				component = componentFactory.getComponent(new TCMURI(publicationId, componentId, 16, 0).toString(), componentViewName);

			} catch (FactoryException e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}

		if (component == null) {
			// still no component, now we should fail
			throw new ResourceNotFoundException();
		}

		LOG.debug("Rendering component presentation with template '{}' and component id '{}'", componentViewName, componentId);

		if (StringUtils.isNotEmpty(componentViewPrefix)) {
			return getComponentViewName(componentViewPrefix + "/" + componentViewName);
		} else {
			return getComponentViewName(componentViewName);
		}
	}

	private String getComponentViewName (final String tridionName) {
		return RenderUtils.fixUrl(getComponentViewPath() + tridionName.trim());
	}

	/**
	 * @return, the component view path
	 */
	public String getComponentViewPath () {
		return componentViewPath;
	}

	/**
	 * @param componentViewPath , sets the component view path relative to the view resolver path
	 */
	public void setComponentViewPath (final String componentViewPath) {
		this.componentViewPath = componentViewPath;
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND) public class ResourceNotFoundException extends RuntimeException {
	}
}