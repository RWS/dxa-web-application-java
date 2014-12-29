package org.dd4t.core.util;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.impl.LabelServiceFactoryImpl;
import org.dd4t.core.services.LabelService;
import org.dd4t.databind.util.DataBindConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO: totally redo component loading..
 */
public class RenderUtils {

	private static final Logger LOG = LoggerFactory.getLogger(RenderUtils.class);
	private static LabelService labelService = null;

	private RenderUtils () {

	}


	public static LabelService getLabelService() {
		if (labelService == null) {
			labelService = LabelServiceFactoryImpl.getInstance().getLabelService();
		}

		return labelService;
	}

	/**
	 * If the component template has a template parameter viewName, the value of
	 * that parameter is returned. Otherwise the title of the component template
	 * is returned.
	 */
	public static String getViewName(ComponentPresentation cp) {
		ComponentTemplate componentTemplate = cp.getComponentTemplate();
		Map<String, Field> metadata = componentTemplate.getMetadata();

		if (metadata != null && metadata.containsKey(DataBindConstants.VIEW_MODEL_DEFAULT_META_KEY)) {
			String viewName = (String) metadata.get(DataBindConstants.VIEW_MODEL_DEFAULT_META_KEY).getValues().get(0);
			if (StringUtils.isNotEmpty(viewName)) {
				return viewName.toLowerCase();
			}
		}

		return componentTemplate.getTitle().toLowerCase();
	}

	/**
	 * If the component template has a template parameter cache, the value of
	 * that parameter is returned as boolean. Otherwise, it returns true.
	 */
	private static boolean getCacheStatus(ComponentPresentation cp) {
		ComponentTemplate componentTemplate = cp.getComponentTemplate();
		Map<String, Field> metadata = componentTemplate.getMetadata();

		if (metadata != null && metadata.containsKey("cache")) {
			String useCache = (String) metadata.get("cache").getValues().get(0);
			if (StringUtils.isNotEmpty(useCache)) {
				return !"false".equalsIgnoreCase(useCache);
			}
		}

		return true;
	}

	/**
	 * Filters the list of component presentations based on the filters provided
	 * by schemaName and templateName. To align with the .Net variant of DD4T
	 * the schema and template matches uses the startsWith method and there also
	 * is support for the negation of the expression.
	 */
	public static List<ComponentPresentation> filterComponentPresentations(final List<ComponentPresentation> componentPresentations, final String schemaName, final String rootElementName, final String viewName) {
		if (StringUtils.isNotEmpty(schemaName) || StringUtils.isNotEmpty(rootElementName) || StringUtils.isNotEmpty(viewName)) {
			final List<ComponentPresentation> filtered = new ArrayList<>();

			for (ComponentPresentation cp : componentPresentations) {
				String componentSchema = cp.getComponent().getSchema().getTitle();
				String componentRootElement = cp.getComponent().getSchema().getRootElement();
				String view = getViewName(cp);

				if (match(componentSchema.toLowerCase(), schemaName) && match(componentRootElement, rootElementName) && match(view, viewName)) {
					filtered.add(cp);
				}
			}
			return filtered;
		} else {
			return componentPresentations;
		}
	}

	// TODO: merge with filterComponentPresentations
	public static List<ComponentPresentation> filterComponentPresentationsByRegion(List<ComponentPresentation> filteredComponentPresentations, String region) {
		boolean regionIsSet = false;

		if (region != null) {
			regionIsSet = true;
			LOG.debug("Fetching component presentations for region: {}", region);
		} else {
			LOG.debug("Component Presentations By Region is called, but no region is set.");
			LOG.debug("Removing component presentations which do have a region set.");
		}

		final Iterator<ComponentPresentation> componentPresentations = filteredComponentPresentations.iterator();
		final List<ComponentPresentation> componentPresentationsToRender = new ArrayList<>();

		while (componentPresentations.hasNext()) {
			ComponentPresentation cp = componentPresentations.next();

			final Map<String, Field> metadata = cp.getComponentTemplate().getMetadata();
			final Field currentRegion = metadata != null ? metadata.get("region") : null;
			if (currentRegion != null && currentRegion.getValues() != null && !currentRegion.getValues().isEmpty()) {
				if (regionIsSet) {
					boolean removeCp = true;
					for (Object regionValue : currentRegion.getValues()) {
						if (regionValue.toString().equalsIgnoreCase(region)) {
							LOG.debug("CP Region matches configured region: {}", regionValue.toString());
							removeCp = false;
						}
					}
					if (removeCp) {
						LOG.debug("No matching region found. Not Rendering.");
					} else {
						componentPresentationsToRender.add(cp);
					}
				} else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("This is a CP with a Region, but there is no region configured on the Tag.");
						LOG.debug("Not rendering CP: {}-{}", cp.getComponent().getId(), cp.getComponentTemplate().getId());
						for (Object regionValue : currentRegion.getValues()) {
							LOG.debug("With Region: {}", regionValue.toString());
						}
					}
				}
			} else {
				LOG.debug("No Region set on CP: {} - {}", cp.getComponent().getId(), cp.getComponentTemplate().getId());
				if (regionIsSet) {
					LOG.debug("Not rendering this CP as a region is set on the Tag.");
				} else {
					LOG.debug("Rendering as no region is set on either the Tag or the CP");
					componentPresentationsToRender.add(cp);
				}
			}
		}
		return componentPresentationsToRender;
	}

	public static boolean match(final String value, final String expressionParam) {
		if (StringUtils.isEmpty(expressionParam)) {
			return true;
		}

		// Check for negation of the expressions, note that we trim first in
		// case the ! sign has space characters in front of it.

		String expression = expressionParam.trim();

		boolean inverseMatching = expression.startsWith("!");

		expression = inverseMatching ? expression.substring(1).trim() : expression;

		// Expression can be comma-separated

		String[] expressionParts = expression.split(",");

		// Start matching

		Boolean matchFound;

		if (inverseMatching) {
			// Inverse matching: return false when a match is found with any of
			// the expression parts
			matchFound = true;

			for (String exprPart : expressionParts) {
				if (value.equals(exprPart.trim())) {
					matchFound = false;
				}
			}
		} else {
			// Normal matching: return true when a match is found with any of
			// the expression parts

			matchFound = false;

			for (String exprPart : expressionParts) {
				if (value.equals(exprPart.trim())) {
					matchFound = true;
				}
			}
		}
		return matchFound;
	}

	/**
	 * Utility method to fix the url, by convention urls are lower case and all
	 * spaces are replaced by dashes (-)
	 *
	 * @param url , the original url
	 */
	public static String fixUrl(String url) {
		return url.replace(' ', '-').toLowerCase();
	}

	/**
	 * Utility method to fix a constant value (probably a CMS-able value from
	 * Tridion), so it can be used inside a URL: lower case and all spaces and
	 * underscores are replaced by dashes (-).
	 */
	public static String stringToDashCase(String value) {
		if (value == null) {
			return "";
		}
		return value.replaceAll("[^a-zA-Z0-9]", "-").replaceAll("([-]+)", "-").toLowerCase();
	}

	/**
	 * Renders a list of component presentations.
	 *
	 * @param request                , the http request
	 * @param response               , the http response
	 * @param componentPresentations , the list with component presentations
	 * @return as string with all component presentations rendered and concatenated.
	 */
	public static String renderComponentPresentations(final HttpServletRequest request, final HttpServletResponse response, final List<ComponentPresentation> componentPresentations) throws ItemNotFoundException, FactoryException {
		final StringBuilder buf = new StringBuilder();

		if (componentPresentations == null) {
			return "";
		}

		for (ComponentPresentation cp : componentPresentations) {
			buf.append(renderComponentPresentation(request, response, cp));
		}

		return buf.toString();
	}

	/**
	 * Renders a single component presentation using the dispatchers include method.
	 *
	 * @param request  , the http request
	 * @param response , the http response
	 * @param cp       , the component presentation
	 * @return the rendered component presentation as a string.
	 */
	public static String renderComponentPresentation(final HttpServletRequest request,
	                                                 final HttpServletResponse response,
	                                                 final ComponentPresentation cp) throws ItemNotFoundException, FactoryException {
		return renderComponentPresentation(request, response, cp, true);
	}

	/**
	 * Renders a single component presentation using the dispatchers include method.
	 *
	 * @param request  , the http request
	 * @param response , the http response
	 * @param cp       , the component presentation
	 * @param useCache , flag indicating whether CP output will be cached. Default = true.
	 * @return the rendered component presentation as a string.
	 */
	public static String renderComponentPresentation(final HttpServletRequest request,
	                                                 final HttpServletResponse response,
	                                                 final ComponentPresentation cp,
	                                                 boolean useCache) throws ItemNotFoundException, FactoryException {

			return getResponse(request, response, cp, getViewName(cp));
	}

	/**
	 * Render a dynamic component presentation based on component and view name. The CT uri is looked up using the LabelService.
	 *
	 * @param request
	 * @param response
	 * @param componentURI
	 * @param viewName
	 * @return
	 */
	public static String renderComponentPresentation(final HttpServletRequest request, final HttpServletResponse response, final String componentURI, final String viewName) throws ItemNotFoundException {
		try {
			String componentTemplateURI = getLabelService().getViewLabel(viewName);

			return renderComponentPresentation(request, response, componentURI, componentTemplateURI, viewName);
		} catch (IOException | FactoryException e) {
			throw new ItemNotFoundException(e);
		}
	}

	/**
	 * Render a dynamic component presentation based on component / ct uri
	 *
	 * @param request
	 * @param response
	 * @param componentURI
	 * @param templateURI
	 * @param viewName
	 */
	public static String renderComponentPresentation(final HttpServletRequest request, final HttpServletResponse response, final String componentURI, final String templateURI, final String viewName) throws ItemNotFoundException, FactoryException {
		ComponentPresentation componentPresentation = ComponentUtils.createPlaceholderComponentPresentation(componentURI, templateURI, viewName);

		return getResponse(request, response, componentPresentation, viewName);
	}

	/**
	 * Dispatch a url to a request dispatcher while buffering the output in a string
	 * (and not directly to the response's writer).
	 */
	public static String dispatchBufferedRequest(final HttpServletRequest request, final HttpServletResponse response, final String url) throws ServletException, IOException {
		long time = System.currentTimeMillis();
		RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(url);
		HttpServletResponse responseWrapper = new HttpServletResponseWrapper(response) {
			private CharArrayWriter output = new CharArrayWriter();

			@Override
			public String toString() {
				return output.toString();
			}

			@Override
			public PrintWriter getWriter() {
				return new PrintWriter(output);
			}
		};

		dispatcher.include(request, responseWrapper);
		time = System.currentTimeMillis() - time;
		LOG.debug("dispatchBufferedRequest {}, takes: {}s", url, time / 1000f);
		return responseWrapper.toString();
	}

	private static String getResponse(final HttpServletRequest request, final HttpServletResponse response,
	                                  final ComponentPresentation cp, final String viewName) throws ItemNotFoundException, FactoryException {
		try {
			TCMURI tcmuri = new TCMURI(cp.getComponent().getId());
			ComponentUtils.setComponent(request, cp);

			request.setAttribute(Constants.COMPONENT_TEMPLATE_ID, cp.getComponentTemplate().getId());
			request.setAttribute(Constants.DYNAMIC_COMPONENT_PRESENTATION, cp.isDynamic());
			String url = fixUrl(String.format(Constants.CONTROLLER_MAPPING_PATTERN, viewName, tcmuri.getItemId()));

			return dispatchBufferedRequest(request, response, url);
		} catch (IOException | ParseException | ServletException e) {
			LOG.error(e.getMessage(), e);
			throw new FactoryException(e);
		} finally {
			ComponentUtils.removeComponent(request);
		}
	}
}
