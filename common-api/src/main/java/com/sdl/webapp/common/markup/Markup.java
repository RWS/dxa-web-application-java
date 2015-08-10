package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.joda.time.DateTime;

public interface Markup {


    public String url(String path);

    public String versionedContent(String path);

    public String region(Region region);

    public String entity(Entity entity);

    public String property(Entity entity, String fieldName);

    public String property(Entity entity, String fieldName, int index);

    public String resource(String key);

    public String formatDate(DateTime dateTime);

    public String formatDateDiff(DateTime dateTime);

    public String formatMessage(String pattern, Object... args);

    public String replaceLineEndsWithHtmlBreaks(String text);

    public String siteMapList(SitemapItem item);

}
