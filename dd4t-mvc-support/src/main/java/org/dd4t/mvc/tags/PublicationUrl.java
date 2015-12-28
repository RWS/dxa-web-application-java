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

package org.dd4t.mvc.tags;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.mvc.utils.PublicationResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outputs the Publication Url or else an empty string
 *
 * @author R. Kempees
 */
public class PublicationUrl {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationUrl.class);

    public static String getPublicationUrl () {
        String publicationUrl = PublicationResolverFactory.getPublicationResolver().getPublicationUrl();
        if (!StringUtils.isEmpty(publicationUrl)) {
            if (publicationUrl.endsWith("/") && publicationUrl.length() > 1) {
                publicationUrl = publicationUrl.substring(0, publicationUrl.length() - 1);
            }
            LOG.debug("Returning publication URL: {}", publicationUrl);
            return publicationUrl.toLowerCase();
        }
        return "";
    }
}
