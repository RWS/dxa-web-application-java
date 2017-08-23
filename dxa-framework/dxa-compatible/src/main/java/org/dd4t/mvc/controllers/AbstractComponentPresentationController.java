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

package org.dd4t.mvc.controllers;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.util.TCMURI;
import org.dd4t.mvc.utils.ComponentUtils;
import org.dd4t.mvc.utils.RenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * dd4t-2
 * <p/>
 * Extend this class in your own web project for default functionality.
 * <p/>
 * Do not add stuff here, as this will be loaded as library only.
 * <p/>
 * Important Note: concrete implementing classes will need to add the
 * {@literal @RequestMapping} annotations!
 */
@Controller
public class AbstractComponentPresentationController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentPresentationController.class);

    @Resource
    private ComponentPresentationFactoryImpl componentPresentationFactory;

    private String componentViewPath = "";

    /**
     * Renders the component/model, which must be put on the request using
     * {@link ComponentUtils#setComponentPresentation}.
     *
     * @param componentViewName the viewName to be used to render this component
     * @param componentId       the id as an int, not a tcm uri!
     * @param request           the request on which the component must be present
     * @return the view name to render
     */
    public String showComponentPresentation (@PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
        return showComponentPresentation(null, componentViewName, componentId, request);
    }

    /**
     * Renders the component template response, the exact mapping needs to be
     * determined.
     * <p/>
     * TODO: split this logic. It is now used for two different things:
     * 1. For attempting to fetch a DCP which is on a Page
     * 2. Attempt to load a DCP based on the incoming URL. Which is something totally different, as we need the publication path
     * to actually do this.
     * <p/>
     * For now, if you use this controller to load DCPs, make sure that the componentViewPrefix variable is actually the publication path,
     * or simply extend this controller and be creative with the ComponentPresentationFactory and resolving Publication Ids
     *
     * @param componentViewPrefix the prefix to use for the view
     * @param componentViewName   the viewName to be used to render this component
     * @param componentId         the id as an int, not a tcm uri!
     * @param request             the request on which the component must be present
     * @return the view name to render
     */

    public String showComponentPresentation (@PathVariable final String componentViewPrefix, @PathVariable final String componentViewName, @PathVariable final int componentId, final HttpServletRequest request) {
        LOG.debug(">> {} component with viewPrefix: {}, viewName: {} and componentId: {}", new Object[]{request.getMethod(), componentViewPrefix, componentViewName, componentId});

        ComponentPresentation componentPresentation = ComponentUtils.getComponentPresentation(request);

        if (componentPresentation == null || componentPresentation.isDynamic()) {
            // In normal operation, this action is called from the server to render embedded component presentations.
            // In that case, the component should be present on the request already.
            // However, it is also possible to retrieve a DCP directly from the browser.

            if (componentPresentation != null) {
                try {

                    final TCMURI ctUri = new TCMURI(componentPresentation.getComponentTemplate().getId());
                    componentPresentation = componentPresentationFactory.getComponentPresentation(new TCMURI(ctUri.getPublicationId(), componentId, 16, 0).toString(), componentPresentation.getComponentTemplate().getId());

                } catch (FactoryException | ParseException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }

//			else {
//
//
//
//				int publicationId = PublicationResolverFactory.getPublicationResolver().getPublicationId();
//
//				// Depending on whether the incoming path actually is a publication path
//				if (publicationId <= 0) {
//					LOG.warn("Cannot get publication Id!");
//				} else {
//					componentPresentation = componentPresentationFactory.getComponentPresentation()
//				}
//			}
        }

        if (componentPresentation == null) {
            // still no component, now we should fail
            throw new ResourceNotFoundException();
        }

        RenderUtils.setDynamicComponentOnRequest(request, componentPresentation.getComponent());
        RenderUtils.setViewModelsOnRequest(request, componentPresentation);

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
     * @return String the component view path
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

    @ResponseStatus (value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
    }
}