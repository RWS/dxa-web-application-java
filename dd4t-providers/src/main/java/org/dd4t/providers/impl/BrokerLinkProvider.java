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

import com.tridion.linking.ComponentLink;
import com.tridion.linking.Link;
import com.tridion.util.TCMURI;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.Constants;
import org.dd4t.providers.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider implementation to wrap around the ComponentLinker.
 * TODO: decompress!
 *
 * @author rooudsho, Mihai Cadariu
 */
public class BrokerLinkProvider extends BaseBrokerProvider implements LinkProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerLinkProvider.class);

    /**
     * Returns a link URL to the given Component TcmUri, if exists. Otherwise, returns null.
     *
     * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not linked to
     */
    @Override
    public String resolveComponent (String targetComponentURI) {
        try {
            TCMURI componentURI = new TCMURI(targetComponentURI);
            ComponentLink componentLink = new ComponentLink(componentURI.getPublicationId());
            Link link = componentLink.getLink(componentURI.getItemId());

            if (link.isResolved()) {
                return link.getURL();
            }
        } catch (Exception ex) {
            LOG.error("Unable to resolve link to " + targetComponentURI + ": " + ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Returns a link URL to the given Component TcmUri, when also specifying the source Page TcmUri.
     *
     * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
     * @param sourcePageURI      String representing the TcmUri of the source Page (the current page)
     * @return String representing the URL of the link; or null, if the Component is not linked to
     */
    @Override
    public String resolveComponentFromPage (String targetComponentURI, String sourcePageURI) {
        String link = getLinkAsString(sourcePageURI, targetComponentURI, Constants.TCM_ZERO_URI);
        if (StringUtils.isNotEmpty(link)) {
            return link;
        }
        return null;
    }

    /**
     * @param targetComponentUri          String representing the TcmUri of the Component to resolve a link to
     * @param excludeComponentTemplateUri The Component Presentation to exclude
     * @return String representing the URL of the link; or null, if the Component is not linked to
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    @Override
    public String resolveComponent (final String targetComponentUri, final String excludeComponentTemplateUri) throws ItemNotFoundException, SerializationException {
        String link = getLinkAsString(Constants.TCM_ZERO_URI, targetComponentUri, excludeComponentTemplateUri);
        if (StringUtils.isNotEmpty(link)) {
            return link;
        }
        return null;
    }

    private static String getLinkAsString (final String sourcePageUri, final String targetComponentUri, final String componentTemplateUri) {
        try {
            TCMURI componentURI = new TCMURI(targetComponentUri);

            ComponentLink componentLink = new ComponentLink(componentURI.getPublicationId());
            Link link = componentLink.getLink(sourcePageUri, targetComponentUri, componentTemplateUri, "", "", true, false);

            if (link.isResolved()) {
                return link.getURL();
            }
        } catch (Exception ex) {
            LOG.error("Unable to resolve link to " + targetComponentUri + ": " + ex.getMessage(), ex);
        }
        return null;
    }
}
