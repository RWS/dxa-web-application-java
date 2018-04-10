package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.joda.time.DateTime;

/**
 * @dxa.publicApi
 */
public interface Markup {

    String url(String path);

    String versionedContent(String path);

    String region(RegionModel region);

    String entity(EntityModel entity);

    String property(EntityModel entity, String fieldName);

    String property(EntityModel entity, String fieldName, int index);

    String resource(String key);

    String formatDate(DateTime dateTime);

    String formatDateDiff(DateTime dateTime);

    String formatMessage(String pattern, Object... args);

    String replaceLineEndsWithHtmlBreaks(String text);

    String siteMapList(SitemapItem item);

    WebRequestContext getWebRequestContext();
}
