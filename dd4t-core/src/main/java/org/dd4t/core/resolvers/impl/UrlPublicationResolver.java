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

package org.dd4t.core.resolvers.impl;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.contentmodel.PublicationDescriptor;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees, Q. Slings
 */
public class UrlPublicationResolver implements PublicationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(UrlPublicationResolver.class);

    @Resource
    private PublicationProvider publicationProvider;
    private boolean useCdDynamic;

    /**
     * Gets the Publication TCMURI item id for the current request
     *
     * @return int representing the SDL Tridion Publication item id
     */
    @Override
    public int getPublicationId () {
        final HttpServletRequest request = HttpUtils.getCurrentRequest();

        if (this.useCdDynamic) {
            LOG.debug("Using cd_dynamic_conf.xml to determine publication Id");
            return publicationProvider.discoverPublicationByBaseUrl(HttpUtils.getOriginalFullUrl(request));
        } else {
            LOG.debug("Determining Pub Id on page URL.");
            return publicationProvider.discoverPublicationIdByPageUrlPath(HttpUtils.getOriginalFullUrl(request));
        }
    }

    /**
     * Gets the Publication Url property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Publication Url metadata property
     */
    @Override
    public String getPublicationUrl () {
        return publicationProvider.discoverPublicationUrl(getPublicationId());
    }

    /**
     * Gets the Publication Path property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Publication Path metadata property
     */
    @Override
    public String getPublicationPath () {
        return publicationProvider.discoverPublicationPath(getPublicationId());
    }

    /**
     * Gets the Images URL property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Images URL metadata property
     */
    @Override
    public String getImagesUrl () {
        return publicationProvider.discoverImagesUrl(getPublicationId());
    }

    /**
     * Gets the Images Path property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Images Path metadata property
     */
    @Override
    public String getImagesPath () {
        return publicationProvider.discoverImagesPath(getPublicationId());
    }

    /**
     * Gets the Page URL in the current Publication corresponding to the given generic URL
     *
     * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
     * @return String representing the current Publication URL followed by the given URL
     */
    @Override
    public String getLocalPageUrl (final String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String publicationUrl = publicationProvider.discoverPublicationUrl(getPublicationId());

        if (StringUtils.isNotEmpty(publicationUrl) && !url.startsWith(publicationUrl)) {
            return String.format("%s%s", publicationUrl, url.startsWith("/") ? url : "/" + url);
        }
        return url;
    }

    /**
     * Gets the Binary URL in the current Publication corresponding to the given generic URL
     *
     * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
     * @return String representing the current Publication URL followed by the given URL
     */
    @Override
    public String getLocalBinaryUrl (final String url) {
        String binaryUrl = publicationProvider.discoverImagesUrl(getPublicationId());
        return url.replaceFirst(binaryUrl, "");
    }

    /**
     * For use in the RS scenario.
     *
     * @return a publication descriptor
     */
    @Override
    public PublicationDescriptor getPublicationDescriptor () {
        return publicationProvider.getPublicationDescriptor(getPublicationId());
    }

    public void setPublicationProvider (final PublicationProvider publicationProvider) {
        this.publicationProvider = publicationProvider;
    }

    public Boolean useCdDynamic () {
        return useCdDynamic;
    }

    public void setUseCdDynamic (final String useCdDynamicValue) {
        this.useCdDynamic = Boolean.parseBoolean(useCdDynamicValue);
    }
}
