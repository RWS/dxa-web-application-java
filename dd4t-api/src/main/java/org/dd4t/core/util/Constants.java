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

package org.dd4t.core.util;

import org.joda.time.DateTime;

import java.util.TimeZone;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class Constants {
    public static final String PAGE_MODEL_KEY = "pageModel";
    // Key for dynamic component presentations, needed for Experience Manager
    public static final String DYNAMIC_COMPONENT_PRESENTATION = "dynamicComponentPresentation";
    // Key for component template id, needed for Exeprience Manager
    public static final String COMPONENT_TEMPLATE_ID = "componentTemplateId";
    public static final String CONTROLLER_MAPPING_PATTERN = "/%s/%s.dcp";
    public static final String PAGE_REQUEST_URI = "pageRequestURI";
    public static final String REFERER = "Referer";

    //TODO: should be configurable
    public static final String DEFAULT_PAGE = "index.html";
    public static final String SMART_INCLUDE_URL = "smartIncludeUrl";
    public static final String COMPONENT_NAME = "component";

    public static final DateTime THE_YEAR_ZERO = new DateTime(0, 1, 1, 0, 0);
    public static final String TCM_ZERO_URI = "tcm:0-0-0";
    public static final String HTTP_X_FORWARDED_FOR_HEADER = "HTTP_X_FORWARDED_FOR";
    public static final int UNKNOWN_PUBLICATION_ID = -1;

    public static final String MONITOR_SERVICE_INTERVAL= "monitor.service.interval";
    public static final String CACHE_EXPIRED_TTL = "cache.expired.ttl";
    public static final String CACHE_DEPENDENCY_TTL = "cache.dependency.ttl";
    public static final String CACHE_TTL = "cache.ttl";


    public static final String PROPERTY_STRIP_CONTEXT_PATH = "publication.resolving.strip.context.path";

    public static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";


    private Constants () {

    }
}
