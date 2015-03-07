package org.dd4t.core.util;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.databind.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * dd4t-2
 */
public class ComponentUtils {

	private static final String COMPONENT_PRESENTATION_NAME = "componentPresentation";
	private static final Logger LOG = LoggerFactory.getLogger(ComponentUtils.class);

	private ComponentUtils () {

	}

	/**
	 * Get the component from the request that has been set using
	 * {@link #setComponentPresentation}.
	 */
	public static ComponentPresentation getComponentPresentation (final HttpServletRequest request) {
		return (ComponentPresentation) request.getAttribute(COMPONENT_PRESENTATION_NAME);
	}

	public static void setComponentPresentation (final HttpServletRequest request, ComponentPresentation componentPresentation) {
		request.setAttribute(COMPONENT_PRESENTATION_NAME, componentPresentation);
		LOG.debug("Added Component Presentation with Component id {} and rootElementName '{}' to the request.", componentPresentation.getComponent().getId(), componentPresentation.getComponent().getSchema().getRootElement());
	}

	/**
	 * Remove the component and model from the request which have been set using
	 * {@link #setComponentPresentation}.
	 */
	public static void removeComponentPresentation (final HttpServletRequest request) {
		request.removeAttribute(COMPONENT_PRESENTATION_NAME);
	}

	public static Map<String,BaseViewModel> getViewModels (final HttpServletRequest request) {
		final ComponentPresentation componentPresentation = (ComponentPresentation) request.getAttribute(COMPONENT_PRESENTATION_NAME);
		if (componentPresentation != null) {
			return componentPresentation.getAllViewModels();
		}
		return null;
	}

	public static BaseViewModel getViewModel(String modelName) {
		final Map<String,BaseViewModel> viewModels = getViewModels(HttpUtils.getCurrentRequest());
		if (viewModels != null && !viewModels.isEmpty()) {
			return viewModels.get(modelName);
		}
		return null;
	}


	public static Object getViewModels () {
		return getViewModels(HttpUtils.getCurrentRequest());
	}
}
