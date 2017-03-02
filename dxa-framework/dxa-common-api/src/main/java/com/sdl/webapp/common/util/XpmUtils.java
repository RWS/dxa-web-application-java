package com.sdl.webapp.common.util;

import com.google.common.collect.ImmutableMap;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY;
import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY;
import static com.sdl.webapp.common.api.model.region.RegionModelImpl.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY;

public final class XpmUtils {

    private XpmUtils() {
    }

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
            return ImmutableMap.<String, Object>builder()
                    .put("ComponentID", componentId)
                    .put("ComponentModified", printDate(componentModified))
                    .put("ComponentTemplateID", componentTemplateID)
                    .put("ComponentTemplateModified", printDate(componentTemplateModified))
                    .put("IsRepositoryPublished", isRepositoryPublished)
                    .build();
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
            return ImmutableMap.<String, Object>builder()
                    .put("PageID", pageID)
                    .put("PageModified", printDate(pageModified))
                    .put("PageTemplateID", pageTemplateID)
                    .put("PageTemplateModified", printDate(pageTemplateModified))
                    .put("CmsUrl", cmsUrl)
                    .build();
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
            return ImmutableMap.<String, Object>builder()
                    .put(INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY, includedFromPageID)
                    .put(INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY, includedFromPageTitle)
                    .put(INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY, includedFromPageFileName)
                    .build();
        }
    }

}
