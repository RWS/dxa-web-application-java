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

import com.tridion.broker.StorageException;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import org.dd4t.core.util.Constants;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends AbstractPublicationProvider implements PublicationProvider {
    private static final DynamicMetaRetriever DYNAMIC_META_RETRIEVER = new DynamicMetaRetriever();
    private static final PublicationMetaFactory PUBLICATION_META_FACTORY = new PublicationMetaFactory();
    private static final Logger LOG = LoggerFactory.getLogger(BrokerPublicationProvider.class);

    /**
     * Uses cd_dynamic to resolve publication Ids
     *
     * @param fullUrl the full url, including the host name
     * @return a publiction Id
     */
    @Override
    public int discoverPublicationByBaseUrl (final String fullUrl) {
        PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(fullUrl);

        if (publicationMapping != null) {
            return publicationMapping.getPublicationId();
        }

        LOG.info("Could not find publication Id for url: {}", fullUrl);
        return Constants.UNKNOWN_PUBLICATION_ID;
    }

    @Override
    protected PublicationMeta loadPublicationMetaByConcreteFactory (final int publicationId) {
        try {
            return PUBLICATION_META_FACTORY.getMeta(publicationId);
        } catch (StorageException e) {
            LOG.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    protected PageMeta loadPageMetaByConcreteFactory (final String url) {
        return DYNAMIC_META_RETRIEVER.getPageMetaByURL(url);
    }

    // TODO: add functionality to discover by start of publication path
}
