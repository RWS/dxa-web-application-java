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
    public static final String DEFAULT_PAGE = "index.html";
    public static final String SMART_INCLUDE_URL = "smartIncludeUrl";
    public static final String VIEW_NAME_FIELD = "viewName";
    public static final String COMPONENT_NAME = "component";

    public static final DateTime THE_YEAR_ZERO = new DateTime(0, 1, 1, 0, 0);
    public static final String TCM_ZERO_URI = "tcm:0-0-0";

    // Properties file constants

    public static final String MONITOR_SERVICE_INTERVAL= "monitor.service.interval";
    public static final String CACHE_EXPIRED_TTL = "cache.expired.ttl";
    public static final String CACHE_DEPENDENCY_TTL = "cache.dependency.ttl";
    public static final String CACHE_TTL = "cache.ttl";

    private Constants () {

    }
}
