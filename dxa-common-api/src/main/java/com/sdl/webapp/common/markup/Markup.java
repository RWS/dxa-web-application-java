package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.joda.time.DateTime;

public interface Markup {


    public String url(String path);

    public String versionedContent(String path);

    public String region(RegionModel region);

    public String entity(EntityModel entity);

    public String property(EntityModel entity, String fieldName);

    public String property(EntityModel entity, String fieldName, int index);

    public String resource(String key);

    public String formatDate(DateTime dateTime);

    public String formatDateDiff(DateTime dateTime);

    public String formatMessage(String pattern, Object... args);

    public String replaceLineEndsWithHtmlBreaks(String text);

    public String siteMapList(SitemapItem item);

}
