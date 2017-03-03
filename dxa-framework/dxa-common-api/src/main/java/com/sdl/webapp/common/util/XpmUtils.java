package com.sdl.webapp.common.util;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY;
import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY;
import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY;

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
            return map;
        }
    }

    @Setter
    @Accessors(chain = true)
    public static final class RegionXpmBuilder implements XpmBuilder {

        private String includedFromPageID;

        private String includedFromPageTitle;

        private String includedFromPageFileName;

        @Override
        public Map<String, Object> buildXpm() {
            Map<String, Object> map = new HashMap<>();
            map.put(INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY, includedFromPageID);
            map.put(INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY, includedFromPageTitle);
            map.put(INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY, includedFromPageFileName);
            return map;
        }
    }

}
