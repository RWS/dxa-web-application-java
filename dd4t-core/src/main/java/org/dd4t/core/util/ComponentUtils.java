package org.dd4t.core.util;

import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dd4t-2
 */
public class ComponentUtils {

	public static final String COMPONENT_NAME = "component";
	private static final String VIEW_NAME_FIELD = "viewName";
	private static final String MODEL_ATTRIBUTE_NAME = "modelAttributeName";

	private static final Logger LOG = LoggerFactory.getLogger(ComponentUtils.class);
	private static ComponentFactory componentFactory = null;

	private ComponentUtils() {

	}

	public static ComponentFactory getComponentFactory () {
		if (componentFactory == null) {
			componentFactory = JspBeanContext.getBean(ComponentFactory.class);
		}
		return componentFactory;
	}

	/**
	 * Get the component from the request that has been set using
	 * {@link #setComponent}.
	 */
	public static Component getComponent (final HttpServletRequest request) {
		return (Component) request.getAttribute(COMPONENT_NAME);
	}

	/**
	 * Set the component and model on the request so it can be rendered.
	 * If rendering is complete, then remove the component and model using
	 * {@link #removeComponent}.
	 */
	public static void setComponent (final HttpServletRequest request, ComponentPresentation componentPresentation) throws ItemNotFoundException, FactoryException {
		resolveDynamicComponentPresentation(componentPresentation);
		Component component = componentPresentation.getComponent();
		Map<String, BaseViewModel> viewModels = componentPresentation.getAllViewModels();

		// TODO: fix this
		for (Map.Entry<String,BaseViewModel> viewModelEntry : viewModels.entrySet()) {
			LOG.debug(">> " + viewModelEntry.getKey());
		}

		setComponent(request, component);
	}

	public static void setComponent (final HttpServletRequest request, Component component) throws ItemNotFoundException {

		request.setAttribute(COMPONENT_NAME, component);




		String modelName = getRequestAttributeName((Component) component);

		request.setAttribute(MODEL_ATTRIBUTE_NAME, modelName);


		// TODO: wire to strong typed model
		request.setAttribute(modelName, component);
		LOG.debug("Adding typed model with name '{}' of type '{}' to the request.", modelName, component.getClass().getName());

	}

	public static void setModel (final HttpServletRequest request, Component component) throws ItemNotFoundException {
		// TODO: wire to strong typed model
		String modelName = component.getSchema().getRootElement();
		request.setAttribute(modelName, component);
		LOG.debug("Adding typed model with name '{}' of type '{}' to the request.", modelName, component.getClass().getName());
	}


	/**
	 * If this is a DCP, it resolves the Component inside it by fetching its model as Dynamic Component with the
	 * Component Template specified in the CP.
	 *
	 * @param componentPresentation ComponentPresentation to lookup the Component in
	 * @throws ItemNotFoundException if the Component is not published with the Dynamic CT
	 */
	public static void resolveDynamicComponentPresentation (ComponentPresentation componentPresentation) throws ItemNotFoundException, FactoryException {
		if (componentPresentation.isDynamic()) {

			Component component = componentPresentation.getComponent();
			String componentURI = component.getId();
			ComponentTemplate template = componentPresentation.getComponentTemplate();
			String templateURI = template.getId();

			component = getComponentFactory().getComponent(componentURI, templateURI);
			if (component == null) {
				throw new ItemNotFoundException("Cannot find Dynamic Component " + componentURI +
						" and Component Template " + templateURI);
			}

			componentPresentation.setComponent(component);
			// TODO: redo

		}
	}

	/**
	 * Remove the component and model from the request which have been set using
	 * {@link #setComponent}.
	 */
	public static void removeComponent (final HttpServletRequest request) {
		request.removeAttribute(COMPONENT_NAME);

		String modelName = (String) request.getAttribute(MODEL_ATTRIBUTE_NAME);

		request.removeAttribute(MODEL_ATTRIBUTE_NAME);
		if (modelName != null) {
			request.removeAttribute(modelName);
		}

		LOG.debug("Removing typed model with name '{}' to the request.", modelName);

	}

	public static Object getModel (final HttpServletRequest request) {
		String modelName = (String) request.getAttribute(MODEL_ATTRIBUTE_NAME);

		return request.getAttribute(modelName);

	}

	public static Object getModel () {
		return getModel(HttpUtils.currentRequest());
	}

	/**
	 * Returns the attribute name for the given component, if the component
	 * contains a rootElementName, the rootElementName will be used. By
	 * convention the attribute name will start with a lower case character, for
	 * example newsArticle, product etc.
	 */
	private static String getRequestAttributeName (final Component component) {
		String attributeName = TridionUtils.getRootElementName(component);

		/*
	     * Force the attribute name to start with a lowercase character.
		 * Although it is convention we cannot rely on the fact everyone follows
		 * the convention.
		 */
		return attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
	}

	/**
	 * Create a mock component presentation for the given component, CT uri and viewname
	 *
	 * @param componentURI
	 * @param templateURI
	 * @param viewName
	 */
	public static ComponentPresentation createPlaceholderComponentPresentation (String componentURI, String templateURI, String viewName) {
		// build component template mock
		List<String> values = new ArrayList<String>();
		values.add(viewName);

		TextField field = new TextField();
		field.setName(VIEW_NAME_FIELD);
		field.setTextValues(values);

		Map<String, Field> metadata = new HashMap<String, Field>();
		metadata.put(VIEW_NAME_FIELD, field);

		ComponentTemplate componentTemplate = new ComponentTemplateImpl();
		componentTemplate.setId(templateURI);
		componentTemplate.setMetadata(metadata);

		// build component mock
		ComponentImpl component = new ComponentImpl();
		component.setId(componentURI);

		// build component presentation mock
		ComponentPresentationImpl componentPresentation = new ComponentPresentationImpl();
		componentPresentation.setComponent(component);
		componentPresentation.setComponentTemplate(componentTemplate);
		componentPresentation.setIsDynamic(true);

		return componentPresentation;
	}
}
