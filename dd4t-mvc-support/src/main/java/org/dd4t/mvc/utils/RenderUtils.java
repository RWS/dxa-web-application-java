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

package org.dd4t.mvc.utils;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.RenderException;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
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
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RenderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RenderUtils.class);

    private RenderUtils () {

    }

    /**
     * If the component template has a template parameter viewName, the value of
     * that parameter is returned. Otherwise the title of the component template
     * is returned.
     */
    public static String getViewName (ComponentPresentation cp) {
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
     * Filters the list of component presentations based on the processors provided
     * by schemaName and templateName. To align with the .Net variant of DD4T
     * the schema and template matches uses the startsWith method and there also
     * is support for the negation of the expression.
     */
    public static List<ComponentPresentation> filterComponentPresentations (final List<ComponentPresentation> componentPresentations, final String schemaName, final String rootElementName, final String viewName, final String region) {
        boolean regionIsSet = false;

        if (region != null) {
            regionIsSet = true;
            LOG.debug("Fetching component presentations for region: {}", region);
        } else {
            LOG.debug("Component Presentations By Region is called, but no region is set.");
            LOG.debug("Removing component presentations which do have a region set.");
        }

        final List<ComponentPresentation> componentPresentationsToRender = new ArrayList<>();

        if (StringUtils.isNotEmpty(schemaName) || StringUtils.isNotEmpty(rootElementName) || StringUtils.isNotEmpty(viewName)) {


            for (ComponentPresentation cp : componentPresentations) {
                String componentSchema = cp.getComponent().getSchema().getTitle();
                String componentRootElement = cp.getComponent().getSchema().getRootElement();
                String view = getViewName(cp);

                if (match(componentSchema.toLowerCase(), schemaName) &&
                        match(componentRootElement, rootElementName) &&
                        match(view, viewName) && isComponentPresentationInRegion(region, regionIsSet, cp)) {
                    componentPresentationsToRender.add(cp);
                }
            }

        } else {
            for (ComponentPresentation cp : componentPresentations) {
                if (isComponentPresentationInRegion(region, regionIsSet, cp)) {
                    componentPresentationsToRender.add(cp);
                }
            }
        }
        return componentPresentationsToRender;
    }

    private static boolean isComponentPresentationInRegion (final String region, final boolean regionIsSet, final ComponentPresentation cp) {
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
                    return false;
                } else {
                    return true;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("This is a CP with a Region, but there is no region configured on the Tag.");
                    LOG.debug("Not rendering CP: {}-{}", cp.getComponent().getId(), cp.getComponentTemplate().getId());
                    for (Object regionValue : currentRegion.getValues()) {
                        LOG.debug("With Region: {}", regionValue.toString());
                    }
                }
                return false;
            }
        } else {
            LOG.debug("No Region set on CP: {} - {}", cp.getComponent() != null ? cp.getComponent().getId() : "null", cp.getComponentTemplate().getId());
            if (regionIsSet) {
                LOG.debug("Not rendering this CP as a region is set on the Tag.");
                return false;
            } else {
                LOG.debug("Rendering as no region is set on either the Tag or the CP");
                return true;
            }
        }
    }

    private static boolean match (final String value, final String expressionParam) {
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
    public static String fixUrl (String url) {
        return url.replace(' ', '-').toLowerCase();
    }

    /**
     * Renders a list of component presentations.
     *
     * @param request                , the http request
     * @param response               , the http response
     * @param componentPresentations , the list with component presentations
     * @return as string with all component presentations rendered and concatenated.
     */
    public static String renderComponentPresentations (final HttpServletRequest request, final HttpServletResponse response, final List<ComponentPresentation> componentPresentations) throws FactoryException {
        final StringBuilder output = new StringBuilder();

        if (componentPresentations == null) {
            return output.toString();
        }

        for (ComponentPresentation cp : componentPresentations) {
            output.append(renderComponentPresentation(request, response, cp));
        }

        return output.toString();
    }

    /**
     * Renders a single component presentation using the dispatchers include method.
     *
     * @param request  the http request
     * @param response the http response
     * @param cp       the component presentation
     * @return the rendered component presentation as a string.
     */
    public static String renderComponentPresentation (final HttpServletRequest request, final HttpServletResponse response, final ComponentPresentation cp) throws FactoryException {
        return getResponse(request, response, cp, getViewName(cp));
    }

    /**
     * Render a dynamic component presentation based on component / ct uri and view name
     *
     * @param request      the current Http Request
     * @param response     the current Http Response
     * @param componentURI the Tcm Uri of the component
     * @param templateURI  the Tcm Uri of the template
     * @param viewName     the view name
     */
    public static String renderDynamicComponentPresentation (final HttpServletRequest request, final HttpServletResponse response, final String componentURI, final String templateURI, final String viewName) throws FactoryException {
        final ComponentPresentation componentPresentation = ComponentPresentationFactoryImpl.getInstance().getComponentPresentation(componentURI, templateURI);
        return getResponse(request, response, componentPresentation, viewName);
    }

    /**
     * Dispatch a url to a request dispatcher while buffering the output in a string
     * (and not directly to the response's writer).
     */
    public static String dispatchBufferedRequest (final HttpServletRequest request, final HttpServletResponse response, final String url) throws ServletException, IOException {
        long time = System.currentTimeMillis();
        final RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(url);
        final HttpServletResponse responseWrapper = new HttpServletResponseWrapper(response) {
            private CharArrayWriter output = new CharArrayWriter();

            @Override
            public String toString () {
                return output.toString();
            }

            @Override
            public PrintWriter getWriter () {
                return new PrintWriter(output);
            }
        };

        dispatcher.include(request, responseWrapper);
        time = System.currentTimeMillis() - time;
        LOG.debug("dispatchBufferedRequest {}, takes: {} ms.", url, time);
        return responseWrapper.toString();
    }

    private static String getResponse (final HttpServletRequest request, final HttpServletResponse response, final ComponentPresentation cp, final String viewName) throws FactoryException {
        try {
            final TCMURI tcmuri = new TCMURI(cp.getComponent().getId());
            ComponentUtils.setComponentPresentation(request, cp);
            request.setAttribute(Constants.COMPONENT_TEMPLATE_ID, cp.getComponentTemplate().getId());
            request.setAttribute(Constants.DYNAMIC_COMPONENT_PRESENTATION, cp.isDynamic());
            String url = fixUrl(String.format(Constants.CONTROLLER_MAPPING_PATTERN, viewName, tcmuri.getItemId()));
            setViewModelsOnRequest(request, cp);
            return dispatchBufferedRequest(request, response, url);
        } catch (IOException | ParseException | ServletException e) {
            LOG.error(e.getMessage(), e);
            throw new RenderException(e);
        } finally {
            ComponentUtils.removeComponentPresentation(request);
            removeViewModelsFromRequest(request);
        }
    }

    public static void removeViewModelsFromRequest (final HttpServletRequest request) {
        final ComponentPresentation componentPresentation = ComponentUtils.getComponentPresentation(request);
        if (componentPresentation != null && componentPresentation.getAllViewModels() != null) {
            LOG.debug("Removing STM entries");
            for (final Map.Entry<String, BaseViewModel> modelEntry : componentPresentation.getAllViewModels().entrySet()) {
                request.removeAttribute(modelEntry.getKey());
            }
        }
    }

    public static void setViewModelsOnRequest (final HttpServletRequest request, final ComponentPresentation cp) {
        final Map<String, BaseViewModel> viewModels = cp.getAllViewModels();
        if (!viewModels.isEmpty()) {
            // Push all STMs on the request stack, so
            // that views can choose which ones to use
            for (final Map.Entry<String, BaseViewModel> modelEntry : viewModels.entrySet()) {
                LOG.debug("Adding model with key: {} and type {} to the request stack", modelEntry.getKey(), modelEntry.getValue());
                request.setAttribute(modelEntry.getKey(), modelEntry.getValue());
            }
        }
    }

    public static void setDynamicComponentOnRequest (final HttpServletRequest request, final Component component) {
        request.setAttribute(Constants.COMPONENT_NAME, component);
    }
}
