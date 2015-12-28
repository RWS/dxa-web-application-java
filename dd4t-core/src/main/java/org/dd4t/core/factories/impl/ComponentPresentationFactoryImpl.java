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
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.util.TCMURI;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class ComponentPresentationFactoryImpl extends BaseFactory implements ComponentPresentationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPresentationFactoryImpl.class);
    private static final ComponentPresentationFactoryImpl INSTANCE = new ComponentPresentationFactoryImpl();
    protected ComponentPresentationProvider componentPresentationProvider;

    protected ComponentPresentationFactoryImpl () {
        LOG.debug("Create new instance");
    }

    public static ComponentPresentationFactoryImpl getInstance () {
        return INSTANCE;
    }

    /**
     * Get the component by the component uri and template uri.
     * but NO CT rendering (so from the Dynamic Template Tab) should take place,
     * as this is done in a View
     * <p/>
     * Null values should be handled in the controller
     * <p/>
     * The Content Service should use ComponentPresentationFactory.getComponentPresentation(int publicationId, int componentId, int templateId)
     * OR
     * ComponentPresentationAssembler.getContent(int componentId, int componentTemplateId)
     * if we want to use REL linking. :)
     * Metadata should be added into the DD4T stack @ publishtime
     * Rendered output is cached in output cache
     *
     * @return the component
     * @throws org.dd4t.core.exceptions.FactoryException if no item found NotAuthorizedException if the user is not authorized to get the component
     */
    @Override
    public ComponentPresentation getComponentPresentation (String componentURI, String templateURI) throws FactoryException {
        LOG.debug("Enter getComponentPresentation with componentURI: {} and templateURI: {}", componentURI, templateURI);

        if (StringUtils.isEmpty(templateURI)) {
            throw new ItemNotFoundException("Provide a CT view or TCMURI");
        }

        final TCMURI componentTcmUri;
        final TCMURI templateTcmUri;
        try {
            componentTcmUri = new TCMURI(componentURI);
            templateTcmUri = new TCMURI(templateURI);
        } catch (ParseException e) {
            throw new ItemNotFoundException(e);
        }
        componentURI = componentTcmUri.toString();
        int publicationId = componentTcmUri.getPublicationId();
        int componentId = componentTcmUri.getItemId();
        int templateId = templateTcmUri.getItemId();

        String key = getKey(publicationId, componentId, templateId);
        CacheElement<ComponentPresentation> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);

        ComponentPresentation componentPresentation;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {

                    cacheElement.setExpired(false);
                    String rawComponentPresentation;
                    rawComponentPresentation = componentPresentationProvider.getDynamicComponentPresentation(componentId, templateId, publicationId);

                    if (rawComponentPresentation == null) {

                        cacheElement.setPayload(null);
                        cacheElement.setExpired(true);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        throw new ItemNotFoundException(String.format("Could not find DCP with componentURI: %s and templateURI: %s", componentURI, templateURI));
                    }

                    // Building STMs here.
                    componentPresentation = DataBindFactory.buildDynamicComponentPresentation(rawComponentPresentation, ComponentPresentationImpl.class);

                    LOG.debug("Running pre caching processors");
                    // TODO: support full CPs?
                    this.executeProcessors(componentPresentation.getComponent(), RunPhase.BEFORE_CACHING, getRequestContext());
                    cacheElement.setPayload(componentPresentation);
                    cacheProvider.storeInItemCache(key, cacheElement, publicationId, componentId);
                    LOG.debug("Added component with uri: {} and template: {} to cache", componentURI, templateURI);

                } else {
                    LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", componentURI, templateURI);
                    componentPresentation = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", componentURI, templateURI);
            componentPresentation = cacheElement.getPayload();
        }

        if (componentPresentation != null) {
            LOG.debug("Running Post caching Processors");
            try {
                this.executeProcessors(componentPresentation.getComponent(), RunPhase.AFTER_CACHING, getRequestContext());
            } catch (ProcessorException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        LOG.debug("Exit getComponentPresentation");
        return componentPresentation;
    }

    private String getKey (int publicationId, int componentId, int templateId) {
        return String.format("Component-%d-%d-%d", publicationId, componentId, templateId);
    }

    public ComponentPresentationProvider getComponentPresentationProvider () {
        return componentPresentationProvider;
    }

    public void setComponentPresentationProvider (ComponentPresentationProvider componentPresentationProvider) {
        this.componentPresentationProvider = componentPresentationProvider;
    }
}
