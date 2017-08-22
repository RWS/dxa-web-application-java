package com.sdl.webapp.common.util;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class XpmUtils {

    private XpmUtils() {
    }

    @FunctionalInterface
    private interface XpmBuilder {

        Map<String, Object> buildXpm();

        default String printDate(DateTime dateTime) {
            return ISODateTimeFormat.dateHourMinuteSecond().print(dateTime);
        }
    }

    @Setter
    @Accessors(chain = true)
    public static final class EntityXpmBuilder implements XpmBuilder {

        private String componentId;

        private DateTime componentModified;

        private String componentTemplateID;

        private DateTime componentTemplateModified;

        private boolean isRepositoryPublished;

        @Override
        public Map<String, Object> buildXpm() {
            Map<String, Object> map = new HashMap<>();
            map.put("ComponentID", componentId);
            map.put("ComponentModified", printDate(componentModified));
            map.put("ComponentTemplateID", componentTemplateID);
            map.put("ComponentTemplateModified", printDate(componentTemplateModified));
            map.put("IsRepositoryPublished", isRepositoryPublished);
            map.values().removeIf(Objects::isNull);
            return map;
        }
    }

    @Setter
    @Accessors(chain = true)
    public static final class PageXpmBuilder implements XpmBuilder {

        private String pageID;

        private DateTime pageModified;

        private String pageTemplateID;

        private DateTime pageTemplateModified;

        private String cmsUrl;

        @Override
        public Map<String, Object> buildXpm() {
            Map<String, Object> map = new HashMap<>();
            map.put("PageID", pageID);
            map.put("PageModified", printDate(pageModified));
            map.put("PageTemplateID", pageTemplateID);
            map.put("PageTemplateModified", printDate(pageTemplateModified));
            map.put("CmsUrl", cmsUrl);
            map.values().removeIf(Objects::isNull);
            return map;
        }
    }

    @Setter
    @Accessors(chain = true)
    public static final class RegionXpmBuilder implements XpmBuilder {

        /**
         * The XPM metadata key used for the ID of the (Include) Page from which the Region originates.
         * Avoid using this in implementation code because it may change in a future release.
         */
        public static final String INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY = "IncludedFromPageID";

        /**
         * The XPM metadata key used for the title of the (Include) Page from which the Region originates.
         * Avoid using this in implementation code because it may change in a future release.
         */
        public static final String INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY = "IncludedFromPageTitle";

        /**
         * The XPM metadata key used for the file name of the (Include) Page from which the Region originates.
         * Avoid using this in implementation code because it may change in a future release.
         */
        public static final String INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY = "IncludedFromPageFileName";

        private String includedFromPageID;

        private String includedFromPageTitle;

        private String includedFromPageFileName;

        @Override
        public Map<String, Object> buildXpm() {
            Map<String, Object> map = new HashMap<>();
            map.put(INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY, includedFromPageID);
            map.put(INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY, includedFromPageTitle);
            map.put(INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY, includedFromPageFileName);
            map.values().removeIf(Objects::isNull);
            return map;
        }
    }

}
