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

package org.dd4t.core.factories.impl;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.caching.CacheElement;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.ComponentPresentationResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.ParseException;

public class ComponentPresentationFactoryImpl extends BaseFactory implements ComponentPresentationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPresentationFactoryImpl.class);
    @Resource
    protected ComponentPresentationProvider componentPresentationProvider;

    private static String getKey(int publicationId, int componentId, int templateId) {
        return String.format("Component-%d-%d-%d", publicationId, componentId, templateId);
    }

    public ComponentPresentationProvider getComponentPresentationProvider() {
        return componentPresentationProvider;
    }

    public void setComponentPresentationProvider(ComponentPresentationProvider componentPresentationProvider) {
        this.componentPresentationProvider = componentPresentationProvider;
    }

    @Override
    public ComponentPresentation getComponentPresentation(String componentURI) throws FactoryException {
        return getComponentPresentation(componentURI, null, null);
    }

    @Override
    public ComponentPresentation getComponentPresentation(String componentURI, String viewOrTemplateURI) throws
            FactoryException {
        return getComponentPresentation(componentURI, viewOrTemplateURI, null);
    }

    @Override
    public ComponentPresentation getComponentPresentation(String componentURI, RequestContext context) throws
            FactoryException {
        return getComponentPresentation(componentURI, null, context);
    }

    /**
     * Get the component by the component uri and template uri.
     * but NO CT rendering (so from the Dynamic Template Tab) should take place,
     * as this is done in a View
     * <p/>
     * Null values should be handled in the controller
     * <p/>
     * The Content Service should use ComponentPresentationFactory.getComponentPresentation(int publicationId, int
     * componentId, int templateId)
     * OR
     * ComponentPresentationAssembler.getContent(int componentId, int componentTemplateId)
     * if we want to use REL linking. :)
     * Metadata should be added into the DD4T stack @ publishtime
     * Rendered output is cached in output cache
     *
     * @return the component
     * @throws org.dd4t.core.exceptions.FactoryException if no item found NotAuthorizedException if the user is not
     * authorized to get the component
     */
    @Override
    public ComponentPresentation getComponentPresentation(String componentURI, String templateURI, RequestContext
            context) throws FactoryException {
        LOG.debug("Enter getComponentPresentation with componentURI: {} and templateURI: {}", componentURI,
                templateURI);

        int templateId = 0;
        final TCMURI componentTcmUri;
        final TCMURI templateTcmUri;

        try {
            componentTcmUri = new TCMURI(componentURI);
        } catch (ParseException e) {
            throw new ItemNotFoundException(e);
        }

        // if we have a valid uri use it, otherwise the 0 default will be handled by provider
        if (!StringUtils.isEmpty(templateURI)) {
            try {
                templateTcmUri = new TCMURI(templateURI);

                templateId = templateTcmUri.getItemId();

            } catch (ParseException e) {
                throw new ItemNotFoundException("Provide a valid TCMURI");
            }
        }

        String parsedComponentUri = componentTcmUri.toString();
        int publicationId = componentTcmUri.getPublicationId();
        int componentId = componentTcmUri.getItemId();

        String key = getKey(publicationId, componentId, templateId);
        CacheElement<ComponentPresentation> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);

        ComponentPresentation componentPresentation;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {

                    ComponentPresentationResultItem<String> result = componentPresentationProvider
                            .getDynamicComponentPresentationItem(componentId, templateId, publicationId);
                    String rawComponentPresentation = result.getSourceContent();

                    if (rawComponentPresentation == null) {
                        cacheElement.setPayload(null);
                        cacheElement.setNull(true);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        throw new ItemNotFoundException(String.format("Could not find DCP with componentURI: %s and " +
                                "templateURI: %s", parsedComponentUri, templateURI));
                    }

                    // Building STMs here.
                    componentPresentation = selectDataBinder(rawComponentPresentation).buildComponentPresentation
                            (rawComponentPresentation, ComponentPresentation.class);

                    // necessary for backwards compatibility to dd4t-1 (no way to get template URI otherwise)
                    componentPresentation.getComponentTemplate().setId(new TCMURI(result.getPublicationId(), result
                            .getTemplateId(), 32).toString());

                    LOG.debug("Running pre caching processors");
                    this.executeProcessors(componentPresentation.getComponent(), RunPhase.BEFORE_CACHING, context);
                    cacheElement.setPayload(componentPresentation);
                    cacheProvider.storeInItemCache(key, cacheElement, publicationId, componentId);
                    cacheElement.setExpired(false);
                    LOG.debug("Added component with uri: {} and template: {} to cache", parsedComponentUri, templateURI);

                } else {
                    LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", parsedComponentUri,
                            templateURI);
                    componentPresentation = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", parsedComponentUri,
                    templateURI);
            componentPresentation = cacheElement.getPayload();
        }

        if (componentPresentation != null) {
            LOG.debug("Running Post caching Processors");
            try {
                this.executeProcessors(componentPresentation.getComponent(), RunPhase.AFTER_CACHING, context);
            } catch (ProcessorException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        } else {
            throw new ItemNotFoundException("Found nullreference in DCP cache. Try again later.");
        }

        LOG.debug("Exit getComponentPresentation");
        return componentPresentation;
    }
}