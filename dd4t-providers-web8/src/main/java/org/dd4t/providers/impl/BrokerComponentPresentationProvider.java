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

package org.dd4t.providers.impl;

import com.sdl.web.api.broker.WebComponentPresentationFactoryImpl;
import com.sdl.web.api.dynamic.WebComponentPresentationFactory;
import com.tridion.dcp.ComponentPresentation;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.AbstractComponentPresentationProvider;
import org.dd4t.providers.ComponentPresentationProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to Dynamic Component Presentations stored in the Content Delivery database. It uses CD API to retrieve
 * raw DCP content from the database. Access to these objects is not cached, and as such must be cached externally.
 */
public class BrokerComponentPresentationProvider extends AbstractComponentPresentationProvider implements ComponentPresentationProvider {

    private static final Map<Integer, WebComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
     * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
     * <p/>
     * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
     * Do provide a templateId!</b>
     *
     * @param componentId   int representing the Component item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException if the requested DCP cannot be found
     */
    @Override
    public String getDynamicComponentPresentation (int componentId, int publicationId) throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentation(componentId, 0, publicationId);
    }

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
     *
     * @param componentId   int representing the Component item id
     * @param templateId    int representing the Component Template item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException if the requested DCP cannot be found
     */
    @Override
    public String getDynamicComponentPresentation (int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {

        WebComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);

        if (factory == null) {
            factory = new WebComponentPresentationFactoryImpl(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        ComponentPresentation result;
        String resultString;
        if (templateId != 0) {
            result = factory.getComponentPresentation(componentId, templateId);
        } else {
            result = factory.getComponentPresentationWithHighestPriority(componentId);
        }

        assertQueryResultNotNull(result,componentId,templateId,publicationId);
        resultString = result.getContent();

        if (!StringUtils.isEmpty(resultString)) {
            return decodeAndDecompressContent(resultString);
        }
        return null;
    }
}
